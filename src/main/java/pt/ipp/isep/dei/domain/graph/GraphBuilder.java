package pt.ipp.isep.dei.domain.graph;

import java.util.List;

/**
 * Builds a RelationGraph from a list of entities (US19) and a list of
 * relations (US20). Every entity is registered as a node so that isolated
 * entities still appear in queries; then every edge is added.
 */
public class GraphBuilder {

    private GraphBuilder() {}

    /**
     * Build relation graph.
     *
     * @param entities the entities
     * @param edges    the edges
     * @return the relation graph
     */
    public static RelationGraph build(List<Entity> entities, List<Edge> edges) {
        if (entities == null) {
            throw new IllegalArgumentException("entities must not be null");
        }
        if (edges == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
        RelationGraph graph = new RelationGraph();
        for (Entity entity : entities) {
            graph.addNode(entity.getId());
        }
        for (Edge edge : edges) {
            graph.addEdge(edge);
        }
        return graph;
    }
}
