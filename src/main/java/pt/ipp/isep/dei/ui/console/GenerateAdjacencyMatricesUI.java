package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.GenerateAdjacencyMatricesController;
import pt.ipp.isep.dei.controller.LabeledAdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

import java.util.List;

public class GenerateAdjacencyMatricesUI implements Runnable {

    private final GenerateAdjacencyMatricesController controller;

    public GenerateAdjacencyMatricesUI() {
        controller = new GenerateAdjacencyMatricesController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Generate Adjacency Matrices (US21) ---");

        List<LabeledAdjacencyMatrix> matrices;
        try {
            matrices = controller.generate();
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        if (matrices.isEmpty()) {
            System.out.println("\nThe relations graph has no edges. No matrices were generated.");
            return;
        }

        System.out.println("\nAdjacency matrices generated successfully.");
        for (LabeledAdjacencyMatrix entry : matrices) {
            String label = entry.getLabel();
            AdjacencyMatrix matrix = entry.getMatrix();
            int size = matrix.getSize();
            int edges = controller.countNonZeroEntries(matrix);
            System.out.println("  - " + label + ": " + size + " x " + size + " matrix, " + edges + " non-zero entries");
        }
    }
}
