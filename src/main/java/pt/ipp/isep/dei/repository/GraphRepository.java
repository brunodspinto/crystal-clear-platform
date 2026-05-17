package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.RelationGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Graph repository.
 */
public class GraphRepository {

    private final List<Entity> entities;
    private RelationGraph relationGraph;

    /**
     * Instantiates a new Graph repository.
     */
    public GraphRepository() {
        entities = new ArrayList<>();
        relationGraph = null;
    }

    /**
     * Add all.
     *
     * @param newEntities the new entities
     */
    public void addAll(List<Entity> newEntities) {
        entities.addAll(newEntities);
    }

    /**
     * Gets all.
     *
     * @return the all
     */
    public List<Entity> getAll() {
        return new ArrayList<>(entities);
    }

    /**
     * Sets relation graph.
     *
     * @param graph the graph
     */
    public void setRelationGraph(RelationGraph graph) {
        this.relationGraph = graph;
    }

    /**
     * Gets relation graph.
     *
     * @return the relation graph
     */
    public RelationGraph getRelationGraph() {
        return relationGraph;
    }
}
