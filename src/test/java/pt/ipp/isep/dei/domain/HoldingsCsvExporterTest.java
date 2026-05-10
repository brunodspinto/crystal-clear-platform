package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class HoldingsCsvExporterTest {

    @TempDir
    Path tempDir;

    private PoliticalAgent createAgent(String tin) {
        return new PoliticalAgent("Agent " + tin, "agent@gov.pt", "12345678", tin, new Date(), null);
    }

    private Organization createOrg(String name) {
        return new Organization(name, "private", OrganizationType.COMPANY);
    }

    private Declaration createValidatedDeclaration(String tin) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(tin), new Date());
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    @Test
    void ensureEmptyListProducesHeaderOnly() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        HoldingsCsvExporter.export(Collections.emptyList(), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertEquals("agent_id,company_NIF,total_value_in_stocks,company_percentage,declaration_date",
                lines.get(0));
    }

    @Test
    void ensureDeclarationWithNoParticipationsProducesHeaderOnly() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("111111111");
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
    }

    @Test
    void ensureOneParticipationProducesOneDataRow() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("222222222");
        d.addBusinessParticipation(createOrg("TechCorp"), 500000001L, 15000.0, 10.5);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).startsWith("222222222,500000001,"));
        assertTrue(lines.get(1).contains("15000.0"));
        assertTrue(lines.get(1).contains("10.5"));
    }

    @Test
    void ensureTwoParticipationsProduceTwoDataRows() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("333333333");
        d.addBusinessParticipation(createOrg("Alpha"), 100000001L, 5000.0, 5.0);
        d.addBusinessParticipation(createOrg("Beta"), 200000002L, 8000.0, 3.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(3, lines.size());
    }

    @Test
    void ensureMultipleDeclarationsProduceCorrectRowCount() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d1 = createValidatedDeclaration("444444444");
        d1.addBusinessParticipation(createOrg("Corp1"), 111111111L, 1000.0, 1.0);
        Declaration d2 = createValidatedDeclaration("555555555");
        d2.addBusinessParticipation(createOrg("Corp2"), 222222222L, 2000.0, 2.0);
        d2.addBusinessParticipation(createOrg("Corp3"), 333333333L, 3000.0, 3.0);
        HoldingsCsvExporter.export(Arrays.asList(d1, d2), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(4, lines.size()); // header + 3 rows
    }

    @Test
    void ensureExportReturnsTrueOnSuccess() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        boolean result = HoldingsCsvExporter.export(Collections.emptyList(), out.toString());
        assertTrue(result);
    }

    @Test
    void ensureDateColumnIsInYyyyMmDdFormat() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("777777777");
        d.addBusinessParticipation(createOrg("DateCorp"), 123456789L, 1000.0, 1.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        String row = Files.readAllLines(out).get(1);
        String[] cols = row.split(",");
        String date = cols[cols.length - 1];
        assertTrue(Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date),
                "Expected yyyy-MM-dd but got: " + date);
    }

    @Test
    void ensureAgentIdAndNifAreCorrectInRow() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("999999999");
        d.addBusinessParticipation(createOrg("MegaCorp"), 987654321L, 50000.0, 25.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        String row = Files.readAllLines(out).get(1);
        assertTrue(row.startsWith("999999999,987654321,"));
        assertTrue(row.contains("50000.0"));
        assertTrue(row.contains("25.0"));
    }
}
