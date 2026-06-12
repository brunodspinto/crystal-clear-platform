package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.AnalyseIncomeEvolutionController;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * UI for US10 - Analyse the evolution of a political agent's income over a period.
 * The journalist selects an agent and a period and the validated declarations
 * inside that period are displayed in chronological order.
 */
public class AnalyseIncomeEvolutionUI implements Runnable {

    private final AnalyseIncomeEvolutionController controller;

    /**
     * Creates the UI and initialises the controller using the singleton repositories.
     */
    public AnalyseIncomeEvolutionUI() {
        controller = new AnalyseIncomeEvolutionController();
    }

    /**
     * Runs the flow: select agent, read start and end dates, fetch the
     * validated declarations and present them chronologically.
     */
    public void run() {
        System.out.println("\n\n--- Analyse Income Evolution -------------------");

        PoliticalAgentDTO agent = displayAndSelectPoliticalAgent();
        if (agent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date startDate = Utils.readDateFromConsole("Start date (dd-MM-yyyy): ");
        Date endDate = Utils.readDateFromConsole("End date (dd-MM-yyyy): ");

        List<DeclarationDTO> declarations;
        try {
            declarations = controller.getIncomeEvolution(agent, startDate, endDate);
        } catch (IllegalArgumentException ex) {
            System.out.println("\n" + ex.getMessage());
            return;
        }

        if (declarations.isEmpty()) {
            System.out.println("\nNo validated declarations were submitted by " + agent.getName()
                    + " between " + startDate + " and " + endDate + ".");
            return;
        }

        showHeader(agent, startDate, endDate, declarations.size());
        for (DeclarationDTO d : declarations) {
            showDeclaration(d);
        }
    }

    private PoliticalAgentDTO displayAndSelectPoliticalAgent() {
        List<PoliticalAgentDTO> agents = controller.getPoliticalAgents();
        if (agents.isEmpty()) {
            System.out.println("No political agents registered in the system.");
            return null;
        }
        return (PoliticalAgentDTO) Utils.showAndSelectOne(agents, "Select a political agent:");
    }

    private void showHeader(PoliticalAgentDTO agent, Date start, Date end, int count) {
        System.out.println("\n--- Income Evolution ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Period          : %s to %s%n", start, end);
        System.out.printf("Validated decls.: %d%n", count);
    }

    private void showDeclaration(DeclarationDTO d) {
        System.out.println("\n>>> " + d.getSummary());
        showSection("Positions", d.getPositions());
        showSection("Incomes", d.getIncomes());
        showSection("Subsidies", d.getSubsidies());
    }

    private void showSection(String title, List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        System.out.println("  " + title + ":");
        for (String line : lines) {
            System.out.println("    - " + line);
        }
    }
}
