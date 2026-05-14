package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.ConsultAssetsController;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.AssetType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.RealEstate;
import pt.ipp.isep.dei.domain.StockAsset;
import pt.ipp.isep.dei.domain.VehicleAsset;
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

        PoliticalAgent agent = displayAndSelectPoliticalAgent();
        if (agent == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        Date referenceDate = Utils.readDateFromConsole("Reference date (dd-MM-yyyy): ");

        List<AssetEntry> assets;
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
        for (AssetEntry a : assets) {
            showAsset(a, fullDetails);
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

    private void showHeader(PoliticalAgent agent, Date referenceDate, int count, boolean fullDetails) {
        System.out.println("\n--- Assets ---");
        System.out.printf("Political Agent : %s%n", agent.getName());
        System.out.printf("Reference Date  : %s%n", referenceDate);
        System.out.printf("Asset entries   : %d%n", count);
        if (!fullDetails) {
            System.out.println("(Sensitive values are masked. Login as a journalist to see full details.)");
        }
    }

    private void showAsset(AssetEntry a, boolean fullDetails) {
        String value = fullDetails ? String.format("%.2f", a.getAssetValue()) : "***";
        System.out.println("- Type: " + a.getAssetType() + ", Value: " + value);
        if (a.getAssetType() == AssetType.REAL_ESTATE) {
            RealEstate re = a.getRealEstate();
            System.out.println("    Real Estate: " + re.getDescription() + " (" + re.getMunicipality() + ")");
        } else if (a.getAssetType() == AssetType.VEHICLES) {
            VehicleAsset v = a.getVehicleAsset();
            if (fullDetails) {
                System.out.println("    Vehicle: " + v);
            } else {
                System.out.println("    Vehicle: ***");
            }
        } else if (a.getAssetType() == AssetType.STOCKS) {
            StockAsset s = a.getStockAsset();
            if (fullDetails) {
                System.out.println("    Stocks: " + s);
            } else {
                System.out.println("    Stocks: ***");
            }
        }
    }
}
