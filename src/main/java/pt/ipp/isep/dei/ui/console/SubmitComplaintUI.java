package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
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
 *
 * <p>The UI works only with DTOs ({@link PoliticalAgentDTO}) and primitives; it
 * never touches domain objects (ESOFT &mdash; DTO pattern).</p>
 */
public class SubmitComplaintUI implements Runnable {

    private final SubmitComplaintController controller;
    private PoliticalAgentDTO selectedAgent;

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

        if (!controller.startComplaint(selectedAgent)) {
            System.out.println("\nCould not start the complaint (citizen or agent not found). Operation cancelled.");
            return;
        }

        boolean addMore = true;
        while (addMore) {
            if (collectAndAddGrievance()) {
                System.out.println("\nGrievance added (" + controller.getCurrentGrievanceCount()
                        + " in this complaint so far).");
            }
            addMore = Utils.confirm("Add another grievance about " + selectedAgent.getName() + "? (y/n)");
        }

        if (controller.getCurrentGrievanceCount() == 0) {
            controller.cancelComplaint();
            System.out.println("\nNo grievances added. Complaint not submitted.");
            return;
        }

        int count = controller.getCurrentGrievanceCount();
        if (controller.submitComplaint()) {
            System.out.println("\nComplaint with " + count + " grievance(s) successfully submitted!");
        } else {
            System.out.println("\nComplaint not submitted!");
        }
    }

    /**
     * Collects a single grievance (function, description and date), shows it for
     * confirmation, and adds it to the complaint if confirmed.
     *
     * @return {@code true} if a grievance was added; {@code false} if it was
     *         canceled, discarded or invalid.
     */
    private boolean collectAndAddGrievance() {
        PoliticalFunction function = displayAndSelectPoliticalFunction();
        if (function == null) {
            System.out.println("\nGrievance cancelled.");
            return false;
        }

        String description = Utils.readLineFromConsole("Complaint description: ");
        Date complaintDate = Utils.readDateFromConsole("Complaint date (dd-MM-yyyy): ");

        System.out.println("\n--- Confirm Grievance ---");
        System.out.printf("Political Agent   : %s%n", selectedAgent.getName());
        System.out.printf("Political Function: %s%n", function);
        System.out.printf("Complaint Date    : %s%n", complaintDate);
        System.out.printf("Description       : %s%n", description);

        if (!Utils.confirm("Confirm this grievance? (y/n)")) {
            System.out.println("\nGrievance discarded.");
            return false;
        }

        try {
            controller.addGrievance(description, complaintDate, function);
            return true;
        } catch (IllegalArgumentException ex) {
            System.out.println("\nInvalid grievance: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Displays the list of political agents and returns the one selected by the citizen.
     *
     * @return the selected {@link PoliticalAgentDTO}, or {@code null} if none exist or cancelled.
     */
    private PoliticalAgentDTO displayAndSelectPoliticalAgent() {
        List<PoliticalAgentDTO> agents = controller.getPoliticalAgents();
        if (agents.isEmpty()) {
            System.out.println("No political agents registered in the system.");
            return null;
        }
        return (PoliticalAgentDTO) Utils.showAndSelectOne(agents, "Select a political agent:");
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
