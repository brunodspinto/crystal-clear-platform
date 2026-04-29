package pt.ipp.isep.dei.domain.graph;

/**
 * Adjacency matrix for a single relation type.
 * Entries hold the weight of the edge between two entities
 * (0 means no edge).
 *
 * For now the matrix is square (m = n), so the same entity index
 * is used on both axes. Will probably need to split into m x n
 * later for relations between different entity types.
 */
public class AdjacencyMatrix {

    private final int size;
    private final double[][] weights;

    public AdjacencyMatrix(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        this.size = size;
        this.weights = new double[size][size];
    }

    public int size() {
        return size;
    }

    public void addEdge(int from, int to, double weight) {
        // TODO: decide if we treat the graph as directed or undirected here
        weights[from][to] = weight;
    }

    public double getWeight(int from, int to) {
        return weights[from][to];
    }

    public boolean hasEdge(int from, int to) {
        return weights[from][to] != 0;
    }

    // TODO: multiply(AdjacencyMatrix other) — needed for US22/US23 (chains)
    // TODO: transpose()
}
