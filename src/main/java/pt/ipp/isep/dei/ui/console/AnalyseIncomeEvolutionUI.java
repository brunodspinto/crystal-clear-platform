package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.AnalyseIncomeEvolutionController;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PositionEntry;
import pt.ipp.isep.dei.domain.SubsidyEntry;
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

    public AnalyseIncomeEvolutionUI() {
        controller = new AnalyseIncomeEvolutionController();
    }

    public void run() {
        System.out.println("\n\n--- Analyse Income Evolution -------------------");

        PoliticalAgent agent = displayAndSelectPoliticalAgent();
        if (agent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date startDate = Utils.readDateFromConsole("Start date (dd-MM-yyyy): ");
        Date endDate = Utils.readDateFromConsole("End date (dd-MM-yyyy): ");

        List<Declaration> declarations;
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
        for (Declaration d : declarations) {
            showDeclaration(d);
        }
    }

    private PoliticalAgent displayAndSelectPoliticalAgent() {
        List<PoliticalAgent> agents = controller.getPoliticalAgents();
        if (agents.isEmpty()) {
            System.out.println("No political agents registered in the system.");
            return null;
        }
        return (PoliticalAgent) Utils.showAndSelectOne(agents, "Select a political agent:");
    }

    private void showHeader(PoliticalAgent agent, Date start, Date end, int count) {
        System.out.println("\n--- Income Evolution ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Period          : %s to %s%n", start, end);
        System.out.printf("Validated decls.: %d%n", count);
    }

    private void showDeclaration(Declaration d) {
        System.out.println("\n>>> " + d);
        showPositions(d.getPositionEntries());
        showSubsidies(d.getSubsidyEntries());
    }

    private void showPositions(List<PositionEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        System.out.println("  Positions:");
        for (PositionEntry e : entries) {
            System.out.println("    - " + e);
        }
    }

    private void showSubsidies(List<SubsidyEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        System.out.println("  Subsidies:");
        for (SubsidyEntry e : entries) {
            System.out.println("    - " + e);
        }
    }
}
