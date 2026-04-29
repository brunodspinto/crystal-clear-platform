package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Skeleton aggregate for US20.
 * Holds the edges produced by US19's entity extraction and exposes
 * the projections that US21/US22/US23 will need.
 *
 * Adjacency-list backed; will probably grow a bridge to AdjacencyMatrix
 * once US21 firms up.
 */
public class RelationGraph {

    private final Map<String, List<Edge>> adj = new HashMap<>();

    public void addEdge(Edge e) {
        if (e == null) {
            throw new IllegalArgumentException("edge must not be null");
        }
        adj.computeIfAbsent(e.fromId(), k -> new ArrayList<>()).add(e);
        // make sure the target node is at least registered, even with no outgoing edges
        adj.computeIfAbsent(e.toId(), k -> new ArrayList<>());
        // TODO: directed vs undirected — for now we only store from -> to
    }

    public List<Edge> neighbors(String id) {
        return Collections.unmodifiableList(adj.getOrDefault(id, List.of()));
    }

    public Set<String> nodes() {
        return new HashSet<>(adj.keySet());
    }

    public int nodeCount() {
        return adj.size();
    }

    // TODO: AdjacencyMatrix toAdjacencyMatrix() — bridge for US21
    // TODO: String exportDot() — graphviz, optional, only if there is time
}
