package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ReviewRegistrationController;
import pt.ipp.isep.dei.dto.RegistrationRequestDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
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

        try {
            List<RegistrationRequestDTO> pending = controller.getPendingRequestsAsDTO();
            if (pending.isEmpty()) {
                System.out.println("No pending registration requests.");
                return;
            }

            RegistrationRequestDTO selected = (RegistrationRequestDTO) Utils.showAndSelectOne(pending,
                    "\nPending requests:");
            if (selected == null) return;

            printDetails(selected);

            List<String> options = new ArrayList<>();
            options.add("Accept");
            options.add("Reject");
            int decision = Utils.showAndSelectIndex(options, "\nDecision:");

            if (decision == 0) {
                controller.approveRequestByEmail(selected.getEmail());
                System.out.printf("%nRequest from %s (%s) ACCEPTED.%n",
                        selected.getFullName(), selected.getRole());
            } else if (decision == 1) {
                String reason = Utils.readLineFromConsole("Rejection reason (mandatory): ");
                while (reason == null || reason.isBlank()) {
                    System.out.println("Reason cannot be empty.");
                    reason = Utils.readLineFromConsole("Rejection reason (mandatory): ");
                }
                controller.rejectRequestByEmail(selected.getEmail(), reason);
                System.out.printf("%nRequest from %s (%s) REJECTED.%n",
                        selected.getFullName(), selected.getRole());
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void printDetails(RegistrationRequestDTO r) {
        System.out.println("\n--- Request Details ---");
        System.out.printf("Name    : %s%n", r.getFullName());
        System.out.printf("Email   : %s%n", r.getEmail());
        System.out.printf("Role    : %s%n", r.getRole());
        System.out.printf("Submitted: %s%n", r.getSubmissionDate());
        String docLabel = r.getRole().getDocumentLabel();
        if (docLabel != null) {
            System.out.printf("%s%s%n", docLabel, r.getIdentificationDocument());
        }
    }
}
