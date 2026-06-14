package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.IndexRegistry;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US21 - Generate one adjacency matrix per relation type from
 * the relations graph built in US20. Also produces a global matrix that sums
 * the per-label matrices, used to support chain queries in US22 and US23.
 */
public class GenerateAdjacencyMatricesController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller using the singleton repository.
     */
    public GenerateAdjacencyMatricesController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository. Used in tests.
     *
     * @param graphRepository the graph repository.
     */
    public GenerateAdjacencyMatricesController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    /**
     * Generates the adjacency matrices for the relations graph stored in the
     * repository. Produces one matrix per distinct relation label plus a
     * global matrix that sums all of them.
     *
     * @return a {@link GenerationResult} with ordered node ids, per-label         matrices and the global matrix.
     * @throws IllegalStateException if no relations graph has been built yet.
     */
    public GenerationResult generate() {
        RelationGraph graph = graphRepository.getRelationGraph();
        if (graph == null) {
            throw new IllegalStateException("No relations graph available. Build the relations graph first (US20).");
        }

        List<String> allNodeIds = graph.nodes();

        List<String> labels = new ArrayList<>();
        for (String nodeId : graph.nodes()) {
            for (Edge e : graph.neighbors(nodeId)) {
                if (!labels.contains(e.getLabel())) {
                    labels.add(e.getLabel());
                }
            }
        }

        List<LabeledAdjacencyMatrix> matrices = new ArrayList<>();
        for (String label : labels) {
            matrices.add(buildLabelMatrix(graph, label, allNodeIds));
        }

        AdjacencyMatrix global = buildGlobalMatrix(graph, allNodeIds);
        return new GenerationResult(allNodeIds, matrices, global);
    }

    /**
     * Builds the matrix for a single relation label, including only the
     * entities that actually participate in that relation (as source or
     * target). This is why a relation such as "relativeOf", that only exists
     * between people, produces a matrix with people only, instead of every
     * entity in the graph.
     *
     * @param graph      the relations graph.
     * @param label      the relation label.
     * @param allNodeIds the ordered ids of every node, used to keep the rows
     *                   and columns in the same global order.
     * @return the labeled matrix restricted to the participating entities.
     */
    private LabeledAdjacencyMatrix buildLabelMatrix(RelationGraph graph, String label, List<String> allNodeIds) {
        List<String> labelNodeIds = participatingNodeIds(graph, label, allNodeIds);

        IndexRegistry registry = new IndexRegistry();
        for (String nodeId : labelNodeIds) {
            registry.indexFor(nodeId);
        }

        AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
        for (String nodeId : graph.nodes()) {
            for (Edge e : graph.neighbors(nodeId)) {
                if (label.equals(e.getLabel())) {
                    int from = registry.indexFor(e.getFromId());
                    int to = registry.indexFor(e.getToId());
                    m.addEdge(from, to, e.getWeight());
                }
            }
        }
        return new LabeledAdjacencyMatrix(label, m, labelNodeIds);
    }

    /**
     * Collects the ids of the entities that take part in at least one edge of
     * the given label (as source or target), keeping them in the same order as
     * {@code allNodeIds}.
     *
     * @param graph      the relations graph.
     * @param label      the relation label.
     * @param allNodeIds the ordered ids of every node.
     * @return the ordered ids of the entities connected by this relation.
     */
    private List<String> participatingNodeIds(RelationGraph graph, String label, List<String> allNodeIds) {
        List<String> participating = new ArrayList<>();
        for (String nodeId : graph.nodes()) {
            for (Edge e : graph.neighbors(nodeId)) {
                if (label.equals(e.getLabel())) {
                    if (!participating.contains(e.getFromId())) {
                        participating.add(e.getFromId());
                    }
                    if (!participating.contains(e.getToId())) {
                        participating.add(e.getToId());
                    }
                }
            }
        }

        List<String> ordered = new ArrayList<>();
        for (String nodeId : allNodeIds) {
            if (participating.contains(nodeId)) {
                ordered.add(nodeId);
            }
        }
        return ordered;
    }

    /**
     * Counts the cells of the given matrix that hold a non-zero weight (i.e.
     * the number of edges represented by that matrix).
     *
     * @param matrix the matrix to inspect.
     * @return the count of non-zero entries.
     */
    public int countNonZeroEntries(AdjacencyMatrix matrix) {
        int count = 0;
        for (int i = 0; i < matrix.getSize(); i++) {
            for (int j = 0; j < matrix.getSize(); j++) {
                if (matrix.hasEdge(i, j)) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * Builds the global adjacency matrix over every entity in the graph. When
     * multiple relations connect the same pair of nodes, the weights are
     * summed in the resulting cell (MDISC clarification). Unlike the per-label
     * matrices, the global matrix always includes every entity.
     *
     * @param graph      the relations graph.
     * @param allNodeIds the ordered ids of every node.
     * @return the global matrix; null when there are no nodes.
     */
    private AdjacencyMatrix buildGlobalMatrix(RelationGraph graph, List<String> allNodeIds) {
        if (allNodeIds.isEmpty()) {
            return null;
        }
        IndexRegistry registry = new IndexRegistry();
        for (String nodeId : allNodeIds) {
            registry.indexFor(nodeId);
        }
        AdjacencyMatrix global = new AdjacencyMatrix(registry.size());
        for (String nodeId : graph.nodes()) {
            for (Edge e : graph.neighbors(nodeId)) {
                int from = registry.indexFor(e.getFromId());
                int to = registry.indexFor(e.getToId());
                global.addEdge(from, to, global.getWeight(from, to) + e.getWeight());
            }
        }
        return global;
    }

    /**
     * Result of an adjacency-matrix generation. Holds the ordered list of
     * node ids (matching the matrix indexes), the per-label matrices and the
     * global matrix.
     */
    public static class GenerationResult {
        private final List<String> nodeIds;
        private final List<LabeledAdjacencyMatrix> matrices;
        private final AdjacencyMatrix globalMatrix;

        /**
         * Creates a result snapshot.
         *
         * @param nodeIds      ordered node ids (matrix indexes).
         * @param matrices     per-label matrices.
         * @param globalMatrix the global summed matrix.
         */
        public GenerationResult(List<String> nodeIds,
                                List<LabeledAdjacencyMatrix> matrices,
                                AdjacencyMatrix globalMatrix) {
            this.nodeIds = new ArrayList<>(nodeIds);
            this.matrices = new ArrayList<>(matrices);
            this.globalMatrix = globalMatrix;
        }

        /**
         * Gets node ids.
         *
         * @return a new list with the ordered node ids.
         */
        public List<String> getNodeIds() {
            return new ArrayList<>(nodeIds);
        }

        /**
         * Gets matrices.
         *
         * @return a new list with the per-label matrices.
         */
        public List<LabeledAdjacencyMatrix> getMatrices() {
            return new ArrayList<>(matrices);
        }

        /**
         * Gets global matrix.
         *
         * @return the global matrix summing all per-label matrices; null when         there are no nodes.
         */
        public AdjacencyMatrix getGlobalMatrix() {
            return globalMatrix;
        }
    }
}
