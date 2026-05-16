package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ConsultIntegratedSituationController;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.BusinessParticipation;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.Income;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PositionEntry;
import pt.ipp.isep.dei.domain.SubsidyEntry;
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

        PoliticalAgent selectedAgent = displayAndSelectPoliticalAgent();
        if (selectedAgent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date referenceDate = Utils.readDateFromConsole("Reference date (dd-MM-yyyy): ");

        List<Declaration> declarations = controller.getIntegratedSituation(selectedAgent, referenceDate);
        if (declarations.isEmpty()) {
            System.out.println("\nNo validated declarations were submitted by " + selectedAgent.getName()
                    + " on or before " + referenceDate + ".");
            return;
        }

        showHeader(selectedAgent, referenceDate, declarations.size());
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

    private void showHeader(PoliticalAgent agent, Date referenceDate, int count) {
        System.out.println("\n--- Integrated Situation ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Reference Date  : %s%n", referenceDate);
        System.out.printf("Validated decls.: %d%n", count);
    }

    private void showDeclaration(Declaration d) {
        System.out.println("\n>>> " + d);
        showPositions(d.getPositionEntries());
        showIncomes(d.getIncomes());
        showSubsidies(d.getSubsidyEntries());
        showAssets(d.getAssetEntries());
        showBusinessParticipations(d.getBusinessParticipations());
    }

    private void showIncomes(List<Income> entries) {
        if (entries.isEmpty()) {
            return;
        }
        System.out.println("  Incomes:");
        for (Income e : entries) {
            System.out.println("    - " + e);
        }
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

    private void showAssets(List<AssetEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        System.out.println("  Assets:");
        for (AssetEntry e : entries) {
            System.out.println("    - " + e);
        }
    }

    private void showBusinessParticipations(List<BusinessParticipation> entries) {
        if (entries.isEmpty()) {
            return;
        }
        System.out.println("  Business participations:");
        for (BusinessParticipation e : entries) {
            System.out.println("    - " + e);
        }
    }
}
