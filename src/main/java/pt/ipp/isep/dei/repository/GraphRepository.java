package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.RelationGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Graph repository.
 */
public class GraphRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Entity> entities;
    private RelationGraph relationGraph;
    private List<Edge> edges;

    /**
     * Instantiates a new Graph repository.
     */
    public GraphRepository() {
        entities = new ArrayList<>();
        relationGraph = null;
        edges = new ArrayList<>();
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

    /**
     * Stores the raw edge list (with temporal data) for snapshot filtering.
     *
     * @param edges the list of edges
     */
    public void setEdges(List<Edge> edges) {
        this.edges = edges == null ? new ArrayList<>() : new ArrayList<>(edges);
    }

    /**
     * Returns the raw edge list.
     *
     * @return a defensive copy of all edges
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }
}
