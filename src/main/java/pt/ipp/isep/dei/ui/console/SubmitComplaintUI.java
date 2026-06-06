package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * UI for submitting a complaint about a political agent (US12).
 * A complaint targets a single political agent but may gather several
 * grievances: after each grievance is confirmed (or discarded), the citizen is
 * asked whether to add another grievance about the same agent. The whole
 * complaint is persisted once, at the end.
 */
public class SubmitComplaintUI implements Runnable {

    private final SubmitComplaintController controller;
    private PoliticalAgent selectedAgent;

    /**
     * Creates the UI and initializes the controller.
     */
    public SubmitComplaintUI() {
        controller = new SubmitComplaintController();
    }

    /**
     * Runs the complaint submission flow: selects the agent once, then loops
     * collecting grievances about that agent, and finally submits.
     */
    public void run() {
        System.out.println("\n\n--- Submit Complaint -------------------------");

        selectedAgent = displayAndSelectPoliticalAgent();
        if (selectedAgent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Complaint complaint = controller.createComplaint(selectedAgent);
        if (complaint == null) {
            System.out.println("\nCould not identify the logged-in citizen. Operation cancelled.");
            return;
        }

        int added = 0;
        boolean addMore = true;
        while (addMore) {
            if (collectAndAddGrievance(complaint)) {
                added = added + 1;
                System.out.println("\nGrievance added (" + added + " in this complaint so far).");
            }
            addMore = Utils.confirm("Add another grievance about " + selectedAgent.getName() + "? (y/n)");
        }

        if (added == 0) {
            System.out.println("\nNo grievances added. Complaint not submitted.");
            return;
        }

        if (controller.saveComplaint(complaint)) {
            System.out.println("\nComplaint with " + added + " grievance(s) successfully submitted!");
        } else {
            System.out.println("\nComplaint not submitted!");
        }
    }

    /**
     * Collects a single grievance (function, description and date), shows it for
     * confirmation, and adds it to the complaint if confirmed.
     *
     * @param complaint the complaint being built.
     * @return {@code true} if a grievance was added; {@code false} if it was
     *         cancelled, discarded or invalid.
     */
    private boolean collectAndAddGrievance(Complaint complaint) {
        PoliticalFunction function = displayAndSelectPoliticalFunction();
        if (function == null) {
            System.out.println("\nGrievance cancelled.");
            return false;
        }

        String description = Utils.readLineFromConsole("Complaint description: ");
        Date complaintDate = Utils.readDateFromConsole("Complaint date (dd-MM-yyyy): ");

        System.out.println("\n--- Confirm Grievance ---");
        System.out.printf("Political Agent   : %s%n", complaint.getPoliticalAgent().getName());
        System.out.printf("Political Function: %s%n", function);
        System.out.printf("Complaint Date    : %s%n", complaintDate);
        System.out.printf("Description       : %s%n", description);

        if (!Utils.confirm("Confirm this grievance? (y/n)")) {
            System.out.println("\nGrievance discarded.");
            return false;
        }

        try {
            controller.addGrievance(complaint, description, complaintDate, function);
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println("\nInvalid grievance: " + ex.getMessage());
            return false;
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
}
