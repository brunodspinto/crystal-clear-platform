package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

/**
 * Pairs an {@link AdjacencyMatrix} with the relation label it represents.
 * Used by US21 to keep the per-label matrices together with their meaning
 * when returning them to the UI.
 */
public class LabeledAdjacencyMatrix {

    private final String label;
    private final AdjacencyMatrix matrix;

    /**
     * Creates a labeled adjacency matrix.
     *
     * @param label  the relation label; must not be blank.
     * @param matrix the matrix for that label; must not be null.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public LabeledAdjacencyMatrix(String label, AdjacencyMatrix matrix) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        if (matrix == null) {
            throw new IllegalArgumentException("matrix must not be null");
        }
        this.label = label;
        this.matrix = matrix;
    }

    /**
     * @return the relation label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the adjacency matrix for this relation.
     */
    public AdjacencyMatrix getMatrix() {
        return matrix;
    }
}
