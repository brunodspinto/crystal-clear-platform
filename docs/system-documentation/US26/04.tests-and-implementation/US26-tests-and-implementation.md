# US26 - Export Graph as Interactive SVG with Hyperlinks

## 4. Tests

The rendering pipeline has two steps: build the DOT source (covered by `GraphDotExporterTest`), and run Graphviz to produce the SVG file (covered by `ExportGraphSvgControllerTest`).

**Test 1:** Check that the DOT output starts with a `digraph` declaration.

    @Test
    void ensureExportContainsDigraphDeclaration() {
        String dot = GraphDotExporter.export(new ArrayList<>(), new ArrayList<>());
        assertTrue(dot.contains("digraph G {"));
    }

**Test 2:** Check that a Person entity is rendered with shape `ellipse` (AC1: distinct shape per type).

    @Test
    void ensurePersonNodeUsesEllipseShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "2020-01-01", "", "Alice", "1980-01-01", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=ellipse"));
    }

**Test 3:** Check that an Organization entity is rendered with shape `box` (AC1).

    @Test
    void ensureOrganizationNodeUsesBoxShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Organization("O-001", "company", "2010-01-01", "", "Acme", "private", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=box"));
    }

**Test 4:** Check that a Position entity is rendered with shape `diamond` (AC1).

    @Test
    void ensurePositionNodeUsesDiamondShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Position("J-001", "role", "2020-01-01", "", "Minister", "political", "O-001"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=diamond"));
    }

**Test 5:** Check that an Asset entity is rendered with shape `triangle` (AC1).

    @Test
    void ensureAssetNodeUsesTriangleShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Asset("A-001", "asset", "2020-01-01", "", "real_estate", "PT", 150000.0));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=triangle"));
    }

**Test 6:** Check that an edge between two entities carries its label.

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

**Test 7:** Check that an empty graph still produces a valid DOT block.

    @Test
    void ensureEmptyGraphProducesValidDot() {
        String dot = GraphDotExporter.export(new ArrayList<>(), new ArrayList<>());
        assertTrue(dot.startsWith("digraph G {"));
        assertTrue(dot.trim().endsWith("}"));
    }

**Test 8:** Check that special characters (quotes) in entity names are escaped in the DOT output.

    @Test
    void ensureQuotesInNamesAreEscaped() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice \"The Boss\"", "", ""));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("\\\""));
    }

**Test 9:** End-to-end check that running the controller writes the SVG file produced by Graphviz.

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

**Test 10:** Check that the entity count reported by the controller matches the entities in the CSV.

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

**Test 11:** Check that missing CSV files raise an IOException.

    @Test
    void ensureExportThrowsOnMissingFile() {
        assertThrows(IOException.class, () ->
                controller.exportToSvg("/nonexistent/path.csv", "/nonexistent/rel.csv", "/tmp/out.svg"));
    }


## 5. Construction (Implementation)

### Class GraphDotExporter

Builds the Graphviz DOT representation from a list of entities and a list of edges. Each entity type is mapped to a distinct shape and fill colour.

```java
public class GraphDotExporter {

    private GraphDotExporter() {}

    public static String export(List<Entity> entities, List<Edge> edges) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [fontname=\"Helvetica\", fontsize=10];\n");
        dot.append("  edge [fontname=\"Helvetica\", fontsize=9];\n");
        for (Entity entity : entities) {
            dot.append("  \"").append(escape(entity.getId())).append("\"");
            dot.append(" [label=\"").append(escape(shortLabel(entity))).append("\"");
            dot.append(", shape=").append(shapeFor(entity));
            dot.append(", fillcolor=\"").append(colorFor(entity)).append("\"");
            dot.append(", style=filled];\n");
        }
        for (Edge edge : edges) {
            dot.append("  \"").append(escape(edge.getFromId())).append("\" -> \"");
            dot.append(escape(edge.getToId())).append("\"");
            dot.append(" [label=\"").append(escape(edge.getLabel())).append("\"];\n");
        }
        dot.append("}\n");
        return dot.toString();
    }
}
```

Entity types are mapped to distinct shapes and colours (AC1):
- **Person**: `ellipse`, blue `#7ec8e3`
- **Organization**: `box`, yellow `#f9c74f`
- **Position**: `diamond`, green `#90be6d`
- **Asset**: `triangle`, orange `#f8961e`

### Class ExportGraphSvgController

Parses both CSV files, writes the DOT source next to the output SVG, then invokes the Graphviz `dot` binary to convert the DOT into SVG.

```java
public int exportToSvg(String entitiesCsvPath, String relationsCsvPath, String outputSvgPath)
        throws IOException {
    List<Entity> entities = EntityCsvParser.parse(entitiesCsvPath);
    List<Edge> edges = RelationCsvParser.parse(relationsCsvPath);

    String dotPath;
    if (outputSvgPath.endsWith(".svg")) {
        dotPath = outputSvgPath.substring(0, outputSvgPath.length() - 4) + ".dot";
    } else {
        dotPath = outputSvgPath + ".dot";
    }

    BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
    try {
        writer.write(GraphDotExporter.export(entities, edges));
    } finally {
        writer.close();
    }

    ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", dotPath, "-o", outputSvgPath);
    pb.redirectErrorStream(true);
    try {
        Process process = pb.start();
        int code = process.waitFor();
        if (code != 0) {
            throw new RuntimeException("Graphviz 'dot' failed with exit code " + code);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Graph rendering was interrupted.", e);
    }
    return entities.size();
}
```


## 6. Integration and Demo

* An option **"Export Graph (SVG)"** is available in the Administrator menu.
* The user provides three paths: the entities CSV, the relations CSV, and the output SVG file.
* The controller writes a sibling `.dot` file and invokes the Graphviz `dot` binary to produce the SVG.
* A sample output is available at `docs/system-documentation/US26/us26_graph.svg`.


## 7. Observations

* The SVG is produced by Graphviz: layout, shapes, labels, and arrows are all rendered by the `dot` engine, which must be installed on the system PATH.
* This matches the approach used by US20 (relations graph render) and avoids re-implementing a custom layout algorithm.
* The team contribution is the construction of the DOT source from the in-memory `Entity` and `Edge` lists; Graphviz handles the rendering.
* Quotes in entity ids and labels are escaped in the DOT output so that special characters do not break the syntax.
