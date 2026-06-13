package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.FindPathController;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * Console UI for US34 – Verify if there is a pathway between two entities
 * in the support graph and, if so, return the distance.
 *
 * <p>The user selects source and target entities from the list of all known
 * entity ids in the current temporal snapshot. The result states whether a
 * path exists and, when it does, the shortest distance (number of edges).</p>
 *
 * <p>The search is performed on the <em>support graph</em> — the undirected,
 * unweighted version of the relations graph where every directed edge becomes
 * a bidirectional link and all weights are discarded (AC2).</p>
 */
public class FindPathUI implements Runnable {

    private final FindPathController controller;

    /**
     * Instantiates a new Find path ui.
     */
    public FindPathUI() {
        controller = new FindPathController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- US34: Pathway Verification ---");
        System.out.println("Checks whether two entities are connected in the support graph");
        System.out.println("and returns the shortest distance (number of edges) if they are.");
        System.out.println("-----------------------------------------------------------------");

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

        if (entityIds.size() < 2) {
            System.out.println("\nThe graph must contain at least two entities"
                    + (useDate ? " active on " + date.trim() + "." : "."));
            return;
        }

        System.out.println("\nAvailable entities (" + entityIds.size() + "):");
        for (int i = 0; i < entityIds.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + entityIds.get(i));
        }

        int srcChoice = Utils.readIntegerFromConsole(
                "\nSelect source entity (1-" + entityIds.size() + "): ");
        int dstChoice = Utils.readIntegerFromConsole(
                "Select target entity (1-" + entityIds.size() + "): ");

        if (srcChoice < 1 || srcChoice > entityIds.size()
                || dstChoice < 1 || dstChoice > entityIds.size()) {
            System.out.println("\nInvalid selection.");
            return;
        }

        String sourceId = entityIds.get(srcChoice - 1);
        String targetId = entityIds.get(dstChoice - 1);

        FindPathController.PathResult result;
        try {
            result = useDate ? controller.findPath(date.trim(), sourceId, targetId)
                    : controller.findPath(sourceId, targetId);
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        System.out.println("\nSource : " + result.getSourceId());
        System.out.println("Target : " + result.getTargetId());

        if (result.hasPath()) {
            System.out.println("Result : Path EXISTS");
            System.out.println("Distance : " + result.getDistance() + " edge(s)");
        } else {
            System.out.println("Result : No path found between these two entities.");
        }
    }
}
