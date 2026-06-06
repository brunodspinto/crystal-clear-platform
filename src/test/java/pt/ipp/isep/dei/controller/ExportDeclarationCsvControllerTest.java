package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.DeclarationRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportDeclarationCsvControllerTest {

    @TempDir
    Path tempDir;

    private PoliticalAgent createAgent(String tin) {
        return new PoliticalAgent("Agent " + tin, "agent@gov.pt", "12345678", tin, new Date(), null);
    }

    private Organization createOrg() {
        return new Organization("Parliament", OrganizationNature.PUBLIC, OrganizationType.POLITICAL_PARTY);
    }

    private Declaration createValidatedDeclaration(String tin) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(tin), new Date());
        d.addPositionEntry(createOrg(), "Deputy", PositionNature.PUBLIC,
                50000.0, 1000.0, 500.0, new Date(), null);
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    private Declaration createPendingDeclaration(String tin) {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(tin), new Date());
        d.addPositionEntry(createOrg(), "Deputy", PositionNature.PUBLIC,
                40000.0, 0.0, 0.0, new Date(), null);
        return d; // status stays PENDING
    }

    @Test
    void ensureExportSucceedsWithValidatedDeclarations() throws IOException {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createValidatedDeclaration("111111111"));

        ExportDeclarationCsvController controller = new ExportDeclarationCsvController(repo);

        Path out = tempDir.resolve("declarations.csv");
        boolean result = controller.exportToCsv(out.toString());

        assertTrue(result);
        assertTrue(Files.exists(out));
    }

    @Test
    void ensureExportSucceedsWhenRepositoryIsEmpty() throws IOException {
        ExportDeclarationCsvController controller =
                new ExportDeclarationCsvController(new DeclarationRepository());

        Path out = tempDir.resolve("declarations.csv");
        boolean result = controller.exportToCsv(out.toString());

        assertTrue(result);
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size()); // header only
    }

    @Test
    void ensureOnlyValidatedDeclarationsAreExported() throws IOException {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createValidatedDeclaration("111111111"));
        repo.save(createPendingDeclaration("222222222"));
        repo.save(createValidatedDeclaration("333333333"));

        ExportDeclarationCsvController controller = new ExportDeclarationCsvController(repo);

        Path out = tempDir.resolve("declarations.csv");
        controller.exportToCsv(out.toString());

        List<String> lines = Files.readAllLines(out);
        assertEquals(3, lines.size()); // header + 2 validated rows
    }

    @Test
    void ensureExportReturnsFalseWhenFilePathIsInvalid() {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createValidatedDeclaration("111111111"));

        ExportDeclarationCsvController controller = new ExportDeclarationCsvController(repo);

        // A path inside a directory that does not exist on any OS
        String invalidPath = tempDir.resolve("does-not-exist-dir").resolve("decl.csv").toString();
        boolean result = controller.exportToCsv(invalidPath);

        assertFalse(result);
    }

    @Test
    void ensureExportWritesHeaderAsFirstLine() throws IOException {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createValidatedDeclaration("111111111"));

        ExportDeclarationCsvController controller = new ExportDeclarationCsvController(repo);

        Path out = tempDir.resolve("declarations.csv");
        controller.exportToCsv(out.toString());

        String header = Files.readAllLines(out).get(0);
        assertTrue(header.startsWith("agent_id,role,declaration_type"));
    }
}
