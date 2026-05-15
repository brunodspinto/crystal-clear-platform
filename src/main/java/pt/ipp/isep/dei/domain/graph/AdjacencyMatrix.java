package pt.ipp.isep.dei.domain.graph;

/**
 * Adjacency matrix for a single relation type.
 * Entries hold the weight of the edge between two entities
 * (0 means no edge).
 * <p>
 * For now the matrix is square (m = n), so the same entity index
 * is used on both axes. Will probably need to split into m x n
 * later for relations between different entity types.
 */
public class AdjacencyMatrix {

    private final int size;
    private final double[][] weights;

    /**
     * Instantiates a new Adjacency matrix.
     *
     * @param size the size
     */
    public AdjacencyMatrix(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }
        this.size = size;
        this.weights = new double[size][size];
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * Add edge.
     *
     * @param from   the from
     * @param to     the to
     * @param weight the weight
     */
    public void addEdge(int from, int to, double weight) {
        checkBounds(from, to);
        weights[from][to] = weight;
    }

    /**
     * Gets weight.
     *
     * @param from the from
     * @param to   the to
     * @return the weight
     */
    public double getWeight(int from, int to) {
        checkBounds(from, to);
        return weights[from][to];
    }

    private void checkBounds(int from, int to) {
        if (from < 0 || from >= size || to < 0 || to >= size) {
            throw new IllegalArgumentException(
                    "Index out of bounds: from=" + from + " to=" + to + " size=" + size);
        }
    }

    /**
     * Has edge boolean.
     *
     * @param from the from
     * @param to   the to
     * @return the boolean
     */
    public boolean hasEdge(int from, int to) {
        return weights[from][to] != 0;
    }

    /**
     * Standard matrix multiplication. Used by US22/US23 to walk chains of
     * length k: M^2 holds reachability/weight in 2 steps, M^3 in 3 steps, etc.
     * Both matrices must have the same size.
     *
     * @param other the other
     * @return the adjacency matrix
     */
    public AdjacencyMatrix multiply(AdjacencyMatrix other) {
        if (other == null) {
            throw new IllegalArgumentException("other must not be null");
        }
        if (other.size != this.size) {
            throw new IllegalArgumentException("matrices must have the same size");
        }
        AdjacencyMatrix result = new AdjacencyMatrix(this.size);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                double sum = 0.0;
                for (int k = 0; k < this.size; k++) {
                    sum = sum + this.weights[i][k] * other.weights[k][j];
                }
                result.weights[i][j] = sum;
            }
        }
        return result;
    }

    /**
     * Returns a new matrix with rows and columns swapped.
     * Used by US23 to follow a relation in the reverse direction.
     *
     * @return the adjacency matrix
     */
    public AdjacencyMatrix transpose() {
        AdjacencyMatrix result = new AdjacencyMatrix(this.size);
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                result.weights[i][j] = this.weights[j][i];
            }
        }
        return result;
    }
}
