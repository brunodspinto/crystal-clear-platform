package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class DeclarationCsvExporterTest {

    @TempDir
    Path tempDir;

    private PoliticalAgent createAgent(String tin) {
        return new PoliticalAgent("Agent " + tin, "agent@gov.pt", "12345678", tin, new Date(), null);
    }

    private Organization createOrg(String name) {
        return new Organization(name, OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY);
    }

    private Declaration createTestDeclaration() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent("111111111"), new Date());
        d.addPositionEntry(createOrg("Parliament"), "Deputy", PositionNature.PUBLIC,
                60000.0, 5000.0, 2000.0, new Date(), null);
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    private Declaration createDeclarationWithTwoPositions(double salary1, double salary2,
                                                         double consulting1, double consulting2,
                                                         double board1, double board2) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent("222222222"), new Date());
        Organization org = createOrg("Parliament");
        d.addPositionEntry(org, "Deputy", PositionNature.PUBLIC,
                salary1, consulting1, board1, new Date(), null);
        d.addPositionEntry(org, "Adviser", PositionNature.PUBLIC,
                salary2, consulting2, board2, new Date(), null);
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    private Declaration createDeclarationWithAssets(double realEstateValue, double vehicleValue, double stockValue) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent("333333333"), new Date());
        d.addAssetEntry(AssetType.REAL_ESTATE, realEstateValue, new RealEstate("House", "Lisbon"));
        d.addAssetEntry(AssetType.VEHICLES, vehicleValue, new VehicleAsset("Car"));
        d.addAssetEntry(AssetType.STOCKS, stockValue, new StockAsset("ABC"));
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    @Test
    void ensureExportWithNoDeclarationsWritesHeaderOnly() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        DeclarationCsvExporter.export(new ArrayList<Declaration>(), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).startsWith("agent_id"));
    }

    @Test
    void ensureExportWritesOneRowPerDeclaration() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(createTestDeclaration());
        declarations.add(createTestDeclaration());
        DeclarationCsvExporter.export(declarations, out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(3, lines.size()); // header + 2 data rows
    }

    @Test
    void ensureDeclarationDateIsFormattedCorrectly() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(createTestDeclaration());
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        String[] cols = row.split(",");
        String date = cols[3];
        assertTrue(Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date),
                "Expected yyyy-MM-dd but got: " + date);
    }

    @Test
    void ensureGrossSalaryIsSummedAcrossPositionEntries() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(createDeclarationWithTwoPositions(10000.0, 5000.0, 0.0, 0.0, 0.0, 0.0));
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        assertTrue(row.contains("15000.0"), "Row should contain summed salary 15000.0 — got: " + row);
    }

    @Test
    void ensureSideIncomeColumnsAreSummedSeparately() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(createDeclarationWithTwoPositions(0.0, 0.0, 3000.0, 2000.0, 1000.0, 500.0));
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        assertTrue(row.contains("5000.0"), "Row should contain summed consulting 5000.0 — got: " + row);
        assertTrue(row.contains("1500.0"), "Row should contain summed board 1500.0 — got: " + row);
    }

    @Test
    void ensureAssetColumnsAreSummedByCategory() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(createDeclarationWithAssets(50000.0, 20000.0, 10000.0));
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        assertTrue(row.contains("50000.0"));
        assertTrue(row.contains("20000.0"));
        assertTrue(row.contains("10000.0"));
    }

    @Test
    void ensureHeaderContainsAllRequiredColumns() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        DeclarationCsvExporter.export(new ArrayList<Declaration>(), out.toString());
        String header = Files.readAllLines(out).get(0);
        assertEquals("agent_id,role,declaration_type,declaration_date,declaration_id," +
                "institution,gross_salary,side_income_consulting," +
                "side_income_board_memberships,assets_in_real_estate," +
                "assets_in_vehicles,assets_in_stocks", header);
    }

    @Test
    void ensureExportReturnsTrueOnSuccess() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        boolean result = DeclarationCsvExporter.export(new ArrayList<Declaration>(), out.toString());
        assertTrue(result);
    }

    @Test
    void ensureRoleAndInstitutionComeFromFirstPositionEntry() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        Declaration d = createDeclarationWithTwoPositions(1000.0, 2000.0, 0.0, 0.0, 0.0, 0.0);
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(d);
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        String[] cols = row.split(",");
        assertEquals("Deputy", cols[1]);
        assertEquals("Parliament", cols[5]);
    }

    @Test
    void ensureDeclarationWithNoPositionsHasEmptyRoleAndInstitution() throws IOException {
        Path out = tempDir.resolve("declarations.csv");
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent("444444444"), new Date());
        d.setStatus(DeclarationStatus.VALIDATED);
        List<Declaration> declarations = new ArrayList<Declaration>();
        declarations.add(d);
        DeclarationCsvExporter.export(declarations, out.toString());
        String row = Files.readAllLines(out).get(1);
        String[] cols = row.split(",", -1);
        assertEquals("", cols[1]); // role
        assertEquals("", cols[5]); // institution
    }
}
