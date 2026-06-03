package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ExportDeclarationGraphController;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UI for US37 - Build the declaration interest graph and export its entities
 * and relations to CSV, inferring family ties (symmetric, inverse and
 * transitive closures) from an optional seed of declared kinships.
 */
public class ExportDeclarationGraphUI implements Runnable {

    private final ExportDeclarationGraphController controller;

    /**
     * Instantiates a new ExportDeclarationGraphUI.
     */
    public ExportDeclarationGraphUI() {
        controller = new ExportDeclarationGraphController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Export Declaration Interest Graph (US37) ---");

        if (!controller.hasData()) {
            System.out.println("\nNo validated declarations to export.");
            return;
        }

        List<Edge> familySeed = readFamilySeed();
        if (familySeed == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String entitiesPath = Utils.readLineFromConsole("\nEntities CSV output path (e.g. entities.csv): ");
        if (entitiesPath == null || entitiesPath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String relationsPath = Utils.readLineFromConsole("Relations CSV output path (e.g. relations.csv): ");
        if (relationsPath == null || relationsPath.trim().isEmpty()) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        int inferred = controller.inferFamilyRelationships(familySeed).size() - familySeed.size();
        boolean success = controller.export(familySeed, entitiesPath.trim(), relationsPath.trim());

        if (success) {
            System.out.println("\nExport successful.");
            System.out.println("Entities : " + entitiesPath.trim());
            System.out.println("Relations: " + relationsPath.trim());
            if (!familySeed.isEmpty()) {
                System.out.println("Family ties: " + familySeed.size() + " seed + "
                        + inferred + " inferred.");
            }
        } else {
            System.out.println("\nExport failed (I/O error).");
        }
    }

    /**
     * Optionally reads a family-seed CSV file. Returns an empty list when the
     * user skips it, or {@code null} when the file could not be read (which the
     * caller treats as a cancellation).
     */
    private List<Edge> readFamilySeed() {
        String path = Utils.readLineFromConsole(
                "\nFamily relationships CSV (blank to skip): ");
        if (path == null || path.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return controller.loadFamilySeed(path.trim());
        } catch (IOException e) {
            System.out.println("Could not read file: " + e.getMessage());
            return null;
        }
    }
}
