package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.DetectNepotismController;
import pt.ipp.isep.dei.domain.graph.NepotismDetector.NepotismPair;

import java.util.List;

/**
 * Console UI for US22 – Find out if someone was appointed by a relative,
 * friend, or associate.
 *
 * <p>Runs the detection automatically and prints every pair. No user input
 * is required beyond navigation to this menu option.</p>
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
        System.out.println("\n\n--- Direct Nepotism Detection (US22) ---");

        List<NepotismPair> pairs;
        try {
            pairs = controller.detect();
        } catch (IllegalStateException e) {
            System.out.println("\n" + e.getMessage());
            return;
        }

        if (pairs.isEmpty()) {
            System.out.println("\nNo direct nepotism detected in the current graph.");
            return;
        }

        System.out.println("\nPairs involved in direct nepotism (" + pairs.size() + "):\n");
        for (int i = 0; i < pairs.size(); i++) {
            NepotismPair pair = pairs.get(i);
            System.out.println("  Pair #" + (i + 1));
            System.out.println("    Appointer    : " + pair.getAppointer());
            System.out.println("    Appointed    : " + pair.getAppointed());
            System.out.println("    Relationship : " + pair.getRelationshipLabel());
        }
    }
}
