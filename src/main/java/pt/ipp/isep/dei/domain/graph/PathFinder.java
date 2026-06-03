package pt.ipp.isep.dei.domain.graph;

/**
 * Verifies the existence of a pathway between two entities in a
 * {@link SupportGraph} and returns the shortest distance when one exists.
 *
 * <p><strong>US34 AC3 compliance:</strong> the BFS implementation uses only
 * primitive operations — a plain {@code int[]} array as a circular queue and
 * an {@code int[]} array for distances. No {@code java.util} collection
 * classes ({@code Queue}, {@code LinkedList}, {@code ArrayDeque}, etc.) are
 * used anywhere in this class.</p>
 *
 * <h2>Algorithm (BFS on the support graph)</h2>
 * <pre>
 * PathFind(G, src, dst):
 *   if src == dst: return 0
 *   dist[0..N-1] = -1          // -1 means "not visited"
 *   queue[0..N-1] = empty      // circular int array used as FIFO
 *   head = 0, tail = 0
 *
 *   dist[src] = 0
 *   enqueue(src)
 *
 *   while queue not empty:
 *     u = dequeue()
 *     for each v adjacent to u in G:
 *       if dist[v] == -1:        // not visited
 *         dist[v] = dist[u] + 1
 *         if v == dst: return dist[v]
 *         enqueue(v)
 *
 *   return -1  // no path
 * </pre>
 *
 * <h2>Worst-case time complexity</h2>
 * <p>Every node is enqueued at most once and every edge is examined at most
 * once from each endpoint. For a support graph with N nodes and E undirected
 * edges: <b>O(N + E)</b>. In the dense case (complete graph) E = N(N−1)/2,
 * giving <b>O(N²)</b>. Documented in full in US35.</p>
 */
public class PathFinder {

    /**
     * Sentinel value returned by {@link #shortestDistance} when no path exists.
     */
    public static final int NO_PATH = -1;

    private PathFinder() {}

    /**
     * Returns the shortest distance (number of edges) between {@code sourceId}
     * and {@code targetId} in the support graph, or {@link #NO_PATH} if no
     * path exists.
     *
     * <p>Distance 0 is returned when source and target are the same entity.
     * Distance 1 means they are directly adjacent.</p>
     *
     * @param graph    the support graph to search; must not be null
     * @param sourceId the starting entity id; must be known to the graph
     * @param targetId the destination entity id; must be known to the graph
     * @return shortest distance ≥ 0, or {@link #NO_PATH} (−1) if unreachable
     * @throws IllegalArgumentException if {@code graph} is null or either id
     *                                  is unknown
     */
    public static int shortestDistance(SupportGraph graph, String sourceId, String targetId) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }

        IndexRegistry registry = graph.getRegistry();

        if (!registry.contains(sourceId)) {
            throw new IllegalArgumentException("unknown source entity: " + sourceId);
        }
        if (!registry.contains(targetId)) {
            throw new IllegalArgumentException("unknown target entity: " + targetId);
        }

        int src = registry.indexFor(sourceId);
        int dst = registry.indexFor(targetId);

        // Trivial case: same entity
        if (src == dst) {
            return 0;
        }

        int n = graph.size();
        boolean[][] adjMatrix = graph.getAdjacencyMatrix();

        // dist[i] = shortest distance from src to i; -1 means not visited
        int[] dist = new int[n];
        for (int i = 0; i < n; i++) {
            dist[i] = NO_PATH;
        }

        // BFS queue implemented as a primitive int array (circular buffer)
        // Maximum elements ever enqueued = n (each node at most once)
        int[] queue = new int[n];
        int head = 0;
        int tail = 0;

        // Initialise BFS from src
        dist[src] = 0;
        queue[tail] = src;
        tail = tail + 1;

        while (head != tail) {
            // Dequeue
            int u = queue[head];
            head = head + 1;

            // Explore neighbours using the raw adjacency row
            for (int v = 0; v < n; v++) {
                if (adjMatrix[u][v] && dist[v] == NO_PATH) {
                    dist[v] = dist[u] + 1;

                    // Early exit: reached the target
                    if (v == dst) {
                        return dist[v];
                    }

                    // Enqueue v
                    queue[tail] = v;
                    tail = tail + 1;
                }
            }
        }

        return NO_PATH;
    }

    /**
     * Convenience method — returns {@code true} when a path exists between
     * the two entities (i.e. {@link #shortestDistance} ≥ 0).
     *
     * @param graph    the support graph
     * @param sourceId the starting entity id
     * @param targetId the destination entity id
     * @return true if reachable, false otherwise
     */
    public static boolean hasPath(SupportGraph graph, String sourceId, String targetId) {
        return shortestDistance(graph, sourceId, targetId) != NO_PATH;
    }
}
