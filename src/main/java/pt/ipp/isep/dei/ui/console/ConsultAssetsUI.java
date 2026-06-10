package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ConsultAssetsController;
import pt.ipp.isep.dei.dto.AssetEntryDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.Date;
import java.util.List;

/**
 * UI for US11 - Consult the assets of a political agent on a specific date.
 * Citizens and journalists pick an agent and a reference date and see the
 * assets declared in validated declarations submitted on or before that date.
 * Sensitive values are masked for citizens (AC4).
 */
public class ConsultAssetsUI implements Runnable {

    private final ConsultAssetsController controller;

    /**
     * Creates the UI and initialises the controller using the singleton repositories.
     */
    public ConsultAssetsUI() {
        controller = new ConsultAssetsController();
    }

    /**
     * Runs the flow: select agent, read reference date, fetch the asset
     * entries, and display them. Sensitive values are masked when the
     * current user is not a journalist (AC4).
     */
    public void run() {
        System.out.println("\n\n--- Consult Assets ----------------------------");

        PoliticalAgentDTO agent = displayAndSelectPoliticalAgent();
        if (agent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date referenceDate = Utils.readDateFromConsole("Reference date (dd-MM-yyyy): ");

        List<AssetEntryDTO> assets;
        try {
            assets = controller.getAssetsAt(agent, referenceDate);
        } catch (IllegalArgumentException ex) {
            System.out.println("\n" + ex.getMessage());
            return;
        }

        if (assets.isEmpty()) {
            System.out.println("\nNo validated declarations were submitted by " + agent.getName()
                    + " on or before " + referenceDate + ".");
            return;
        }

        boolean fullDetails = controller.isCurrentUserJournalist();
        showHeader(agent, referenceDate, assets.size(), fullDetails);
        for (AssetEntryDTO a : assets) {
            showAsset(a, fullDetails);
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

    private void showHeader(PoliticalAgentDTO agent, Date referenceDate, int count, boolean fullDetails) {
        System.out.println("\n--- Assets ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Reference Date  : %s%n", referenceDate);
        System.out.printf("Asset entries   : %d%n", count);
        if (!fullDetails) {
            System.out.println("(Sensitive values are masked. Login as a journalist to see full details.)");
        }
    }

    private void showAsset(AssetEntryDTO a, boolean fullDetails) {
        String value = fullDetails ? String.format("%.2f", a.getValue()) : "***";
        System.out.println("- Type: " + a.getType() + ", Value: " + value);
        if (!a.getDetail().isEmpty()) {
            if (a.isSensitiveDetail() && !fullDetails) {
                String label = a.getDetail().split(":")[0];
                System.out.println("    " + label + ": ***");
            } else {
                System.out.println("    " + a.getDetail());
            }
        }
    }
}
