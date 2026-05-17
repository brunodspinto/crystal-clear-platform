package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.SubmitDeclarationController;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UI for submitting a Declaration of Interests (US06).
 * Guides the Political Agent through all sections of the declaration:
 * type selection, position entries, subsidy entries, asset entries,
 * business participations, and attachments.
 */
public class SubmitDeclarationUI implements Runnable {

    private final SubmitDeclarationController controller;

    private DeclarationType selectedType;
    private final List<Object[]> positionEntries = new ArrayList<>();
    private final List<Object[]> subsidyEntries = new ArrayList<>();
    private final List<Object[]> assetEntries = new ArrayList<>();
    private final List<Object[]> businessParticipations = new ArrayList<>();
    private final List<Object[]> attachments = new ArrayList<>();

    /**
     * Creates the UI and initializes the controller.
     */
    public SubmitDeclarationUI() {
        controller = new SubmitDeclarationController();
    }

    /**
     * Runs the full declaration submission flow.
     */
    @Override
    public void run() {
        System.out.println("\n\n--- Submit Declaration of Interests -------------------------");

        selectedType = selectDeclarationType();
        if (selectedType == null) {
            System.out.println("\nOperation cancelled.");
            return;
        }

        collectPositionEntries();
        if (positionEntries.isEmpty()) {
            System.out.println("\nAt least one position entry is required. Operation cancelled.");
            return;
        }

        collectSubsidyEntries();
        collectAssetEntries();
        collectBusinessParticipations();
        collectAttachments();

        printSummary();

        if (Utils.confirm("Confirm submission? (y/n)")) {
            submitData();
        } else {
            System.out.println("\nOperation cancelled.");
        }
    }

    // -------------------------------------------------------------------------
    // Type selection
    // -------------------------------------------------------------------------

    private DeclarationType selectDeclarationType() {
        List<DeclarationType> types = controller.getDeclarationTypes();
        return (DeclarationType) Utils.showAndSelectOne(types, "Select declaration type:");
    }

    // -------------------------------------------------------------------------
    // Position entries (1 or more required)
    // -------------------------------------------------------------------------

    private void collectPositionEntries() {
        System.out.println("\n--- Position Entries (at least one required) ---");
        do {
            Object[] entry = collectSinglePositionEntry();
            if (entry != null) {
                positionEntries.add(entry);
            }
        } while (Utils.confirm("Add another position entry? (y/n)"));
    }

    private Object[] collectSinglePositionEntry() {
        List<Organization> orgs = controller.getOrganizations();
        if (orgs.isEmpty()) {
            System.out.println("No organizations registered. Cannot add position entry.");
            return null;
        }
        Organization org = (Organization) Utils.showAndSelectOne(orgs, "Select organization:");
        if (org == null) return null;

        String function = Utils.readLineFromConsole("Enter function/position designation (e.g., Director, Mayor): ");
        if (function == null || function.trim().isEmpty()) return null;

        PositionNature nature = (PositionNature) Utils.showAndSelectOne(
                controller.getPositionNatures(), "Select nature:");
        if (nature == null) return null;

        double grossSalary = Utils.readDoubleFromConsole("Annual gross salary: ");
        double sideIncomeConsulting = Utils.readDoubleFromConsole("Side income - consulting (0 if none): ");
        double sideIncomeBoardMemberships = Utils.readDoubleFromConsole("Side income - board memberships (0 if none): ");
        Date startDate = Utils.readDateFromConsole("Start date (dd-MM-yyyy): ");
        Date endDate = null;
        if (!Utils.confirm("Is the position still active? (y/n)")) {
            endDate = Utils.readDateFromConsole("End date (dd-MM-yyyy): ");
        }

        return new Object[]{org, function, nature, grossSalary, sideIncomeConsulting, sideIncomeBoardMemberships, startDate, endDate};
    }

    // -------------------------------------------------------------------------
    // Subsidy entries (optional)
    // -------------------------------------------------------------------------

    private void collectSubsidyEntries() {
        System.out.println("\n--- Subsidy / Support Entries (optional) ---");
        if (!Utils.confirm("Add subsidy entries? (y/n)")) return;
        do {
            Object[] entry = collectSingleSubsidyEntry();
            if (entry != null) subsidyEntries.add(entry);
        } while (Utils.confirm("Add another subsidy entry? (y/n)"));
    }

    private Object[] collectSingleSubsidyEntry() {
        List<Organization> orgs = controller.getOrganizations();
        Organization org = (Organization) Utils.showAndSelectOne(orgs, "Select source organization:");
        if (org == null) return null;

        double amount = Utils.readDoubleFromConsole("Amount: ");
        String description = Utils.readLineFromConsole("Description: ");
        Date date = Utils.readDateFromConsole("Date received (dd-MM-yyyy): ");

        return new Object[]{org, amount, description, date};
    }

    // -------------------------------------------------------------------------
    // Asset entries (optional)
    // -------------------------------------------------------------------------

    private void collectAssetEntries() {
        System.out.println("\n--- Asset Entries (optional) ---");
        if (!Utils.confirm("Add asset entries? (y/n)")) return;
        do {
            Object[] entry = collectSingleAssetEntry();
            if (entry != null) assetEntries.add(entry);
        } while (Utils.confirm("Add another asset entry? (y/n)"));
    }

    private Object[] collectSingleAssetEntry() {
        AssetType assetType = (AssetType) Utils.showAndSelectOne(
                controller.getAssetTypes(), "Select asset type:");
        if (assetType == null) return null;

        double assetValue = Utils.readDoubleFromConsole("Asset value: ");

        Object detail;
        switch (assetType) {
            case REAL_ESTATE:
                String reDescription = Utils.readLineFromConsole("Property description: ");
                String municipality = Utils.readLineFromConsole("Municipality: ");
                detail = new RealEstate(reDescription, municipality);
                break;
            case VEHICLES:
                String vDescription = Utils.readLineFromConsole("Vehicle description: ");
                detail = new VehicleAsset(vDescription);
                break;
            case STOCKS:
                String sDescription = Utils.readLineFromConsole("Stock/instrument description: ");
                detail = new StockAsset(sDescription);
                break;
            default:
                return null;
        }

        return new Object[]{assetType, assetValue, detail};
    }

    // -------------------------------------------------------------------------
    // Business participations (optional)
    // -------------------------------------------------------------------------

    private void collectBusinessParticipations() {
        System.out.println("\n--- Business Participations / Holdings (optional) ---");
        if (!Utils.confirm("Add business participation entries? (y/n)")) return;
        do {
            Object[] entry = collectSingleBusinessParticipation();
            if (entry != null) businessParticipations.add(entry);
        } while (Utils.confirm("Add another participation entry? (y/n)"));
    }

    private Object[] collectSingleBusinessParticipation() {
        List<Organization> orgs = controller.getOrganizations();
        Organization org = (Organization) Utils.showAndSelectOne(orgs, "Select company:");
        if (org == null) return null;

        long companyNIF = Utils.readLongFromConsole("Company NIF: ");
        double totalValue = Utils.readDoubleFromConsole("Total value in stocks: ");
        double percentage = Utils.readDoubleFromConsole("Company percentage (%): ");

        return new Object[]{org, companyNIF, totalValue, percentage};
    }

    // -------------------------------------------------------------------------
    // Attachments (optional)
    // -------------------------------------------------------------------------

    private void collectAttachments() {
        System.out.println("\n--- Attachments (optional) ---");
        if (!Utils.confirm("Add attachments? (y/n)")) return;
        do {
            String fileName = Utils.readLineFromConsole("File name: ");
            Date uploadDate = new Date();
            attachments.add(new Object[]{fileName, uploadDate});
        } while (Utils.confirm("Add another attachment? (y/n)"));
    }

    // -------------------------------------------------------------------------
    // Summary and submission
    // -------------------------------------------------------------------------

    private void printSummary() {
        System.out.println("\n--- Declaration Summary ---");
        System.out.printf("Type                    : %s%n", selectedType);
        System.out.printf("Position entries        : %d%n", positionEntries.size());
        System.out.printf("Subsidy entries         : %d%n", subsidyEntries.size());
        System.out.printf("Asset entries           : %d%n", assetEntries.size());
        System.out.printf("Business participations : %d%n", businessParticipations.size());
        System.out.printf("Attachments             : %d%n", attachments.size());
    }

    private void submitData() {
        boolean success = controller.submitDeclaration(
                selectedType, positionEntries, subsidyEntries,
                assetEntries, businessParticipations, attachments);
        if (success) {
            System.out.println("\nDeclaration successfully submitted! Status: PENDING.");
        } else {
            System.out.println("\nDeclaration not submitted. Political agent not found in session.");
        }
    }
}
