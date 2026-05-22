package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.DetectConflictsController;
import pt.ipp.isep.dei.domain.graph.ConflictDetector.Chain;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * Console UI for US23 – Detect potential indirect conflicts of interest
 * and indirect nepotism through multi-step relation chains.
 *
 * <p>Unlike US22 (which checks a single direct appointment edge), US23
 * traverses multi-hop paths in the graph to surface situations that are
 * not obvious from individual declarations:</p>
 * <ul>
 *   <li><strong>Indirect nepotism</strong> – a relative holds a position,
 *       or holds a position inside a specific organisation.</li>
 *   <li><strong>Conflict of interest</strong> – a public official influences
 *       a private company, is associated with an asset owner, or was
 *       appointed by a member of an organisation.</li>
 * </ul>
 *
 * <p>The user selects one of the predefined questions (AC1). For every
 * detected chain the first and last entities are shown (AC2).</p>
 */
public class DetectConflictsUI implements Runnable {

    /**
     * Short category labels shown next to each question number so the user
     * immediately understands what type of situation each query looks for.
     * Order must match {@link DetectConflictsController} QUERY_* constants.
     */
    private static final String[] QUERY_CATEGORIES = {
        "[Indirect nepotism]      ",
        "[Indirect nepotism]      ",
        "[Conflict of interest]   ",
        "[Conflict of interest]   ",
        "[Conflict of interest]   "
    };

    private final DetectConflictsController controller;

    /**
     * Instantiates a new Detect conflicts ui.
     */
    public DetectConflictsUI() {
        controller = new DetectConflictsController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- US23: Indirect Nepotism & Conflicts of Interest ---");
        System.out.println("Analyses multi-hop relation chains in the loaded graph.");
        System.out.println("Select the type of situation to investigate:");
        System.out.println("-------------------------------------------------------");

        List<String> questions = controller.getAvailableQuestions();
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("  " + (i + 1) + ". "
                    + QUERY_CATEGORIES[i] + questions.get(i));
        }
        System.out.println();

        int choice = Utils.readIntegerFromConsole(
                "Enter question number (1-" + questions.size() + "): ");
        int queryIndex = choice - 1;

        if (queryIndex < 0 || queryIndex >= questions.size()) {
            System.out.println("\nInvalid selection. Please choose a number between 1 and "
                    + questions.size() + ".");
            return;
        }

        // Q2 needs an optional organisation filter
        String organisationId = null;
        if (queryIndex == DetectConflictsController.QUERY_RELATIVES_IN_ORGANISATION) {
            organisationId = Utils.readLineFromConsole(
                    "Enter organisation ID to filter (leave blank for any): ");
        }

        List<Chain> chains;
        try {
            chains = controller.runQuery(queryIndex, organisationId);
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        System.out.println("\nQuestion : " + questions.get(queryIndex));
        System.out.println("Category : " + QUERY_CATEGORIES[queryIndex].trim());

        if (chains.isEmpty()) {
            System.out.println("Result   : No situations detected for this query.");
            return;
        }

        System.out.println("Result   : " + chains.size() + " chain(s) detected.\n");

        for (int i = 0; i < chains.size(); i++) {
            Chain chain = chains.get(i);
            System.out.println("  Chain #" + (i + 1) + "  " + chain);
            System.out.println("    First entity (origin)      : " + chain.getFirst());
            System.out.println("    Last  entity (destination) : " + chain.getLast());
            if (chain.hasContext()) {
                System.out.println("    Context                    : " + chain.getContext());
            }
        }
    }
}
