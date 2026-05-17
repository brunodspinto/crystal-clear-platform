package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.LoadEntitiesFromCsvController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;

/**
 * UI for US19 - Load entities from a CSV file into the graph.
 * After loading, the user is offered the chance to render the entity graph to an SVG via Graphviz.
 */
public class LoadEntitiesFromCsvUI implements Runnable {

    private static final String DEFAULT_OUTPUT_SVG = "docs/system-documentation/US19/us19_entities_graph.svg";

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
            return;
        }

        if (Utils.confirm("Render the entity graph to an SVG file using Graphviz? (y/n)")) {
            try {
                String svgPath = controller.renderEntitiesToSvg(DEFAULT_OUTPUT_SVG);
                System.out.println("\nGraph rendered: " + svgPath);
                System.out.println("Open the file in any browser or image viewer.");
            } catch (IOException e) {
                System.out.println("\nFailed to write the DOT file: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("\n" + e.getMessage());
            }
        }
    }
}
