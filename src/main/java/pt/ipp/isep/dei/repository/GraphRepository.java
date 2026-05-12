package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.RelationGraph;

import java.util.ArrayList;
import java.util.List;

public class GraphRepository {

    private final List<Entity> entities;
    private RelationGraph relationGraph;

    public GraphRepository() {
        entities = new ArrayList<>();
        relationGraph = null;
    }

    public void addAll(List<Entity> newEntities) {
        entities.addAll(newEntities);
    }

    public List<Entity> getAll() {
        return List.copyOf(entities);
    }

    public void setRelationGraph(RelationGraph graph) {
        this.relationGraph = graph;
    }

    public RelationGraph getRelationGraph() {
        return relationGraph;
    }
}
