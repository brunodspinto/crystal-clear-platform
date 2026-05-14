package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.BuildRelationsGraphController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;

/**
 * UI for US20 - Build the relations graph from a CSV file. After the graph
 * is built the user is offered the chance to render it to an SVG via
 * Graphviz.
 */
public class BuildRelationsGraphUI implements Runnable {

    private static final String DEFAULT_OUTPUT_SVG = "docs/system-documentation/US20/us20_relations_graph.svg";

    private final BuildRelationsGraphController controller;

    /**
     * Creates the UI and initialises the controller using the singleton repository.
     */
    public BuildRelationsGraphUI() {
        controller = new BuildRelationsGraphController();
    }

    /**
     * Runs the flow: read the CSV path, build the graph, print a summary and
     * optionally render the graph to an SVG using Graphviz.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Build Relations Graph (US20) ---");

        String filePath = Utils.readLineFromConsole("Enter path to relations CSV file: ");
        if (filePath == null || filePath.isBlank()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            BuildRelationsGraphController.BuildResult result = controller.buildFromCsv(filePath);
            System.out.println("\nRelations graph built successfully.");
            System.out.println("  Edges loaded: " + result.getEdgeCount());
            System.out.println("  Nodes registered: " + result.getNodeCount());
            System.out.println("  Distinct relation labels: " + result.getLabels().size());
            for (String label : result.getLabels()) {
                System.out.println("    - " + label);
            }
        } catch (IOException e) {
            System.out.println("\nFailed to load relations: " + e.getMessage());
            return;
        }

        if (Utils.confirm("Render the graph to an SVG file using Graphviz? (y/n)")) {
            try {
                String svgPath = controller.renderGraphToSvg(DEFAULT_OUTPUT_SVG);
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
