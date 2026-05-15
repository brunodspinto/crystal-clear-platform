package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ExportDeclarationCsvController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

/**
 * UI for US24 - Export Declaration Dataset to CSV.
 */
public class ExportDeclarationCsvUI implements Runnable {

    private final ExportDeclarationCsvController controller;

    /**
     * Instantiates a new Export declaration csv ui.
     */
    public ExportDeclarationCsvUI() {
        controller = new ExportDeclarationCsvController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Export Declaration Dataset to CSV ---");

        String filePath = Utils.readLineFromConsole("Enter output file path (e.g. declarations.csv): ");
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        boolean success = controller.exportToCsv(filePath.trim());
        if (success) {
            System.out.println("\nExport successful. File saved to: " + filePath.trim());
        } else {
            System.out.println("\nExport failed. Please check the file path and try again.");
        }
    }
}
