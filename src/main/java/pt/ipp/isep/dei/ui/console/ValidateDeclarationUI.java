package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ValidateDeclarationController;
import pt.ipp.isep.dei.domain.ValidationOutcome;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * UI for validating a Declaration of Interests (US08).
 * Guides the Ethics Committee Member through selecting a pending declaration,
 * reviewing its content, selecting an outcome, and optionally adding section comments.
 */
public class ValidateDeclarationUI implements Runnable {

    private final ValidateDeclarationController controller;

    /**
     * Instantiates a new Validate declaration ui.
     */
    public ValidateDeclarationUI() {
        controller = new ValidateDeclarationController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Validate Declaration of Interests -------------------------");

        List<DeclarationDTO> pending = controller.getPendingDeclarations();
        if (pending.isEmpty()) {
            System.out.println("\nNo declarations pending validation.");
            return;
        }

        DeclarationDTO selected = (DeclarationDTO) Utils.showAndSelectOne(pending,
                "Select a declaration to review:");
        if (selected == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        System.out.println("\n--- Declaration Details ---");
        System.out.println(controller.getDeclarationDetails(selected.getId()));

        ValidationOutcome outcome = (ValidationOutcome) Utils.showAndSelectOne(
                controller.getValidationOutcomes(), "Select outcome:");
        if (outcome == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        List<Object[]> comments = new ArrayList<>();

        if (outcome == ValidationOutcome.RETURNED_FOR_CORRECTION) {
            if (!Utils.confirm("Confirm rejection? (y/n)")) {
                System.out.println("\nOperation cancelled.");
                return;
            }
            collectComments(comments);
        } else {
            if (!Utils.confirm("Confirm validation? (y/n)")) {
                System.out.println("\nOperation cancelled.");
                return;
            }
        }

        try {
            boolean success = controller.processValidation(selected.getId(), outcome, comments);

            if (success) {
                if (outcome == ValidationOutcome.VALIDATED) {
                    System.out.println("\nDeclaration successfully validated. Status: VALIDATED.");
                } else {
                    System.out.printf("\nDeclaration rejected with %d comment(s). Status: REJECTED.%n",
                            comments.size());
                    System.out.println("Declaration returned to Political Agent for correction.");
                }
            } else {
                System.out.println("\nOperation failed. Declaration may no longer be in PENDING status " +
                        "or authenticated member not found.");
            }
        }catch (IllegalArgumentException e) {
            System.out.println("\nInvalid validation data: " + e.getMessage());
            System.out.println("Operation cancelled.");
            }
    }

    /**
     * Collects one or more section comments from the Ethics Committee Member (AC2).
     * The loop allows multiple sections to be commented independently.
     *
     * @param comments the list to which collected comments will be added.
     */
    private void collectComments(List<Object[]> comments) {
        System.out.println("\n--- Add Comments for Inconsistent Sections ---");
        do {
            String section = Utils.readLineFromConsole("Section: ");
            String comment = Utils.readLineFromConsole("Comment: ");
            comments.add(new Object[]{section, comment});
        } while (Utils.confirm("Add comment for another section? (y/n)"));
    }
}
