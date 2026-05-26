package pt.ipp.isep.dei.domain.graph;

/**
 * Undirected, unweighted support graph for US33, US34, and US36.
 *
 * <p>The support graph is derived from a {@link RelationGraph} by treating
 * every directed edge as a bidirectional link and discarding all weights.
 * Two entities are adjacent in the support graph if there is at least one
 * edge (in either direction, with any label) between them in the original
 * directed graph.</p>
 *
 * <p>Internally the graph is stored as a square boolean adjacency matrix
 * of dimension N×N, where N is the number of distinct entity ids. The
 * {@link IndexRegistry} maps each entity id to its row/column index.
 * All operations use only primitive Java constructs — no {@code java.util}
 * collections beyond the index registry — to satisfy US34 AC3.</p>
 *
 * <p>Self-loops are ignored: an entity is not considered adjacent to itself.</p>
 */
public class SupportGraph {

    private final boolean[][] adj;
    private final IndexRegistry registry;
    private final int size;

    /**
     * Builds a support graph from an existing directed {@link RelationGraph}.
     *
     * <p>Every directed edge {@code u→v} (regardless of label or weight) is
     * recorded as the undirected pair {@code {u, v}}.</p>
     *
     * @param graph the directed relation graph; must not be null
     * @throws IllegalArgumentException if {@code graph} is null
     */
    public SupportGraph(RelationGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }

        registry = new IndexRegistry();

        // Pass 1 — register all node ids in a stable order
        for (String id : graph.nodes()) {
            registry.indexFor(id);
        }

        size = registry.size();
        adj = new boolean[size][size];

        // Pass 2 — fill adjacency matrix (undirected: set both u[i][j] and adj[j][i])
        for (String u : graph.nodes()) {
            int i = registry.indexFor(u);
            for (Edge e : graph.neighbors(u)) {
                int j = registry.indexFor(e.getToId());
                if (i != j) {           // ignore self-loops
                    adj[i][j] = true;
                    adj[j][i] = true;   // symmetric — undirected
                }
            }
        }
    }

    /**
     * Returns the number of entities (nodes) in this support graph.
     *
     * @return node count
     */
    public int size() {
        return size;
    }

    /**
     * Returns the {@link IndexRegistry} that maps entity ids to matrix indexes.
     *
     * @return the registry
     */
    public IndexRegistry getRegistry() {
        return registry;
    }

    /**
     * Returns {@code true} if entity {@code u} is adjacent to entity {@code v}
     * in the support graph.
     *
     * @param u id of the first entity
     * @param v id of the second entity
     * @return true if adjacent
     * @throws IllegalArgumentException if either id is unknown
     */
    public boolean isAdjacent(String u, String v) {
        int i = requireKnown(u);
        int j = requireKnown(v);
        return adj[i][j];
    }

    /**
     * Returns a copy of the raw adjacency matrix row for node {@code id},
     * expressed as a boolean array of length {@link #size()}.
     * Index {@code k} is {@code true} when the entity with index {@code k}
     * is adjacent to {@code id}.
     *
     * @param id the entity id
     * @return a boolean array of length {@code size()}
     * @throws IllegalArgumentException if {@code id} is unknown
     */
    public boolean[] adjacencyRow(String id) {
        int i = requireKnown(id);
        boolean[] copy = new boolean[size];
        for (int j = 0; j < size; j++) {
            copy[j] = adj[i][j];
        }
        return copy;
    }

    /**
     * Returns the full N×N adjacency matrix as a defensive copy.
     * Entry {@code [i][j]} is {@code true} when entity {@code i}
     * is adjacent to entity {@code j}.
     *
     * @return a deep copy of the adjacency matrix
     */
    public boolean[][] getAdjacencyMatrix() {
        boolean[][] copy = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy[i][j] = adj[i][j];
            }
        }
        return copy;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private int requireKnown(String id) {
        if (!registry.contains(id)) {
            throw new IllegalArgumentException("unknown entity id: " + id);
        }
        return registry.indexFor(id);
    }
}
