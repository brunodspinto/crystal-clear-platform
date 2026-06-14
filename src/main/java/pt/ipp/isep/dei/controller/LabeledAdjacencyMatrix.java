package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Pairs an {@link AdjacencyMatrix} with the relation label it represents and
 * with the ordered list of entity ids that label actually connects.
 * Used by US21 to keep the per-label matrices together with their meaning
 * when returning them to the UI.
 * <p>
 * Each per-label matrix only contains the entities that participate in that
 * relation (as source or target), so the node ids kept here may be a subset
 * of all the entities in the graph.
 */
public class LabeledAdjacencyMatrix {

    private final String label;
    private final AdjacencyMatrix matrix;
    private final List<String> nodeIds;

    /**
     * Creates a labeled adjacency matrix.
     *
     * @param label   the relation label; must not be blank.
     * @param matrix  the matrix for that label; must not be null.
     * @param nodeIds the ordered entity ids of this matrix (its row/column
     *                headers); must not be null and its size must match the
     *                matrix size.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public LabeledAdjacencyMatrix(String label, AdjacencyMatrix matrix, List<String> nodeIds) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        if (matrix == null) {
            throw new IllegalArgumentException("matrix must not be null");
        }
        if (nodeIds == null) {
            throw new IllegalArgumentException("nodeIds must not be null");
        }
        if (nodeIds.size() != matrix.getSize()) {
            throw new IllegalArgumentException("nodeIds size must match matrix size");
        }
        this.label = label;
        this.matrix = matrix;
        this.nodeIds = new ArrayList<>(nodeIds);
    }

    /**
     * Gets label.
     *
     * @return the relation label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets matrix.
     *
     * @return the adjacency matrix for this relation.
     */
    public AdjacencyMatrix getMatrix() {
        return matrix;
    }

    /**
     * Gets the ordered entity ids of this matrix (its row/column headers).
     *
     * @return a new list with the node ids that this relation connects.
     */
    public List<String> getNodeIds() {
        return new ArrayList<>(nodeIds);
    }
}
