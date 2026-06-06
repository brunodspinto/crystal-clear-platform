package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportHoldingsCsvControllerTest {

    @TempDir
    Path tempDir;

    private DeclarationRepository repo;
    private ExportHoldingsCsvController controller;

    @BeforeEach
    void setUp() {
        repo = new DeclarationRepository();
        controller = new ExportHoldingsCsvController(repo);
    }

    private PoliticalAgent createAgent(String tin) {
        return new PoliticalAgent("Agent " + tin, "agent@gov.pt", "12345678", tin, new Date(), null);
    }

    private Organization createOrg(String name) {
        return new Organization(name, OrganizationNature.PRIVATE, OrganizationType.COMPANY);
    }

    private Declaration createValidatedDeclaration(String tin) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(tin), new Date());
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    @Test
    void ensureExportReturnsTrueOnSuccess() {
        Path out = tempDir.resolve("holdings.csv");
        boolean result = controller.exportToCsv(out.toString());
        assertTrue(result);
    }

    @Test
    void ensureExportReturnsFalseOnInvalidPath() {
        boolean result = controller.exportToCsv("/invalid/path/that/does/not/exist/holdings.csv");
        assertFalse(result);
    }

    @Test
    void ensureExportCreatesFile() {
        Path out = tempDir.resolve("holdings.csv");
        controller.exportToCsv(out.toString());
        assertTrue(Files.exists(out));
    }

    @Test
    void ensureEmptyRepositoryProducesOnlyHeader() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        controller.exportToCsv(out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("agent_id"));
    }

    @Test
    void ensureOnlyValidatedDeclarationsAreExported() throws IOException {
        Declaration validated = createValidatedDeclaration("111111111");
        validated.addBusinessParticipation(createOrg("Corp"), 100000001L, 5000.0, 10.0);
        repo.save(validated);

        Declaration pending = new Declaration(DeclarationType.INITIAL, createAgent("222222222"), new Date());
        pending.addBusinessParticipation(createOrg("Corp2"), 200000002L, 3000.0, 5.0);
        repo.save(pending);

        Path out = tempDir.resolve("holdings.csv");
        controller.exportToCsv(out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(2, lines.size()); // header + 1 row from validated only
    }

    @Test
    void ensureExportedRowContainsCorrectAgentId() throws IOException {
        Declaration d = createValidatedDeclaration("999999999");
        d.addBusinessParticipation(createOrg("MegaCorp"), 987654321L, 50000.0, 25.0);
        repo.save(d);

        Path out = tempDir.resolve("holdings.csv");
        controller.exportToCsv(out.toString());
        List<String> lines = Files.readAllLines(out);
        assertTrue(lines.get(1).startsWith("999999999,"));
    }

    @Test
    void ensureDeclarationWithNoParticipationsProducesNoDataRows() throws IOException {
        Declaration d = createValidatedDeclaration("333333333");
        repo.save(d);

        Path out = tempDir.resolve("holdings.csv");
        controller.exportToCsv(out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
    }
}
