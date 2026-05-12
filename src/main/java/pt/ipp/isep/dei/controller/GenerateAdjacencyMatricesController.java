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

    public List<LabeledAdjacencyMatrix> generate() {
        RelationGraph graph = graphRepository.getRelationGraph();
        if (graph == null) {
            throw new IllegalStateException("No relations graph available. Build the relations graph first (US20).");
        }

        IndexRegistry registry = new IndexRegistry();
        for (String nodeId : graph.nodes()) {
            registry.indexFor(nodeId);
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
        return matrices;
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
}
