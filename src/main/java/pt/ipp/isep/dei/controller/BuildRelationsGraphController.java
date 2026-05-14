package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.GraphBuilder;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US20 - Build the relations graph between entities by loading
 * a CSV file. The resulting graph is stored in the {@link GraphRepository} and
 * consumed by US21 (adjacency matrices), US22 (nepotism), and US23 (conflicts).
 */
public class BuildRelationsGraphController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller using the singleton repository.
     */
    public BuildRelationsGraphController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository. Used in tests.
     *
     * @param graphRepository the graph repository.
     */
    public BuildRelationsGraphController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    /**
     * Parses a relations CSV, builds the {@link RelationGraph} and stores it
     * in the repository for later use.
     *
     * @param filePath path to the relations CSV file.
     * @return a {@link BuildResult} with edge count, node count and distinct labels.
     * @throws IOException if the CSV cannot be read.
     */
    public BuildResult buildFromCsv(String filePath) throws IOException {
        List<Edge> edges = RelationCsvParser.parse(filePath);
        List<Entity> entities = graphRepository.getAll();
        RelationGraph graph = GraphBuilder.build(entities, edges);
        graphRepository.setRelationGraph(graph);

        List<String> labels = new ArrayList<>();
        for (Edge e : edges) {
            if (!labels.contains(e.getLabel())) {
                labels.add(e.getLabel());
            }
        }
        return new BuildResult(edges.size(), graph.nodeCount(), labels);
    }

    /**
     * Renders the current relations graph to an SVG file using Graphviz.
     * Writes the DOT source next to the SVG and then invokes the `dot`
     * binary to produce the visual rendering.
     *
     * @param outputSvgPath path where the SVG should be written.
     * @return the path of the generated SVG file.
     * @throws IOException if writing the DOT file fails.
     * @throws IllegalStateException if no relations graph has been built yet.
     * @throws RuntimeException if Graphviz is not available on the system.
     */
    public String renderGraphToSvg(String outputSvgPath) throws IOException {
        RelationGraph graph = graphRepository.getRelationGraph();
        if (graph == null) {
            throw new IllegalStateException("No relations graph available. Build the relations graph first.");
        }

        String dotPath;
        if (outputSvgPath.endsWith(".svg")) {
            dotPath = outputSvgPath.substring(0, outputSvgPath.length() - 4) + ".dot";
        } else {
            dotPath = outputSvgPath + ".dot";
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath))) {
            writer.write(graph.exportDot());
        }

        ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", dotPath, "-o", outputSvgPath);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                throw new RuntimeException("Graphviz 'dot' failed with exit code " + code
                        + ". Is Graphviz installed? On macOS: brew install graphviz");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Graph rendering was interrupted.", e);
        }
        return outputSvgPath;
    }

    /**
     * Summary of a graph build operation. Holds the edge count, the number of
     * distinct nodes, and the list of distinct relation labels encountered.
     */
    public static class BuildResult {
        private final int edgeCount;
        private final int nodeCount;
        private final List<String> labels;

        /**
         * Creates a build result.
         *
         * @param edgeCount the number of edges loaded.
         * @param nodeCount the number of distinct nodes registered.
         * @param labels    the distinct labels encountered in the relations.
         */
        public BuildResult(int edgeCount, int nodeCount, List<String> labels) {
            this.edgeCount = edgeCount;
            this.nodeCount = nodeCount;
            this.labels = new ArrayList<>(labels);
        }

        /**
         * @return the number of edges loaded from the CSV.
         */
        public int getEdgeCount() {
            return edgeCount;
        }

        /**
         * @return the number of distinct nodes registered in the graph.
         */
        public int getNodeCount() {
            return nodeCount;
        }

        /**
         * @return a new list with the distinct relation labels encountered.
         */
        public List<String> getLabels() {
            return new ArrayList<>(labels);
        }
    }
}
