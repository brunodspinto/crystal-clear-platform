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
        List<Edge> outgoing = adj.get(e.getFromId());
        if (outgoing == null) {
            outgoing = new ArrayList<>();
            adj.put(e.getFromId(), outgoing);
        }
        outgoing.add(e);
        // make sure the target node is at least registered, even with no outgoing edges
        if (!adj.containsKey(e.getToId())) {
            adj.put(e.getToId(), new ArrayList<>());
        }
        // TODO: directed vs undirected — for now we only store from -> to
    }

    /**
     * Registers a node with no outgoing edges. Useful when an entity from
     * US19 has no relations yet but should still appear in queries that
     * iterate every known node.
     */
    public void addNode(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (!adj.containsKey(id)) {
            adj.put(id, new ArrayList<>());
        }
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

    /**
     * Bridge for US21. Builds a square AdjacencyMatrix containing every edge
     * stored in this graph, regardless of label. The given registry is used
     * to map entity ids to row/column indexes; the registry is updated with
     * the ids encountered here, so the same instance can be reused across
     * several relation matrices to keep indexes consistent.
     */
    public AdjacencyMatrix toAdjacencyMatrix(IndexRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        for (String id : adj.keySet()) {
            registry.indexFor(id);
        }
        AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
        for (String fromId : adj.keySet()) {
            for (Edge e : adj.get(fromId)) {
                int from = registry.indexFor(e.getFromId());
                int to = registry.indexFor(e.getToId());
                m.addEdge(from, to, e.getWeight());
            }
        }
        return m;
    }

    /**
     * Same as {@link #toAdjacencyMatrix(IndexRegistry)} but only includes the
     * edges whose label matches {@code label}. Used by US21 to produce one
     * matrix per relation type.
     */
    public AdjacencyMatrix toAdjacencyMatrix(String label, IndexRegistry registry) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        for (String id : adj.keySet()) {
            registry.indexFor(id);
        }
        AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
        for (String fromId : adj.keySet()) {
            for (Edge e : adj.get(fromId)) {
                if (label.equals(e.getLabel())) {
                    int from = registry.indexFor(e.getFromId());
                    int to = registry.indexFor(e.getToId());
                    m.addEdge(from, to, e.getWeight());
                }
            }
        }
        return m;
    }

    // TODO: String exportDot() — graphviz, optional, only if there is time
}
