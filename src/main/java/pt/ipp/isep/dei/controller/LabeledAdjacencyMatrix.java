package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

public class LabeledAdjacencyMatrix {

    private final String label;
    private final AdjacencyMatrix matrix;

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

    public String getLabel() {
        return label;
    }

    public AdjacencyMatrix getMatrix() {
        return matrix;
    }
}
