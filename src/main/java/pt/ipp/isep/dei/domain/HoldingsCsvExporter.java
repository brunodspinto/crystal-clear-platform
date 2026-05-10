package pt.ipp.isep.dei.domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Pure Fabrication responsible for writing the holdings dataset to a CSV file
 * following the format of Table 3 (Holdings Dataset):
 * agent_id, company_NIF, total_value_in_stocks, company_percentage, declaration_date.
 *
 * One row is produced per (declaration, business-participation) pair.
 */
public class HoldingsCsvExporter {

    private static final String HEADER =
            "agent_id,company_NIF,total_value_in_stocks,company_percentage,declaration_date";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private HoldingsCsvExporter() {}

    public static boolean export(List<Declaration> declarations, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(HEADER);
            for (Declaration d : declarations) {
                for (BusinessParticipation bp : d.getBusinessParticipations()) {
                    writer.println(toCsvRow(d, bp));
                }
            }
        }
        return true;
    }

    private static String toCsvRow(Declaration d, BusinessParticipation bp) {
        String agentId = d.getAgent().getTaxIdentificationNumber();
        String date = d.getSubmissionDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
        return String.join(",",
                agentId,
                String.valueOf(bp.getCompanyNIF()),
                String.valueOf(bp.getTotalValueInStocks()),
                String.valueOf(bp.getCompanyPercentage()),
                date);
    }
}
