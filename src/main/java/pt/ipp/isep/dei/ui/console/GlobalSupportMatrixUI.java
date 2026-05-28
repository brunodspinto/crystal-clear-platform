package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.GlobalSupportMatrixController;
import pt.ipp.isep.dei.domain.graph.SupportGraph;
import pt.ipp.isep.dei.ui.console.utils.Utils;

/**
 * UI for US33 – show the global adjacency matrix of the support graph
 * (undirected, unweighted) for a temporal snapshot date chosen by the user.
 */
public class GlobalSupportMatrixUI implements Runnable {

    private static final int CELL_WIDTH = 8;

    private final GlobalSupportMatrixController controller;

    /**
     * Instantiates a new GlobalSupportMatrixUI.
     */
    public GlobalSupportMatrixUI() {
        controller = new GlobalSupportMatrixController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Global Support Adjacency Matrix (US33) ---");

        if (!controller.hasData()) {
            System.out.println("\nNo entities loaded. Please load entities from CSV first.");
            return;
        }

        String date = readDate();
        if (date == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        SupportGraph supportGraph;
        try {
            supportGraph = controller.buildFor(date);
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        printMatrix(supportGraph, date);
    }

    private String readDate() {
        while (true) {
            String date = Utils.readLineFromConsole("\nSnapshot date (yyyy-MM-dd, blank to cancel): ");
            if (date == null || date.isBlank()) {
                return null;
            }
            date = date.trim();
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Invalid format. Use yyyy-MM-dd.");
                continue;
            }
            return date;
        }
    }

    private void printMatrix(SupportGraph sg, String date) {
        int n = sg.size();
        System.out.println("\n=== Support graph at " + date + " (" + n + "x" + n + ") ===");

        if (n == 0) {
            System.out.println("(no active entities at this date)");
            return;
        }

        boolean[][] matrix = sg.getAdjacencyMatrix();

        StringBuilder header = new StringBuilder();
        header.append(pad(""));
        for (int j = 0; j < n; j++) {
            header.append(pad(sg.getRegistry().idAt(j)));
        }
        System.out.println(header.toString());

        for (int i = 0; i < n; i++) {
            StringBuilder row = new StringBuilder();
            row.append(pad(sg.getRegistry().idAt(i)));
            for (int j = 0; j < n; j++) {
                row.append(pad(matrix[i][j] ? "1" : "0"));
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
}
