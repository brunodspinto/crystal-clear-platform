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

        IndexRegistry registry = new IndexRegistry();
        for (String nodeId : graph.nodes()) {
            registry.indexFor(nodeId);
        }

        List<String> nodeIds = new ArrayList<>();
        for (int i = 0; i < registry.size(); i++) {
            nodeIds.add(registry.idAt(i));
        }

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
            AdjacencyMatrix m = graph.toAdjacencyMatrix(label, registry);
            matrices.add(new LabeledAdjacencyMatrix(label, m));
        }

        AdjacencyMatrix global = buildGlobalMatrix(matrices, nodeIds.size());
        return new GenerationResult(nodeIds, matrices, global);
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
     * Builds the global adjacency matrix as the sum of every per-label
     * matrix. When multiple relations connect the same pair of nodes, the
     * weights are summed in the resulting cell (MDISC clarification).
     *
     * @param matrices the per-label matrices.
     * @param n        the size of each matrix (number of nodes).
     * @return the global matrix; null when n is 0.
     */
    private AdjacencyMatrix buildGlobalMatrix(List<LabeledAdjacencyMatrix> matrices, int n) {
        if (n == 0) {
            return null;
        }
        AdjacencyMatrix global = new AdjacencyMatrix(n);
        for (LabeledAdjacencyMatrix lam : matrices) {
            AdjacencyMatrix m = lam.getMatrix();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double add = m.getWeight(i, j);
                    if (add != 0) {
                        double existing = global.getWeight(i, j);
                        global.addEdge(i, j, existing + add);
                    }
                }
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
