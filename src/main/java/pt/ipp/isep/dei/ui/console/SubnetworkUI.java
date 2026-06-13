package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.SubnetworkController;
import pt.ipp.isep.dei.domain.graph.SubnetworkExtractor.SubnetworkResult;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * Console UI for US36 – Visualise the subnetwork where a given entity can
 * integrate chains of influence.
 *
 * <p>The user selects an origin entity from the list of all known entities.
 * The subnetwork (connected component in the support graph that contains the
 * chosen entity) is extracted, listed on screen, and optionally exported to
 * an SVG file for visual inspection.</p>
 *
 * <p>The extraction uses only primitive operations (AC2). The SVG export is
 * handled by Graphviz, which is explicitly exempt per the AC.</p>
 */
public class SubnetworkUI implements Runnable {

    private final SubnetworkController controller;

    /**
     * Instantiates a new Subnetwork ui.
     */
    public SubnetworkUI() {
        controller = new SubnetworkController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- US36: Influence Subnetwork Visualisation ---");
        System.out.println("Extracts the connected subnetwork that contains a chosen entity.");
        System.out.println("All entities in this subnetwork can participate in influence chains");
        System.out.println("that pass through the chosen entity.");
        System.out.println("----------------------------------------------------------");

        String date = Utils.readLineFromConsole(
                "\nSnapshot date (yyyy-MM-dd), or leave blank for the full network: ");
        boolean useDate = date != null && !date.trim().isEmpty();

        List<String> entityIds;
        try {
            entityIds = useDate ? controller.getEntityIds(date.trim())
                    : controller.getEntityIds();
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        if (entityIds.isEmpty()) {
            System.out.println("\nNo entities available"
                    + (useDate ? " on " + date.trim() + "." : " in the current graph."));
            return;
        }

        System.out.println("\nAvailable entities (" + entityIds.size() + "):");
        for (int i = 0; i < entityIds.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + entityIds.get(i));
        }

        int choice = Utils.readIntegerFromConsole(
                "\nSelect origin entity (1-" + entityIds.size() + "): ");

        if (choice < 1 || choice > entityIds.size()) {
            System.out.println("\nInvalid selection.");
            return;
        }

        String originId = entityIds.get(choice - 1);

        SubnetworkResult result;
        try {
            result = useDate ? controller.extractSubnetwork(date.trim(), originId)
                    : controller.extractSubnetwork(originId);
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        // Print summary
        System.out.println("\nOrigin entity  : " + result.getOriginId());
        System.out.println("Subnetwork size: " + result.size() + " entity/entities\n");
        System.out.println("Entities in subnetwork:");
        for (int i = 0; i < result.size(); i++) {
            String marker = result.getNodeId(i).equals(originId) ? " ← origin" : "";
            System.out.println("  " + (i + 1) + ". " + result.getNodeId(i) + marker);
        }

        // Print adjacency in the subnetwork
        System.out.println("\nAdjacency in subnetwork (support graph):");
        for (int i = 0; i < result.size(); i++) {
            for (int j = i + 1; j < result.size(); j++) {
                if (result.isAdjacent(i, j)) {
                    System.out.println("  " + result.getNodeId(i)
                            + "  --  " + result.getNodeId(j));
                }
            }
        }

        // Optional SVG export
        String exportChoice = Utils.readLineFromConsole(
                "\nExport subnetwork to SVG? (y/n): ");

        if (!"y".equalsIgnoreCase(exportChoice.trim())) {
            return;
        }

        String entitiesCsv = Utils.readLineFromConsole("Entities CSV file path: ");
        String outputSvg   = Utils.readLineFromConsole("Output SVG file path (e.g. subnetwork.svg): ");

        if (entitiesCsv == null || entitiesCsv.isBlank()
                || outputSvg == null || outputSvg.isBlank()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            controller.exportToSvg(result, entitiesCsv.trim(), outputSvg.trim());
            System.out.println("\nSubnetwork SVG exported to: " + outputSvg.trim());
        } catch (Exception e) {
            System.out.println("\nExport failed: " + e.getMessage());
        }
    }
}
