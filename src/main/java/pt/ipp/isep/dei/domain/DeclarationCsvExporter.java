package pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Pure Fabrication responsible for writing a list of validated declarations
 * to a CSV file following the format of Table 2 (Declaration Dataset).
 */
public class DeclarationCsvExporter {

    private static final String HEADER =
            "agent_id,role,declaration_type,declaration_date,declaration_id," +
            "institution,gross_salary,side_income_consulting," +
            "side_income_board_memberships,assets_in_real_estate," +
            "assets_in_vehicles,assets_in_stocks";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Utility class — do not instantiate. */
    private DeclarationCsvExporter() {}

    /**
     * Writes the given declarations to a CSV file at the specified path.
     *
     * @param declarations list of declarations to export
     * @param filePath     path of the output CSV file
     * @return {@code true} if the file was written successfully
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static boolean export(List<Declaration> declarations, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(HEADER);
            for (Declaration d : declarations) {
                writer.println(toCsvRow(d));
            }
        }
        return true;
    }

    /**
     * Converts a single declaration into a comma-separated row string.
     *
     * @param d the declaration to convert
     * @return a CSV-formatted string representing the declaration
     */
    private static String toCsvRow(Declaration d) {
        String role = "";
        String institution = "";
        double grossSalary = 0.0;
        double sideIncomeConsulting = 0.0;
        double sideIncomeBoardMemberships = 0.0;
        List<PositionEntry> positions = d.getPositionEntries();
        if (!positions.isEmpty()) {
            role = positions.get(0).getFunctionDesignation();
            institution = positions.get(0).getOrganization().getName();
            for (PositionEntry p : positions) {
                grossSalary += p.getGrossSalary();
                sideIncomeConsulting += p.getSideIncomeConsulting();
                sideIncomeBoardMemberships += p.getSideIncomeBoardMemberships();
            }
        }
        double realEstate = 0.0, vehicles = 0.0, stocks = 0.0;
        for (AssetEntry a : d.getAssetEntries()) {
            switch (a.getAssetType()) {
                case REAL_ESTATE: realEstate += a.getAssetValue(); break;
                case VEHICLES:    vehicles   += a.getAssetValue(); break;
                case STOCKS:      stocks     += a.getAssetValue(); break;
            }
        }
        String date = d.getSubmissionDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
        return String.join(",",
                d.getAgent().getTaxIdentificationNumber(),
                role,
                d.getType().toString().toLowerCase(),
                date,
                d.getId().toString(),
                institution,
                String.valueOf(grossSalary),
                String.valueOf(sideIncomeConsulting),
                String.valueOf(sideIncomeBoardMemberships),
                String.valueOf(realEstate),
                String.valueOf(vehicles),
                String.valueOf(stocks));
    }
}
