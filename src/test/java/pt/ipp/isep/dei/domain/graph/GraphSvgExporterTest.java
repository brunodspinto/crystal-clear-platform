package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GraphSvgExporterTest {

    @TempDir
    Path tempDir;

    private Person makePerson(String id) {
        return new Person(id, "politician", "2020-01-01", "", "Alice", "1990-01-01", "Portuguese");
    }

    private Organization makeOrg(String id) {
        return new Organization(id, "company", "2015-01-01", "", "Acme", "private", "Portugal");
    }

    private Position makePosition(String id) {
        return new Position(id, "public", "2019-01-01", "", "Minister", "government", "O-001");
    }

    private Asset makeAsset(String id) {
        return new Asset(id, "property", "2018-01-01", "", "real_estate", "Portugal", 500000.0);
    }

    @Test
    void ensureExportCreatesFile() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.emptyList(), Collections.emptyList(), out.toString());
        assertTrue(Files.exists(out));
    }

    @Test
    void ensureOutputIsValidSvg() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.emptyList(), Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.startsWith("<?xml"));
        assertTrue(content.contains("<svg "));
        assertTrue(content.endsWith("</svg>" + System.lineSeparator()));
    }

    @Test
    void ensurePersonNodeAppearsAsCircle() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("<circle "));
    }

    @Test
    void ensureOrganizationNodeAppearsAsRect() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makeOrg("O-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("<rect "));
    }

    @Test
    void ensurePositionNodeAppearsAsDiamondWithFourPoints() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePosition("J-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        Matcher m = Pattern.compile("points=\"([^\"]+)\"").matcher(content);
        assertTrue(m.find(), "No polygon found");
        long pairs = Arrays.stream(m.group(1).trim().split("\\s+")).count();
        assertEquals(4, pairs, "Diamond (position) must have 4 coordinate pairs");
    }

    @Test
    void ensureAssetNodeAppearsAsTriangleWithThreePoints() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makeAsset("A-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        Matcher m = Pattern.compile("points=\"([^\"]+)\"").matcher(content);
        assertTrue(m.find(), "No polygon found");
        long pairs = Arrays.stream(m.group(1).trim().split("\\s+")).count();
        assertEquals(3, pairs, "Triangle (asset) must have 3 coordinate pairs");
    }

    @Test
    void ensureEntityHyperlinkAnchorIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-001")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("href=\"#detail-P-001\""));
        assertTrue(content.contains("id=\"detail-P-001\""));
    }

    @Test
    void ensureEdgeLineIsDrawn() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        List<Entity> entities = Arrays.asList(makePerson("P-1"), makeOrg("O-1"));
        List<Edge> edges = Collections.singletonList(new Edge("P-1", "O-1", "employment", 1.0));
        GraphSvgExporter.export(entities, edges, out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("<line "));
        assertTrue(content.contains("employment"));
    }

    @Test
    void ensureEdgeHyperlinkAnchorIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        List<Entity> entities = Arrays.asList(makePerson("P-1"), makeOrg("O-1"));
        List<Edge> edges = Collections.singletonList(new Edge("P-1", "O-1", "employment", 1.0));
        GraphSvgExporter.export(entities, edges, out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("id=\"detail-rel-P-1-O-1\""));
    }

    @Test
    void ensureDetailSectionTitleIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("Entity &amp; Relation Details"));
    }

    @Test
    void ensureMultipleEntitiesAllHaveAnchors() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        List<Entity> entities = Arrays.asList(
                makePerson("P-1"), makeOrg("O-1"), makePosition("J-1"), makeAsset("A-1"));
        GraphSvgExporter.export(entities, Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("id=\"detail-P-1\""));
        assertTrue(content.contains("id=\"detail-O-1\""));
        assertTrue(content.contains("id=\"detail-J-1\""));
        assertTrue(content.contains("id=\"detail-A-1\""));
    }
}
