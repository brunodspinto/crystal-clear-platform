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
        for (Edge edge : edges) {
            if (isSymmetricLabel(edge.getLabel()) && !hasReverse(edges, edge)) {
                graph.addEdge(new Edge(edge.getToId(), edge.getFromId(),
                        edge.getLabel(), edge.getWeight(),
                        edge.getStartDate(), edge.getEndDate()));
            }
        }
        return graph;
    }

    /**
     * Tells whether a relation label represents a symmetric (bidirectional)
     * tie, i.e. one that holds in both directions between the two entities
     * (being relatives, friends or associates of each other).
     *
     * @param label the relation label.
     * @return {@code true} if the relation is symmetric.
     */
    public static boolean isSymmetricLabel(String label) {
        return "relativeOf".equals(label)
                || "friendOf".equals(label)
                || "associatedWith".equals(label);
    }

    private static boolean hasReverse(List<Edge> edges, Edge e) {
        for (Edge other : edges) {
            if (other.getFromId().equals(e.getToId())
                    && other.getToId().equals(e.getFromId())
                    && other.getLabel().equals(e.getLabel())) {
                return true;
            }
        }
        return false;
    }
}
