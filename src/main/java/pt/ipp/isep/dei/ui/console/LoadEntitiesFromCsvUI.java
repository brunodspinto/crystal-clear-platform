package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.LoadEntitiesFromCsvController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;

/**
 * The type Load entities from csv ui.
 */
public class LoadEntitiesFromCsvUI implements Runnable {

    private final LoadEntitiesFromCsvController controller;

    /**
     * Instantiates a new Load entities from csv ui.
     */
    public LoadEntitiesFromCsvUI() {
        controller = new LoadEntitiesFromCsvController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Load Entities from CSV (US19) ---");

        String filePath = Utils.readLineFromConsole("Enter path to entities CSV file: ");
        if (filePath == null || filePath.isBlank()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            int count = controller.loadEntities(filePath);
            System.out.printf("\n%d entities loaded successfully.%n", count);
        } catch (IOException e) {
            System.out.println("\nFailed to load entities: " + e.getMessage());
        }
    }
}
