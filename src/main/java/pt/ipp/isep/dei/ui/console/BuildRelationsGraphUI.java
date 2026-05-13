package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.BuildRelationsGraphController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;

public class BuildRelationsGraphUI implements Runnable {

    private static final String DEFAULT_OUTPUT_SVG = "docs/system-documentation/US20/us20_relations_graph.svg";

    private final BuildRelationsGraphController controller;

    public BuildRelationsGraphUI() {
        controller = new BuildRelationsGraphController();
    }

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
