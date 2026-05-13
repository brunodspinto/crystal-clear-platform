package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.GenerateAdjacencyMatricesController;
import pt.ipp.isep.dei.controller.LabeledAdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

import java.util.List;

public class GenerateAdjacencyMatricesUI implements Runnable {

    private static final int CELL_WIDTH = 10;

    private final GenerateAdjacencyMatricesController controller;

    public GenerateAdjacencyMatricesUI() {
        controller = new GenerateAdjacencyMatricesController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Generate Adjacency Matrices (US21) ---");

        GenerateAdjacencyMatricesController.GenerationResult result;
        try {
            result = controller.generate();
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        List<LabeledAdjacencyMatrix> matrices = result.getMatrices();
        List<String> nodeIds = result.getNodeIds();

        if (matrices.isEmpty()) {
            System.out.println("\nThe relations graph has no edges. No matrices were generated.");
            return;
        }

        System.out.println("\nGenerated " + matrices.size() + " adjacency matrices ("
                + nodeIds.size() + " x " + nodeIds.size() + " each).");

        for (LabeledAdjacencyMatrix entry : matrices) {
            printMatrix(entry.getLabel(), entry.getMatrix(), nodeIds);
        }

        AdjacencyMatrix global = result.getGlobalMatrix();
        if (global != null) {
            printMatrix("GLOBAL (sum of all relations)", global, nodeIds);
        }
    }

    private void printMatrix(String label, AdjacencyMatrix matrix, List<String> nodeIds) {
        int n = matrix.getSize();
        int nonZero = controller.countNonZeroEntries(matrix);
        System.out.println("\n=== Matrix: " + label + " (" + n + "x" + n + ", " + nonZero + " non-zero) ===");

        StringBuilder header = new StringBuilder();
        header.append(pad(""));
        for (int j = 0; j < n; j++) {
            header.append(pad(nodeIds.get(j)));
        }
        System.out.println(header.toString());

        for (int i = 0; i < n; i++) {
            StringBuilder row = new StringBuilder();
            row.append(pad(nodeIds.get(i)));
            for (int j = 0; j < n; j++) {
                double w = matrix.getWeight(i, j);
                row.append(pad(formatWeight(w)));
            }
            System.out.println(row.toString());
        }
    }

    private String pad(String s) {
        if (s.length() >= CELL_WIDTH) {
            return s.substring(0, CELL_WIDTH - 1) + " ";
        }
        StringBuilder out = new StringBuilder(s);
        while (out.length() < CELL_WIDTH) {
            out.append(" ");
        }
        return out.toString();
    }

    private String formatWeight(double w) {
        if (w == 0) {
            return "0";
        }
        if (w == (long) w) {
            return Long.toString((long) w);
        }
        return String.format("%.2f", w);
    }
}
