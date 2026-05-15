package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.DetectNepotismController;
import pt.ipp.isep.dei.domain.graph.NepotismDetector.NepotismPair;

import java.util.List;

/**
 * Console UI for US22 – Find out if someone was appointed by a relative,
 * friend, or associate (direct nepotism detection).
 *
 * <p>Runs the detection automatically on the loaded relations graph and
 * lists every flagged pair. No user input is required beyond navigating
 * to this menu option.</p>
 *
 * <p>A pair is flagged when <strong>all</strong> of the following hold:</p>
 * <ul>
 *   <li>Person B was appointed by person A ({@code appointedBy} edge).</li>
 *   <li>A and B share at least one personal tie:
 *       {@code relativeOf}, {@code friendOf}, or {@code associatedWith}.</li>
 * </ul>
 */
public class DetectNepotismUI implements Runnable {

    private final DetectNepotismController controller;

    /**
     * Instantiates a new Detect nepotism ui.
     */
    public DetectNepotismUI() {
        controller = new DetectNepotismController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- US22: Direct Nepotism Detection ---");
        System.out.println("Checks whether any person was appointed by a relative,");
        System.out.println("friend, or close associate. All matching pairs are listed.");
        System.out.println("Personal ties checked: relativeOf | friendOf | associatedWith");
        System.out.println("-------------------------------------------------------");

        List<NepotismPair> pairs;
        try {
            pairs = controller.detect();
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        if (pairs.isEmpty()) {
            System.out.println("\nResult: No direct nepotism detected in the current graph.");
            return;
        }

        System.out.println("\nResult: " + pairs.size()
                + " direct nepotism pair(s) detected.\n");

        for (int i = 0; i < pairs.size(); i++) {
            NepotismPair pair = pairs.get(i);
            System.out.println("  Pair #" + (i + 1));
            System.out.println("    Appointer      : " + pair.getAppointer()
                    + "  (the person who made the appointment)");
            System.out.println("    Appointed      : " + pair.getAppointed()
                    + "  (the person who was appointed)");
            System.out.println("    Personal tie   : " + pair.getRelationshipLabel());
        }
    }
}
