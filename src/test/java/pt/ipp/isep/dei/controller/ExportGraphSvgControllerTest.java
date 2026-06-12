package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class ExportGraphSvgControllerTest {

    @TempDir
    Path tempDir;

    private final ExportGraphSvgController controller = new ExportGraphSvgController();

    @Test
    void ensureExportProducesSvgFile() throws IOException {
        Path entities = tempDir.resolve("entities.csv");
        Path relations = tempDir.resolve("relations.csv");
        Path output = tempDir.resolve("graph.svg");

        Files.writeString(entities,
                "person;P-001;politician;2020-01-01;2024-01-01;Alice Smith;1980-05-10;Portuguese\n" +
                "organization;O-001;company;2010-01-01;;Acme Corp;private;Portugal\n");
        Files.writeString(relations, "P-001;O-001;employment;1.0\n");

        int count = controller.exportToSvg(entities.toString(), relations.toString(), output.toString());

        assertTrue(Files.exists(output));
        assertEquals(2, count);
        String svg = Files.readString(output);
        assertTrue(svg.contains("<svg "));
    }

    @Test
    void ensureExportReturnsCorrectEntityCount() throws IOException {
        Path entities = tempDir.resolve("entities2.csv");
        Path relations = tempDir.resolve("relations2.csv");
        Path output = tempDir.resolve("graph2.svg");

        Files.writeString(entities,
                "person;P-001;politician;;;Alice;1980-01-01;PT\n" +
                "person;P-002;advisor;;;Bob;1975-01-01;PT\n" +
                "person;P-003;businessman;;;Carl;1970-01-01;PT\n");
        Files.writeString(relations, "");

        int count = controller.exportToSvg(entities.toString(), relations.toString(), output.toString());
        assertEquals(3, count);
    }

    @Test
    void ensureExportThrowsOnMissingFile() {
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.exportToSvg("/nonexistent/path.csv", "/nonexistent/rel.csv", "/tmp/out.svg");
            }
        });
    }
}
