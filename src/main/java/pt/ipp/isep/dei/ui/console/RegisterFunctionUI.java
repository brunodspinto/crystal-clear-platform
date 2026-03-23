package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RegisterFunctionController;
import pt.ipp.isep.dei.domain.Function;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Optional;

/**
 * UI for registering a new Function (US05).
 */
public class RegisterFunctionUI implements Runnable {

    private final RegisterFunctionController controller;

    public RegisterFunctionUI() {
        controller = new RegisterFunctionController();
    }

    public void run() {
        System.out.println("\n\n--- Register Function ------------------------");

        String designation = Utils.readLineFromConsole("Enter the function designation (e.g., Mayor, Minister): ");

        try {
            Optional<Function> function = controller.registerFunction(designation);

            if (function.isPresent()) {
                System.out.println("\nFunction successfully registered!");
            } else {
                System.out.println("\nFunction registration failed (it might already exist).");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }
}