package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ExportHoldingsCsvController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

/**
 * UI for US25 - Export Holdings Dataset to CSV.
 */
public class ExportHoldingsCsvUI implements Runnable {

    private final ExportHoldingsCsvController controller;

    public ExportHoldingsCsvUI() {
        controller = new ExportHoldingsCsvController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Export Holdings Dataset to CSV ---");

        String filePath = Utils.readLineFromConsole("Enter output file path (e.g. holdings.csv): ");
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
