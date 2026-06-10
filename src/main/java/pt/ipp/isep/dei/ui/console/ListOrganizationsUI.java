package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ListOrganizationsController;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.dto.OrganizationDTO;

import java.util.List;
import java.util.Map;

/**
 * UI for listing all registered institutions grouped by type (US03).
 */
public class ListOrganizationsUI implements Runnable {

    private final ListOrganizationsController controller;

    /**
     * Creates the UI and initializes the controller.
     */
    public ListOrganizationsUI() {
        controller = new ListOrganizationsController();
    }

    /**
     * Displays all institutions grouped by type, sorted alphabetically within each group.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- List Institutions -------------------------");

        Map<OrganizationType, List<OrganizationDTO>> grouped = controller.getOrganizationsGroupedByType();

        boolean anyFound = false;
        for (Map.Entry<OrganizationType, List<OrganizationDTO>> entry : grouped.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                anyFound = true;
                System.out.println("\n[ " + entry.getKey() + " ]");
                for (OrganizationDTO org : entry.getValue()) {
                    System.out.println("  - " + org.getName());
                }
            }
        }

        if (!anyFound) {
            System.out.println("\nNo institutions registered yet.");
        }
    }
}
