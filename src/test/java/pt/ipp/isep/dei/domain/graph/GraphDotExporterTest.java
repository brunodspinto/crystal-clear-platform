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
    void ensureSymmetricRelationIsDrawnAsBidirectional() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        entities.add(new Person("P-002", "politician", "", "", "Bob", "", ""));
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("P-001", "P-002", "friendOf", 1.0));
        String dot = GraphDotExporter.export(entities, edges);
        assertTrue(dot.contains("dir=both"));
    }

    @Test
    void ensureDirectedRelationIsNotMarkedBidirectional() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        entities.add(new Organization("O-001", "company", "", "", "Acme", "private", "PT"));
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("P-001", "O-001", "employment", 1.0));
        String dot = GraphDotExporter.export(entities, edges);
        assertFalse(dot.contains("dir=both"));
    }

    @Test
    void ensureSymmetricRelationListedBothWaysIsDrawnOnce() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        entities.add(new Person("P-002", "politician", "", "", "Bob", "", ""));
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("P-001", "P-002", "friendOf", 1.0));
        edges.add(new Edge("P-002", "P-001", "friendOf", 1.0));
        String dot = GraphDotExporter.export(entities, edges);
        assertEquals(1, countOccurrences(dot, "->"));
        assertTrue(dot.contains("dir=both"));
    }

    private int countOccurrences(String text, String needle) {
        int count = 0;
        int from = text.indexOf(needle);
        while (from >= 0) {
            count = count + 1;
            from = text.indexOf(needle, from + needle.length());
        }
        return count;
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

    @Test
    void ensureEntityNodeContainsTooltipWithDetails() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "2020-01-01", "", "Alice", "1980-01-01", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("tooltip="));
        assertTrue(dot.contains("Alice"));
        assertTrue(dot.contains("politician"));
    }

    @Test
    void ensureEdgeContainsTooltipWithDetails() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        entities.add(new Organization("O-001", "company", "", "", "Acme", "private", "PT"));
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge("P-001", "O-001", "employment", 0.8));
        String dot = GraphDotExporter.export(entities, edges);
        assertTrue(dot.contains("tooltip="));
        assertTrue(dot.contains("employment"));
        assertTrue(dot.contains("0.8"));
    }

    @Test
    void ensureNodeWithUrlContainsUrlAttribute() {
        List<Entity> entities = new ArrayList<>();
        Person person = new Person("P-001", "politician", "", "", "Alice", "", "");
        person.setUrl("https://example.com/alice");
        entities.add(person);
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("URL=\"https://example.com/alice\""));
    }

    @Test
    void ensureNodeWithoutUrlOmitsUrlAttribute() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertFalse(dot.contains("URL="));
    }
}
