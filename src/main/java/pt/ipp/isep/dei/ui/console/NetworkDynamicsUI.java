package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.NetworkDynamicsController;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UI for US32: shows the network dynamics over time.
 * The user provides a list of dates, either typed in the console or loaded
 * from a CSV file; for each date a snapshot of active entities and relations
 * is displayed.
 */
public class NetworkDynamicsUI implements Runnable {

    private final NetworkDynamicsController controller;

    /**
     * Instantiates a new NetworkDynamicsUI.
     */
    public NetworkDynamicsUI() {
        controller = new NetworkDynamicsController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Network Dynamics Over Time (US32) ---");

        if (!controller.hasData()) {
            System.out.println("\nNo entities loaded. Please load entities from CSV first.");
            return;
        }

        List<String> dates = collectDates();
        if (dates.isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Collections.sort(dates);

        List<NetworkSnapshot> snapshots = controller.buildSnapshots(dates);
        displaySnapshots(snapshots);
    }

    private List<String> collectDates() {
        List<String> options = new ArrayList<>();
        options.add("Enter the dates in the console");
        options.add("Load the dates from a CSV file");
        int option = Utils.showAndSelectIndex(options,
                "\nHow do you want to provide the snapshot dates?");
        if (option == 0) {
            return collectDatesFromConsole();
        }
        if (option == 1) {
            return collectDatesFromCsv();
        }
        return new ArrayList<>();
    }

    private List<String> collectDatesFromConsole() {
        List<String> dates = new ArrayList<>();
        System.out.println("\nEnter snapshot dates in yyyy-MM-dd format.");
        do {
            String date = Utils.readLineFromConsole("Date: ");
            if (date == null || date.isBlank()) {
                System.out.println("Date cannot be blank. Skipping.");
            } else if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Invalid format. Use yyyy-MM-dd. Skipping.");
            } else {
                dates.add(date.trim());
            }
        } while (Utils.confirm("Add another date? (y/n)"));
        return dates;
    }

    private List<String> collectDatesFromCsv() {
        String path = Utils.readLineFromConsole(
                "Path of the CSV file (header \"SnapshotDate\", one date per line): ");
        if (path == null || path.isBlank()) {
            System.out.println("File path cannot be blank.");
            return new ArrayList<>();
        }
        try {
            List<String> dates = controller.loadDatesFromCsv(path.trim());
            if (dates.isEmpty()) {
                System.out.println("No valid dates were found in the file.");
            } else {
                System.out.println(dates.size() + " date(s) loaded from the file.");
            }
            return dates;
        } catch (IOException e) {
            System.out.println("Could not read the file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void displaySnapshots(List<NetworkSnapshot> snapshots) {
        System.out.println("\n========================================");
        System.out.println("  NETWORK DYNAMICS — " + snapshots.size() + " SNAPSHOT(S)");
        System.out.println("========================================");

        for (NetworkSnapshot snap : snapshots) {
            System.out.println("\n--- Snapshot: " + snap.getDate() + " ---");
            System.out.println("  Active entities : " + snap.getEntityCount());
            System.out.println("  Active relations: " + snap.getEdgeCount());

            if (snap.getEntityCount() > 0) {
                System.out.println("\n  Entities by type:");
                printCountByType(snap.getActiveEntities());
            }

            if (snap.getEdgeCount() > 0) {
                System.out.println("\n  Relations by label:");
                printCountByLabel(snap.getActiveEdges());
            }

            if (snap.getEntityCount() == 0 && snap.getEdgeCount() == 0) {
                System.out.println("  (no active entities or relations at this date)");
            }
        }
        System.out.println("\n========================================");
    }

    private void printCountByType(List<Entity> entities) {
        List<String> seen = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Entity e : entities) {
            String type = e.getClass().getSimpleName();
            int idx = seen.indexOf(type);
            if (idx < 0) {
                seen.add(type);
                counts.add(1);
            } else {
                counts.set(idx, counts.get(idx) + 1);
            }
        }
        for (int i = 0; i < seen.size(); i++) {
            System.out.printf("    %-20s %d%n", seen.get(i), counts.get(i));
        }
    }

    private void printCountByLabel(List<Edge> edges) {
        List<String> seen = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Edge e : edges) {
            int idx = seen.indexOf(e.getLabel());
            if (idx < 0) {
                seen.add(e.getLabel());
                counts.add(1);
            } else {
                counts.set(idx, counts.get(idx) + 1);
            }
        }
        for (int i = 0; i < seen.size(); i++) {
            System.out.printf("    %-25s %d%n", seen.get(i), counts.get(i));
        }
    }
}
