package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.GraphBuilder;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuildRelationsGraphController {

    private final GraphRepository graphRepository;

    public BuildRelationsGraphController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    public BuildRelationsGraphController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

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

    public static class BuildResult {
        private final int edgeCount;
        private final int nodeCount;
        private final List<String> labels;

        public BuildResult(int edgeCount, int nodeCount, List<String> labels) {
            this.edgeCount = edgeCount;
            this.nodeCount = nodeCount;
            this.labels = new ArrayList<>(labels);
        }

        public int getEdgeCount() {
            return edgeCount;
        }

        public int getNodeCount() {
            return nodeCount;
        }

        public List<String> getLabels() {
            return new ArrayList<>(labels);
        }
    }
}
