package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate for US20. Stores edges produced by US19's entity extraction
 * and exposes projections used by US21/US22/US23.
 *
 * Backed by parallel ArrayLists (one with node ids, one with the
 * corresponding outgoing edges), keeping the implementation within
 * the PPROG-level Collections coverage.
 */
public class RelationGraph {

    private final List<String> nodeIds = new ArrayList<>();
    private final List<List<Edge>> outgoing = new ArrayList<>();

    public void addEdge(Edge e) {
        if (e == null) {
            throw new IllegalArgumentException("edge must not be null");
        }
        int fromIndex = indexOfNode(e.getFromId());
        if (fromIndex < 0) {
            fromIndex = registerNode(e.getFromId());
        }
        outgoing.get(fromIndex).add(e);
        // make sure the target node is at least registered, even with no outgoing edges
        if (indexOfNode(e.getToId()) < 0) {
            registerNode(e.getToId());
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
        if (indexOfNode(id) < 0) {
            registerNode(id);
        }
    }

    public List<Edge> neighbors(String id) {
        int i = indexOfNode(id);
        if (i < 0) {
            return List.of();
        }
        return Collections.unmodifiableList(outgoing.get(i));
    }

    public List<String> nodes() {
        return new ArrayList<>(nodeIds);
    }

    public int nodeCount() {
        return nodeIds.size();
    }

    /**
     * Bridge for US21. Builds a square AdjacencyMatrix containing every edge
     * stored in this graph, regardless of label. The given registry is used
     * to map entity ids to row/column indexes.
     */
    public AdjacencyMatrix toAdjacencyMatrix(IndexRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        for (String id : nodeIds) {
            registry.indexFor(id);
        }
        AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
        for (int i = 0; i < nodeIds.size(); i++) {
            for (Edge e : outgoing.get(i)) {
                int from = registry.indexFor(e.getFromId());
                int to = registry.indexFor(e.getToId());
                m.addEdge(from, to, e.getWeight());
            }
        }
        return m;
    }

    /**
     * Same as {@link #toAdjacencyMatrix(IndexRegistry)} but only includes the
     * edges whose label matches {@code label}.
     */
    public AdjacencyMatrix toAdjacencyMatrix(String label, IndexRegistry registry) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        for (String id : nodeIds) {
            registry.indexFor(id);
        }
        AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
        for (int i = 0; i < nodeIds.size(); i++) {
            for (Edge e : outgoing.get(i)) {
                if (label.equals(e.getLabel())) {
                    int from = registry.indexFor(e.getFromId());
                    int to = registry.indexFor(e.getToId());
                    m.addEdge(from, to, e.getWeight());
                }
            }
        }
        return m;
    }

    /**
     * Builds a Graphviz DOT representation of the graph. Nodes are entity ids,
     * edges carry the relation label and weight. Designed to be piped into
     * `dot -Tsvg` (or any other Graphviz layout engine) to produce a visual
     * rendering of the relations network.
     */
    public String exportDot() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph RelationsGraph {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=box, style=rounded, fontname=\"Helvetica\"];\n");
        dot.append("  edge [fontname=\"Helvetica\", fontsize=10];\n");
        for (String id : nodeIds) {
            dot.append("  \"").append(id).append("\";\n");
        }
        for (int i = 0; i < nodeIds.size(); i++) {
            for (Edge e : outgoing.get(i)) {
                dot.append("  \"").append(e.getFromId()).append("\" -> \"")
                        .append(e.getToId()).append("\"")
                        .append(" [label=\"").append(e.getLabel())
                        .append(" (").append(e.getWeight()).append(")\"];\n");
            }
        }
        dot.append("}\n");
        return dot.toString();
    }

    private int indexOfNode(String id) {
        for (int i = 0; i < nodeIds.size(); i++) {
            if (nodeIds.get(i).equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private int registerNode(String id) {
        nodeIds.add(id);
        outgoing.add(new ArrayList<>());
        return nodeIds.size() - 1;
    }
}
