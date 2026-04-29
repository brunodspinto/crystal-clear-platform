package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * UI for submitting a complaint about a political agent (US12).
 * Guides the citizen through agent selection, function selection,
 * data entry, and confirmation before delegating to {@link SubmitComplaintController}.
 */
public class SubmitComplaintUI implements Runnable {
    private final SubmitComplaintController controller;
    private PoliticalAgent selectedAgent;
    private PoliticalFunction selectedFunction;
    private String description;
    private Date complaintDate;

    /**
     * Creates the UI and initializes the controller.
     */
    public SubmitComplaintUI() {
        controller = new SubmitComplaintController();
    }

    /**
     * Runs the complaint submission flow: selects agent and function,
     * collects data, confirms and submits.
     */
    public void run() {
        System.out.println("\n\n--- Submit Complaint -------------------------");

        selectedAgent = displayAndSelectPoliticalAgent();
        if (selectedAgent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        selectedFunction = displayAndSelectPoliticalFunction();
        if (selectedFunction == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        requestData();

        System.out.println("\n--- Confirm Complaint ---");
        System.out.printf("Political Agent   : %s%n", selectedAgent.getName());
        System.out.printf("Political Function: %s%n", selectedFunction);
        System.out.printf("Complaint Date    : %s%n", complaintDate);
        System.out.printf("Description       : %s%n", description);

        if (Utils.confirm("Confirm submission? (s/n)")) {
            submitData();
        } else {
            System.out.println("\nOperation cancelled.");
        }
    }

    /**
     * Displays the list of political agents and returns the one selected by the citizen.
     *
     * @return the selected {@link PoliticalAgent}, or {@code null} if none exist or cancelled.
     */
    private PoliticalAgent displayAndSelectPoliticalAgent() {
        List<PoliticalAgent> agents = controller.getPoliticalAgents();
        if (agents.isEmpty()) {
            System.out.println("No political agents registered in the system.");
            return null;
        }
        return (PoliticalAgent) Utils.showAndSelectOne(agents, "Select a political agent:");
    }

    /**
     * Displays the list of political functions and returns the one selected by the citizen.
     *
     * @return the selected {@link PoliticalFunction}, or {@code null} if cancelled.
     */
    private PoliticalFunction displayAndSelectPoliticalFunction() {
        List<PoliticalFunction> functions = controller.getPoliticalFunctions();
        return (PoliticalFunction) Utils.showAndSelectOne(functions,
                "Select the political function held at the time of the behaviour:");
    }

    /**
     * Reads the complaint description and date from the console.
     */
    private void requestData() {
        description = Utils.readLineFromConsole("Complaint description: ");
        complaintDate = Utils.readDateFromConsole("Complaint date (dd-MM-yyyy): ");
    }

    /**
     * Submits the collected data to the controller and prints the result.
     */
    private void submitData() {
        boolean success = controller.submitComplaint(description, complaintDate, selectedAgent, selectedFunction);
        if (success) {
            System.out.println("\nComplaint successfully submitted!");
        } else {
            System.out.println("\nComplaint not submitted!");
        }
    }
}
