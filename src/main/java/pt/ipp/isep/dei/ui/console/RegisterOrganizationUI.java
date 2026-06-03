package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RegisterOrganizationController;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

/**
 * UI for registering a new organization (US04).
 * Collects the organization type, name and nature from the admin,
 * confirms the data and delegates registration to {@link RegisterOrganizationController}.
 */
public class RegisterOrganizationUI implements Runnable {
    private final RegisterOrganizationController controller;
    private OrganizationType selectedType;
    private OrganizationNature selectedNature;
    private String name;

    /**
     * Creates the UI and initializes the controller.
     */
    public RegisterOrganizationUI() {
        controller = new RegisterOrganizationController();
    }

    /**
     * Runs the registration flow: selects type, collects data, confirms and submits.
     */
    public void run() {
        System.out.println("\n\n--- Register Organization -------------------------");

        selectedType = displayAndSelectOrganizationType();
        if (selectedType == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        selectedNature = displayAndSelectOrganizationNature();
        if (selectedNature == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        requestData();

        System.out.println("\n--- Confirm Data ---");
        System.out.printf("Name   : %s%n", name);
        System.out.printf("Nature : %s%n", selectedNature);
        System.out.printf("Type   : %s%n", selectedType);

        if (Utils.confirm("Confirm registration? (y/n)")) {
            submitData();
        } else {
            System.out.println("\nOperation cancelled.");
        }
    }

    /**
     * Displays the list of organization types and returns the one selected by the user.
     *
     * @return the selected {@link OrganizationType}, or {@code null} if cancelled.
     */
    private OrganizationType displayAndSelectOrganizationType() {
        List<OrganizationType> types = controller.getOrganizationTypes();
        return (OrganizationType) Utils.showAndSelectOne(types, "Select the organization type:");
    }

    /**
     * Displays the list of organization natures and returns the one selected by the user.
     *
     * @return the selected {@link OrganizationNature}, or {@code null} if cancelled.
     */
    private OrganizationNature displayAndSelectOrganizationNature() {
        List<OrganizationNature> natures = controller.getOrganizationNatures();
        return (OrganizationNature) Utils.showAndSelectOne(natures, "Select the organization nature:");
    }

    /**
     * Reads the organization name from the console.
     */
    private void requestData() {
        name = Utils.readLineFromConsole("Organization Name: ");
    }

    /**
     * Submits the collected data to the controller and prints the result.
     */
    private void submitData() {
        boolean success = controller.registerOrganization(name, selectedNature.toString(), selectedType);
        if (success) {
            System.out.println("\nOrganization successfully registered!");
        } else {
            System.out.println("\nOrganization not registered (duplicate or invalid data).");
        }
    }
}
