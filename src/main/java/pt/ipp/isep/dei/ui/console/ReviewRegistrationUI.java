package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ReviewRegistrationController;
import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * UI for US02 - Accept/Reject Registration Requests.
 */
public class ReviewRegistrationUI implements Runnable {

    private final ReviewRegistrationController controller;

    /**
     * Instantiates a new Review registration ui.
     */
    public ReviewRegistrationUI() {
        controller = new ReviewRegistrationController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Review Registration Requests ---");

        List<RegistrationRequest> pending = controller.getPendingRequests();
        if (pending.isEmpty()) {
            System.out.println("No pending registration requests.");
            return;
        }

        RegistrationRequest selected = (RegistrationRequest) Utils.showAndSelectOne(pending,
                "\nPending requests:");
        if (selected == null) return;

        printDetails(selected);

        int decision = Utils.showAndSelectIndex(
                List.of("Accept", "Reject"),
                "\nDecision:");

        if (decision == 0) {
            controller.approveRequest(selected);
            System.out.printf("%nRequest from %s (%s) ACCEPTED.%n",
                    selected.getFullName(), selected.getRole());
        } else if (decision == 1) {
            String reason = Utils.readLineFromConsole("Rejection reason (mandatory): ");
            while (reason == null || reason.isBlank()) {
                System.out.println("Reason cannot be empty.");
                reason = Utils.readLineFromConsole("Rejection reason (mandatory): ");
            }
            controller.rejectRequest(selected, reason);
            System.out.printf("%nRequest from %s (%s) REJECTED.%n",
                    selected.getFullName(), selected.getRole());
        }
    }

    private void printDetails(RegistrationRequest r) {
        System.out.println("\n--- Request Details ---");
        System.out.printf("Name    : %s%n", r.getFullName());
        System.out.printf("Email   : %s%n", r.getEmail());
        System.out.printf("Role    : %s%n", r.getRole());
        System.out.printf("Submitted: %s%n", r.getSubmissionDate());
        if (r.getRole() == UserRole.JOURNALIST) {
            System.out.printf("Press card: %s%n", r.getIdentificationDocument());
        } else if (r.getRole() == UserRole.CITIZEN) {
            System.out.printf("National ID: %s%n", r.getIdentificationDocument());
        }
    }
}
