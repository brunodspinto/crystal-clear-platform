package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ConsultIntegratedSituationController;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * UI for consulting the integrated situation of a political agent on a given date (US09).
 * Guides the Ethics Committee member through agent selection, date entry, and the
 * presentation of the validated declarations and their entries.
 */
public class ConsultIntegratedSituationUI implements Runnable {

    private final ConsultIntegratedSituationController controller;

    /**
     * Creates the UI and initializes the controller using the singleton repositories.
     */
    public ConsultIntegratedSituationUI() {
        controller = new ConsultIntegratedSituationController();
    }

    /**
     * Runs the consult flow: select agent, read date, fetch declarations, and present them.
     */
    public void run() {
        System.out.println("\n\n--- Consult Integrated Situation ---------------");

        PoliticalAgentDTO selectedAgent = displayAndSelectPoliticalAgent();
        if (selectedAgent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date referenceDate = Utils.readDateFromConsole("Reference date (dd-MM-yyyy): ");

        List<DeclarationDTO> declarations = controller.getIntegratedSituation(selectedAgent, referenceDate);
        if (declarations.isEmpty()) {
            System.out.println("\nNo validated declarations were submitted by " + selectedAgent.getName()
                    + " on or before " + referenceDate + ".");
            return;
        }

        showHeader(selectedAgent, referenceDate, declarations.size());
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

    private void showHeader(PoliticalAgentDTO agent, Date referenceDate, int count) {
        System.out.println("\n--- Integrated Situation ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Reference Date  : %s%n", referenceDate);
        System.out.printf("Validated decls.: %d%n", count);
    }

    private void showDeclaration(DeclarationDTO d) {
        System.out.println("\n>>> " + d.getSummary());
        showSection("Positions", d.getPositions());
        showSection("Incomes", d.getIncomes());
        showSection("Subsidies", d.getSubsidies());
        showSection("Assets", d.getAssets());
        showSection("Business participations", d.getBusinessParticipations());
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
