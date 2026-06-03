package pt.ipp.isep.dei.domain.graph;

/**
 * Extracts the subnetwork of influence chains that include a given entity
 * in the support graph (US36).
 *
 * <p>The subnetwork is defined as the set of all entities reachable from the
 * given entity in the undirected support graph — i.e. the connected component
 * that contains it. Any entity in this component can participate in an
 * influence chain that passes through the chosen entity.</p>
 *
 * <p><strong>US36 AC2 compliance:</strong> the extraction algorithm uses only
 * primitive operations — {@code boolean[]} and {@code int[]} arrays with
 * manual index tracking. No {@code java.util} collection classes are used.
 * (The visualisation step is handled separately in the controller and UI,
 * which are exempt from this constraint per the AC.)</p>
 *
 * <h2>Algorithm (BFS on the support graph)</h2>
 * <pre>
 * ExtractSubnetwork(G, origin):
 *   visited[0..N-1] = false
 *   queue[0..N-1] = empty array used as FIFO
 *   head = 0, tail = 0
 *
 *   src = index of origin in G
 *   visited[src] = true
 *   enqueue(src)
 *
 *   while queue not empty:
 *     u = dequeue()
 *     for each v in 0..N-1:
 *       if adj[u][v] AND NOT visited[v]:
 *         visited[v] = true
 *         enqueue(v)
 *
 *   // collect results
 *   subIds[0..k-1]   = ids of visited nodes
 *   subAdj[0..k-1][0..k-1] = submatrix of adj for visited nodes
 *   return SubnetworkResult(subIds, subAdj)
 * </pre>
 *
 * <h2>Worst-case time complexity</h2>
 * <p>Same as BFS: O(N + E) where N is the number of nodes and E the number
 * of undirected edges in the support graph. In the dense case O(N²).
 * Documented in full in US35.</p>
 */
public class SubnetworkExtractor {

    private SubnetworkExtractor() {}

    /**
     * Extracts the connected subnetwork containing {@code originId} from the
     * support graph.
     *
     * @param graph    the support graph; must not be null
     * @param originId the entity whose influence subnetwork is to be extracted;
     *                 must be a known entity id
     * @return a {@link SubnetworkResult} describing the subnetwork
     * @throws IllegalArgumentException if {@code graph} is null or
     *                                  {@code originId} is unknown
     */
    public static SubnetworkResult extract(SupportGraph graph, String originId) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
        if (!graph.getRegistry().contains(originId)) {
            throw new IllegalArgumentException("unknown entity id: " + originId);
        }

        int n = graph.size();
        boolean[][] adj = graph.getAdjacencyMatrix();
        int src = graph.getRegistry().indexFor(originId);

        // ----------------------------------------------------------------
        // BFS using primitive arrays only (AC2)
        // ----------------------------------------------------------------

        boolean[] visited = new boolean[n];
        int[] queue = new int[n];
        int head = 0;
        int tail = 0;

        visited[src] = true;
        queue[tail] = src;
        tail = tail + 1;

        while (head != tail) {
            int u = queue[head];
            head = head + 1;

            for (int v = 0; v < n; v++) {
                if (adj[u][v] && !visited[v]) {
                    visited[v] = true;
                    queue[tail] = v;
                    tail = tail + 1;
                }
            }
        }

        // ----------------------------------------------------------------
        // Collect visited node ids into a compact array
        // ----------------------------------------------------------------

        // Count visited nodes first (primitive — no list)
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                count = count + 1;
            }
        }

        // Map original index → subnetwork index
        int[] subIndex = new int[n];
        for (int i = 0; i < n; i++) {
            subIndex[i] = -1;
        }
        String[] subIds = new String[count];
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                subIndex[i] = k;
                subIds[k] = graph.getRegistry().idAt(i);
                k = k + 1;
            }
        }

        // ----------------------------------------------------------------
        // Build the sub-adjacency matrix for visited nodes only
        // ----------------------------------------------------------------

        boolean[][] subAdj = new boolean[count][count];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) continue;
            for (int j = 0; j < n; j++) {
                if (!visited[j]) continue;
                subAdj[subIndex[i]][subIndex[j]] = adj[i][j];
            }
        }

        return new SubnetworkResult(originId, subIds, subAdj);
    }

    // =========================================================================
    // SubnetworkResult value object
    // =========================================================================

    /**
     * Immutable result of a subnetwork extraction.
     *
     * <p>Contains the origin entity id, the ordered array of entity ids in the
     * subnetwork (including the origin), and the symmetric boolean adjacency
     * matrix of the subnetwork.</p>
     */
    public static class SubnetworkResult {

        private final String originId;
        private final String[] nodeIds;
        private final boolean[][] adjMatrix;

        /**
         * @param originId  the entity the extraction started from
         * @param nodeIds   ordered array of entity ids in the subnetwork
         * @param adjMatrix symmetric boolean adjacency matrix; entry [i][j] is
         *                  true when node i and node j are adjacent
         */
        public SubnetworkResult(String originId, String[] nodeIds, boolean[][] adjMatrix) {
            if (originId == null || originId.isBlank()) {
                throw new IllegalArgumentException("originId must not be blank");
            }
            if (nodeIds == null || nodeIds.length == 0) {
                throw new IllegalArgumentException("nodeIds must not be empty");
            }
            if (adjMatrix == null) {
                throw new IllegalArgumentException("adjMatrix must not be null");
            }
            this.originId  = originId;
            // defensive copies (primitive arrays)
            this.nodeIds   = copyArray(nodeIds);
            this.adjMatrix = copyMatrix(adjMatrix);
        }

        /** @return the id of the entity the extraction started from */
        public String getOriginId() {
            return originId;
        }

        /** @return the number of entities in the subnetwork */
        public int size() {
            return nodeIds.length;
        }

        /**
         * Returns the entity id at position {@code index} in the subnetwork.
         *
         * @param index 0-based position
         * @return the entity id
         */
        public String getNodeId(int index) {
            if (index < 0 || index >= nodeIds.length) {
                throw new IndexOutOfBoundsException("index out of range: " + index);
            }
            return nodeIds[index];
        }

        /**
         * Returns a defensive copy of the entity id array.
         *
         * @return array of entity ids
         */
        public String[] getNodeIds() {
            return copyArray(nodeIds);
        }

        /**
         * Returns {@code true} if the entity with subnetwork index {@code i}
         * is adjacent to the entity with subnetwork index {@code j}.
         *
         * @param i row index
         * @param j column index
         * @return true if adjacent
         */
        public boolean isAdjacent(int i, int j) {
            return adjMatrix[i][j];
        }

        /**
         * Returns a defensive copy of the adjacency matrix.
         *
         * @return the sub-adjacency matrix
         */
        public boolean[][] getAdjacencyMatrix() {
            return copyMatrix(adjMatrix);
        }

        // ---------------------------------------------------------------
        // Helpers
        // ---------------------------------------------------------------

        private static String[] copyArray(String[] src) {
            String[] copy = new String[src.length];
            for (int i = 0; i < src.length; i++) {
                copy[i] = src[i];
            }
            return copy;
        }

        private static boolean[][] copyMatrix(boolean[][] src) {
            boolean[][] copy = new boolean[src.length][];
            for (int i = 0; i < src.length; i++) {
                copy[i] = new boolean[src[i].length];
                for (int j = 0; j < src[i].length; j++) {
                    copy[i][j] = src[i][j];
                }
            }
            return copy;
        }
    }
}
