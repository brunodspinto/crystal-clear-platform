package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class LoadEntitiesFromCsvControllerTest {

    private GraphRepository repo;
    private LoadEntitiesFromCsvController controller;

    @BeforeEach
    void setUp() {
        repo = new GraphRepository();
        controller = new LoadEntitiesFromCsvController(repo);
    }

    private String resourcePath(String name) {
        URL url = getClass().getClassLoader().getResource(name);
        assertNotNull(url, "Test resource not found: " + name);
        try {
            return Paths.get(url.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void ensureLoadEntitiesReturnsCorrectCount() throws IOException {
        int count = controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        assertEquals(12, count);
    }

    @Test
    void ensureLoadEntitiesStoresEntitiesInRepository() throws IOException {
        controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        assertEquals(12, repo.getAll().size());
    }

    @Test
    void ensureLoadEntitiesOnInvalidPathThrowsIOException() {
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.loadEntities("nonexistent/path/file.csv");
            }
        });
    }

    @Test
    void ensureLoadEntitiesCalledTwiceAccumulatesEntities() throws IOException {
        controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        assertEquals(24, repo.getAll().size());
    }

    @TempDir
    Path tempDir;

    @Test
    void ensureRenderEntitiesToSvgCreatesSvgAndDotFiles() throws IOException {
        controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        Path svg = tempDir.resolve("entities.svg");
        String result = controller.renderEntitiesToSvg(svg.toString());
        assertEquals(svg.toString(), result);
        assertTrue(Files.exists(svg));
        assertTrue(Files.exists(tempDir.resolve("entities.dot")));
    }

    @Test
    void ensureRenderEntitiesToSvgAddsDotExtensionWhenPathHasNoSvgSuffix() throws IOException {
        controller.loadEntities(resourcePath("graph/entities_sample.csv"));
        Path out = tempDir.resolve("entities_graph");
        controller.renderEntitiesToSvg(out.toString());
        assertTrue(Files.exists(tempDir.resolve("entities_graph.dot")));
    }
}
