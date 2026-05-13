package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BuildRelationsGraphControllerTest {

    @TempDir
    Path tempDir;

    @Test
    void ensureBuildsGraphFromCsv() throws IOException {
        Path relations = tempDir.resolve("relations.csv");
        Files.writeString(relations,
                "P-001;P-002;kinship;1.0\n" +
                "P-001;O-001;employment;0.8\n" +
                "P-002;O-001;employment;0.6\n");

        GraphRepository repo = new GraphRepository();
        BuildRelationsGraphController controller = new BuildRelationsGraphController(repo);

        BuildRelationsGraphController.BuildResult result = controller.buildFromCsv(relations.toString());

        assertEquals(3, result.getEdgeCount());
        assertEquals(3, result.getNodeCount());
        assertEquals(2, result.getLabels().size());
        assertTrue(result.getLabels().contains("kinship"));
        assertTrue(result.getLabels().contains("employment"));
        assertNotNull(repo.getRelationGraph());
    }

    @Test
    void ensureSkipsInvalidLines() throws IOException {
        Path relations = tempDir.resolve("relations.csv");
        Files.writeString(relations,
                "# comment line\n" +
                "P-001;P-002;kinship;1.0\n" +
                "\n" +
                "P-001;P-002\n" +
                "P-003;P-004;ownership;not-a-number\n" +
                "P-005;P-006;membership;0.5\n");

        GraphRepository repo = new GraphRepository();
        BuildRelationsGraphController controller = new BuildRelationsGraphController(repo);

        BuildRelationsGraphController.BuildResult result = controller.buildFromCsv(relations.toString());

        assertEquals(2, result.getEdgeCount());
        assertEquals(2, result.getLabels().size());
    }

    @Test
    void ensureRenderGraphToSvgWritesFile() throws IOException {
        Path relations = tempDir.resolve("relations.csv");
        Files.writeString(relations,
                "P-001;P-002;kinship;1.0\n" +
                "P-001;O-001;employment;0.8\n");

        GraphRepository repo = new GraphRepository();
        BuildRelationsGraphController controller = new BuildRelationsGraphController(repo);
        controller.buildFromCsv(relations.toString());

        Path svg = tempDir.resolve("graph.svg");
        String result = controller.renderGraphToSvg(svg.toString());

        assertEquals(svg.toString(), result);
        assertTrue(Files.exists(svg));
        String content = Files.readString(svg);
        assertTrue(content.contains("<svg "));
        assertTrue(content.contains("kinship") || content.contains("employment"));
    }

    @Test
    void ensureRenderGraphToSvgFailsWithoutGraph() {
        GraphRepository repo = new GraphRepository();
        BuildRelationsGraphController controller = new BuildRelationsGraphController(repo);
        Path svg = tempDir.resolve("graph.svg");
        assertThrows(IllegalStateException.class, () -> controller.renderGraphToSvg(svg.toString()));
    }

    @Test
    void ensureEmptyCsvProducesEmptyGraph() throws IOException {
        Path relations = tempDir.resolve("relations.csv");
        Files.writeString(relations, "");

        GraphRepository repo = new GraphRepository();
        BuildRelationsGraphController controller = new BuildRelationsGraphController(repo);

        BuildRelationsGraphController.BuildResult result = controller.buildFromCsv(relations.toString());

        assertEquals(0, result.getEdgeCount());
        assertEquals(0, result.getNodeCount());
        assertTrue(result.getLabels().isEmpty());
    }
}
