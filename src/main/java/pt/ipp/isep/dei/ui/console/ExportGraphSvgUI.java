package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ExportGraphSvgController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

/**
 * UI for US26 - Export heterogeneous graph as interactive SVG with hyperlinks.
 */
public class ExportGraphSvgUI implements Runnable {

    private final ExportGraphSvgController controller;

    public ExportGraphSvgUI() {
        controller = new ExportGraphSvgController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Export Graph to SVG (with Hyperlinks) ---");

        String entitiesPath = Utils.readLineFromConsole("Entities CSV file path: ");
        if (entitiesPath == null || entitiesPath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String relationsPath = Utils.readLineFromConsole("Relations CSV file path: ");
        if (relationsPath == null || relationsPath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String outputPath = Utils.readLineFromConsole("Output SVG file path (e.g. graph.svg): ");
        if (outputPath == null || outputPath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            int count = controller.exportToSvg(
                    entitiesPath.trim(), relationsPath.trim(), outputPath.trim());
            System.out.printf("%nSVG exported successfully with %d entities. File: %s%n",
                    count, outputPath.trim());
        } catch (Exception e) {
            System.out.println("\nExport failed: " + e.getMessage());
        }
    }
}
