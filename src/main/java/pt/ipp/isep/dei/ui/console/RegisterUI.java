package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RegisterController;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * UI for submitting a registration request (US01).
 */
public class RegisterUI implements Runnable {

    private final RegisterController controller;

    public RegisterUI() {
        controller = new RegisterController();
    }

    @Override
    public void run() {
        System.out.println("\n\n--- Register on the Platform -------------------------");

        UserRole role = displayAndSelectRole();
        if (role == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String fullName = Utils.readLineFromConsole("Full name: ");
        String email = Utils.readLineFromConsole("Email address: ");

        String password = requestValidPassword();
        if (password == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        String identificationDocument = null;
        if (controller.requiresDocument(role)) {
            identificationDocument = Utils.readLineFromConsole(controller.getDocumentLabel(role));
        }

        System.out.println("\n--- Confirm Registration ---");
        System.out.printf("Full name : %s%n", fullName);
        System.out.printf("Email     : %s%n", email);
        System.out.printf("Role      : %s%n", role);
        if (identificationDocument != null) {
            System.out.printf("Document  : %s%n", identificationDocument);
        }

        if (!Utils.confirm("Submit registration request? (s/n)")) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        try {
            boolean success = controller.submitRequest(fullName, email, password, role, identificationDocument);
            if (success) {
                System.out.println("\nRegistration request submitted successfully!");
                System.out.println("Your request is PENDING review by an Administrator.");
            } else {
                System.out.println("\nRegistration request not submitted: a request for this email and role already exists.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private UserRole displayAndSelectRole() {
        List<UserRole> roles = controller.getAvailableRoles();
        return (UserRole) Utils.showAndSelectOne(roles, "\nSelect your role:");
    }

    private String requestValidPassword() {
        System.out.println("\nPassword must have exactly 7 alphanumeric characters, at least 3 uppercase letters and at least 2 digits.");
        int attempts = 0;
        while (attempts < 3) {
            String password = Utils.readLineFromConsole("Password: ");
            if (!controller.isValidPassword(password)) {
                System.out.println("Invalid password. Please try again.");
                attempts++;
                continue;
            }
            String confirm = Utils.readLineFromConsole("Confirm password: ");
            if (!password.equals(confirm)) {
                System.out.println("Passwords do not match. Please try again.");
                attempts++;
                continue;
            }
            return password;
        }
        System.out.println("Too many failed attempts.");
        return null;
    }
}
