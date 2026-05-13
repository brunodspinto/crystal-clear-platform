package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.IndexRegistry;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

public class GenerateAdjacencyMatricesController {

    private final GraphRepository graphRepository;

    public GenerateAdjacencyMatricesController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    public GenerateAdjacencyMatricesController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

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

    // MDISC: the global matrix sums all per-label matrices.
    // When multiple relations connect the same pair, their weights are summed.
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

    public static class GenerationResult {
        private final List<String> nodeIds;
        private final List<LabeledAdjacencyMatrix> matrices;
        private final AdjacencyMatrix globalMatrix;

        public GenerationResult(List<String> nodeIds,
                                List<LabeledAdjacencyMatrix> matrices,
                                AdjacencyMatrix globalMatrix) {
            this.nodeIds = new ArrayList<>(nodeIds);
            this.matrices = new ArrayList<>(matrices);
            this.globalMatrix = globalMatrix;
        }

        public List<String> getNodeIds() {
            return new ArrayList<>(nodeIds);
        }

        public List<LabeledAdjacencyMatrix> getMatrices() {
            return new ArrayList<>(matrices);
        }

        public AdjacencyMatrix getGlobalMatrix() {
            return globalMatrix;
        }
    }
}
