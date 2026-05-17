package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphDotExporterTest {

    @Test
    void ensureExportContainsDigraphDeclaration() {
        String dot = GraphDotExporter.export(new ArrayList<>(), new ArrayList<>());
        assertTrue(dot.contains("digraph G {"));
    }

    @Test
    void ensurePersonNodeUsesEllipseShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "2020-01-01", "", "Alice", "1980-01-01", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=ellipse"));
    }

    @Test
    void ensureOrganizationNodeUsesBoxShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Organization("O-001", "company", "2010-01-01", "", "Acme", "private", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=box"));
    }

    @Test
    void ensurePositionNodeUsesDiamondShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Position("J-001", "role", "2020-01-01", "", "Minister", "political", "O-001"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=diamond"));
    }

    @Test
    void ensureAssetNodeUsesTriangleShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Asset("A-001", "asset", "2020-01-01", "", "real_estate", "PT", 150000.0));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=triangle"));
    }

    @Test
    void ensureEdgeLabelIsIncluded() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        entities.add(new Organization("O-001", "company", "", "", "Acme", "private", "PT"));
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("P-001", "O-001", "employment", 1.0));
        String dot = GraphDotExporter.export(entities, edges);
        assertTrue(dot.contains("employment"));
        assertTrue(dot.contains("P-001"));
        assertTrue(dot.contains("O-001"));
    }

    @Test
    void ensureEmptyGraphProducesValidDot() {
        String dot = GraphDotExporter.export(new ArrayList<>(), new ArrayList<>());
        assertTrue(dot.startsWith("digraph G {"));
        assertTrue(dot.trim().endsWith("}"));
    }

    @Test
    void ensureQuotesInNamesAreEscaped() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice \"The Boss\"", "", ""));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("\\\""));
    }
}
