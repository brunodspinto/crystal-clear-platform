package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.AssessComplaintController;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.ComplaintOutcome;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Arrays;
import java.util.List;

/**
 * UI for assessing a citizen complaint (US27).
 * Guides the Ethics Committee Member through selecting a complaint,
 * choosing valid or invalid, and providing a reason if invalid.
 */
public class AssessComplaintUI implements Runnable {

    private final AssessComplaintController controller;

    /**
     * Instantiates a new AssessComplaintUI.
     */
    public AssessComplaintUI() {
        controller = new AssessComplaintController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Assess Complaint (US27) -------------------------");

        List<Complaint> complaints = controller.getComplaints();
        if (complaints.isEmpty()) {
            System.out.println("\nNo complaints available.");
            return;
        }

        Complaint selected = (Complaint) Utils.showAndSelectOne(complaints, "Select a complaint to assess:");
        if (selected == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        System.out.println("\n--- Complaint Details ---");
        System.out.printf("Agent     : %s%n", selected.getPoliticalAgent().getName());
        System.out.printf("Function  : %s%n", selected.getPoliticalFunction());
        System.out.printf("Date      : %s%n", selected.getComplaintDate());
        System.out.printf("Submitted : %s%n", selected.getSubmissionDate());
        System.out.printf("Citizen   : %s%n", selected.getCitizen().getName());
        System.out.printf("Description: %s%n", selected.getDescription());

        ComplaintOutcome outcome = (ComplaintOutcome) Utils.showAndSelectOne(
                Arrays.asList(ComplaintOutcome.values()), "Select outcome:");
        if (outcome == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String reason = null;
        if (outcome == ComplaintOutcome.INVALID) {
            reason = Utils.readLineFromConsole("Reason for INVALID classification: ");
        }

        if (!Utils.confirm("Confirm assessment? (y/n)")) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            boolean success = controller.assessComplaint(selected, outcome, reason);
            if (success) {
                System.out.printf("%nComplaint assessed as %s.%n", outcome);
            } else {
                System.out.println("\nOperation failed: authenticated member not found.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid data: " + e.getMessage());
        }
    }
}
