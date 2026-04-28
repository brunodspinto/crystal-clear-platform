package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RegisterOrganizationController;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class RegisterOrganizationUI implements Runnable {
    private final RegisterOrganizationController controller;
    private OrganizationType selectedType;
    private String name;
    private String nature;

    public RegisterOrganizationUI() {
        controller = new RegisterOrganizationController();
    }

    public void run() {
        System.out.println("\n\n--- Register Organization -------------------------");

        selectedType = displayAndSelectOrganizationType();
        if (selectedType == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        requestData();

        System.out.println("\n--- Confirm Data ---");
        System.out.printf("Name   : %s%n", name);
        System.out.printf("Nature : %s%n", nature);
        System.out.printf("Type   : %s%n", selectedType);

        if (Utils.confirm("Confirm registration? (s/n)")) {
            submitData();
        } else {
            System.out.println("\nOperation cancelled.");
        }
    }

    private OrganizationType displayAndSelectOrganizationType() {
        List<OrganizationType> types = controller.getOrganizationTypes();
        return (OrganizationType) Utils.showAndSelectOne(types, "Select the organization type:");
    }

    private void requestData() {
        name = Utils.readLineFromConsole("Organization Name: ");
        nature = Utils.readLineFromConsole("Organization Nature (e.g. public, private, social): ");
    }

    private void submitData() {
        boolean success = controller.registerOrganization(name, nature, selectedType);
        if (success) {
            System.out.println("\nOrganization successfully registered!");
        } else {
            System.out.println("\nOrganization not registered (duplicate or invalid data).");
        }
    }
}
