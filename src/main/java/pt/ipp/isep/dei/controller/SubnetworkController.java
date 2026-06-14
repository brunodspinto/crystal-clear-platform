package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.GraphDotExporter;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.domain.graph.SubnetworkExtractor;
import pt.ipp.isep.dei.domain.graph.SubnetworkExtractor.SubnetworkResult;
import pt.ipp.isep.dei.domain.graph.SupportGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US36 – Visualise the subnetwork where a given entity can
 * integrate chains of influence, to reduce the size of the network to be
 * analysed.
 *
 * <p>The subnetwork is extracted using only primitive operations
 * ({@link SubnetworkExtractor} — AC2). The visualisation is then delegated
 * to {@link GraphDotExporter}, which is exempt from the primitive-operations
 * constraint per the AC.</p>
 */
public class SubnetworkController {

    private final GraphRepository graphRepository;
    private SupportGraph cachedSupportGraph;

    /**
     * Creates a controller using the singleton repository.
     */
    public SubnetworkController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository. Used in tests.
     *
     * @param graphRepository the graph repository
     */
    public SubnetworkController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the list of all entity ids in the current relations graph.
     * Used by the UI to let the user choose the origin entity.
     *
     * @return list of entity ids
     * @throws IllegalStateException if no relations graph has been built yet
     */
    public List<String> getEntityIds() {
        List<String> ids = new ArrayList<>();
        RelationGraph relationGraph = requireRelationGraph();
        for (String id : relationGraph.nodes()) {
            ids.add(id);
        }
        return ids;
    }

    /**
     * Returns the entity ids active at the given snapshot date (US32 temporal
     * view), so the UI lists only the entities that exist on that date.
     *
     * @param date the snapshot date in yyyy-MM-dd format
     * @return list of entity ids active at that date; never null
     * @throws IllegalStateException    if no relations graph has been built yet
     * @throws IllegalArgumentException if the date is null or blank
     */
    public List<String> getEntityIds(String date) {
        SupportGraph graph = buildSupportGraphFor(date);
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < graph.size(); i++) {
            ids.add(graph.getRegistry().idAt(i));
        }
        return ids;
    }

    /**
     * Extracts the subnetwork for the given origin entity using only primitive
     * operations (AC2).
     *
     * @param originId the chosen entity id
     * @return the {@link SubnetworkResult}
     * @throws IllegalStateException    if no relations graph has been built yet
     * @throws IllegalArgumentException if {@code originId} is unknown
     */
    public SubnetworkResult extractSubnetwork(String originId) {
        SupportGraph graph = getSupportGraph();
        return SubnetworkExtractor.extract(graph, originId);
    }

    /**
     * Extracts the subnetwork of the given origin entity in the network state
     * active at the given snapshot date (US32 temporal view). The same entity
     * can integrate a different subnetwork depending on which edges are active.
     *
     * @param date     the snapshot date in yyyy-MM-dd format
     * @param originId the chosen entity id
     * @return the {@link SubnetworkResult} for that date
     * @throws IllegalStateException    if no relations graph has been built yet
     * @throws IllegalArgumentException if the date is blank or {@code originId} is unknown
     */
    public SubnetworkResult extractSubnetwork(String date, String originId) {
        return SubnetworkExtractor.extract(buildSupportGraphFor(date), originId);
    }

    /**
     * Exports the subnetwork as a DOT file and renders it to SVG via Graphviz.
     *
     * <p>This method uses {@link GraphDotExporter} and a system {@code Process}
     * to call Graphviz — both are permitted by US36 AC2, which exempts the
     * visualisation step from the primitive-operations constraint.</p>
     *
     * @param result        the subnetwork result from {@link #extractSubnetwork}
     * @param entitiesCsv   path to the entities CSV (for typed node rendering)
     * @param outputSvgPath path for the output SVG file
     * @throws IOException if any file operation fails
     */
    public void exportToSvg(SubnetworkResult result,
                            String entitiesCsv,
                            String outputSvgPath) throws IOException {
        // Load typed entities from CSV so the exporter can apply shapes/colours
        writeSvg(result, EntityCsvParser.parse(entitiesCsv), outputSvgPath);
    }

    /**
     * Exports the subnetwork to SVG using the entities already loaded in the
     * graph (US19), so the GUI does not have to ask for the entities CSV again.
     *
     * @param result        the subnetwork result from {@link #extractSubnetwork}
     * @param outputSvgPath path for the output SVG file
     * @throws IOException if any file operation fails
     */
    public void exportToSvg(SubnetworkResult result,
                            String outputSvgPath) throws IOException {
        writeSvg(result, graphRepository.getAll(), outputSvgPath);
    }

    private void writeSvg(SubnetworkResult result,
                          List<Entity> allEntities,
                          String outputSvgPath) throws IOException {
        RelationGraph relationGraph = requireRelationGraph();

        // Filter entities and edges to those present in the subnetwork
        boolean[] inSubnet = buildMembershipSet(result);
        List<Entity> subEntities = filterEntities(allEntities, result);
        List<Edge>   subEdges    = filterEdges(relationGraph, inSubnet, result);

        // Build DOT string (visualisation — exempt from AC2)
        String dot = GraphDotExporter.export(subEntities, subEdges);

        // Write DOT file
        String dotPath = outputSvgPath.endsWith(".svg")
                ? outputSvgPath.substring(0, outputSvgPath.length() - 4) + ".dot"
                : outputSvgPath + ".dot";

        BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
        try {
            writer.write(dot);
        } finally {
            writer.close();
        }

        // Render SVG via Graphviz (visualisation — exempt from AC2)
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", dotPath, "-o", outputSvgPath);
        // Discard dot's stdout/stderr so a chatty process cannot fill the pipe
        // buffer and deadlock waitFor(); the SVG itself is written to the file.
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                throw new RuntimeException(
                        "Graphviz 'dot' failed with exit code " + code
                        + ". Is Graphviz installed? On macOS: brew install graphviz");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Graph rendering was interrupted.", e);
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private RelationGraph requireRelationGraph() {
        RelationGraph relationGraph = graphRepository.getRelationGraph();
        if (relationGraph == null) {
            throw new IllegalStateException(
                    "No relations graph available. Build the graph first (US20).");
        }
        return relationGraph;
    }

    private SupportGraph getSupportGraph() {
        if (cachedSupportGraph == null) {
            cachedSupportGraph = new SupportGraph(requireRelationGraph());
        }
        return cachedSupportGraph;
    }

    /**
     * Builds the support graph for the network state active at the given date,
     * via a {@link NetworkSnapshot} (same approach as US33). Not cached because
     * it depends on the requested date.
     */
    private SupportGraph buildSupportGraphFor(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("date must not be blank");
        }
        requireRelationGraph();
        NetworkSnapshot snapshot = NetworkSnapshot.of(
                date, graphRepository.getAll(), graphRepository.getEdges());
        return new SupportGraph(snapshot.getGraph());
    }

    /**
     * Builds a fast O(1)-lookup set: nodeInSubnet[i] = true when the entity
     * with id == result.getNodeId(i) is in the subnetwork.
     * We use a String-comparison scan (primitive — no HashSet).
     */
    private boolean[] buildMembershipSet(SubnetworkResult result) {
        // reuse result itself — containsId() scans result.getNodeIds()
        boolean[] set = new boolean[result.size()];
        for (int i = 0; i < result.size(); i++) {
            set[i] = true;   // every node in result is by definition a member
        }
        return set;
    }

    private boolean containedInResult(SubnetworkResult result, String id) {
        for (int i = 0; i < result.size(); i++) {
            if (result.getNodeId(i).equals(id)) {
                return true;
            }
        }
        return false;
    }

    private List<Entity> filterEntities(List<Entity> all, SubnetworkResult result) {
        List<Entity> filtered = new ArrayList<>();
        for (Entity e : all) {
            if (containedInResult(result, e.getId())) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    private List<Edge> filterEdges(RelationGraph relationGraph,
                                    boolean[] ignored,
                                    SubnetworkResult result) {
        List<Edge> filtered = new ArrayList<>();
        for (String nodeId : relationGraph.nodes()) {
            if (containedInResult(result, nodeId)) {
                for (Edge e : relationGraph.neighbors(nodeId)) {
                    if (containedInResult(result, e.getToId())) {
                        filtered.add(e);
                    }
                }
            }
        }
        return filtered;
    }
}
