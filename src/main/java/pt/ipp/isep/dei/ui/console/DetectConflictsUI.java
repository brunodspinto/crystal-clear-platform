package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.DetectConflictsController;
import pt.ipp.isep.dei.domain.graph.ConflictDetector.Chain;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * Console UI for US23 – Detect potential (indirect) conflicts of interest.
 *
 * <p>The user selects one of the predefined questions (AC1). After the query
 * runs, each detected chain is printed showing its full path and, clearly
 * labelled, its first and last entities (AC2).</p>
 */
public class DetectConflictsUI implements Runnable {

    private final DetectConflictsController controller;

    /**
     * Instantiates a new Detect conflicts ui.
     */
    public DetectConflictsUI() {
        controller = new DetectConflictsController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Detect Conflicts of Interest (US23) ---");

        // show the available questions and let the user pick one
        List<String> questions = controller.getAvailableQuestions();
        System.out.println("\nSelect a question:");
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + questions.get(i));
        }

        int choice = Utils.readIntegerFromConsole("Enter question number (1-" + questions.size() + "): ");
        int queryIndex = choice - 1;

        if (queryIndex < 0 || queryIndex >= questions.size()) {
            System.out.println("\nInvalid selection. Please choose a number between 1 and " + questions.size() + ".");
            return;
        }

        // Q2 needs an optional organisation filter
        String organisationId = null;
        if (queryIndex == DetectConflictsController.QUERY_RELATIVES_IN_ORGANISATION) {
            organisationId = Utils.readLineFromConsole(
                    "Enter organisation ID to filter (leave blank for any): ");
        }

        // run the query
        List<Chain> chains;
        try {
            chains = controller.runQuery(queryIndex, organisationId);
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        // display results
        System.out.println("\nQuestion: " + questions.get(queryIndex));
        if (chains.isEmpty()) {
            System.out.println("No conflicts detected for this query.");
            return;
        }

        System.out.println("Detected chains (" + chains.size() + "):\n");
        for (int i = 0; i < chains.size(); i++) {
            Chain chain = chains.get(i);
            System.out.println("  Chain #" + (i + 1) + ": " + chain);
            System.out.println("    First entity : " + chain.getFirst());
            System.out.println("    Last  entity : " + chain.getLast());
        }
    }
}
