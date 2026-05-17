# US26 - Export Graph as Interactive SVG with Hyperlinks

## 4. Tests

The rendering pipeline has two steps: build the DOT source (covered by `GraphDotExporterTest`), and run Graphviz to produce the SVG file (covered by `ExportGraphSvgControllerTest`).

**Test 1:** Check that the DOT output starts with a `digraph` declaration.

    @Test
    void ensureExportContainsDigraphDeclaration() {
        String dot = GraphDotExporter.export(new ArrayList<>(), new ArrayList<>());
        assertTrue(dot.contains("digraph G {"));
    }

**Test 2:** Check that a Person entity is rendered with shape `ellipse`.

    @Test
    void ensurePersonNodeUsesEllipseShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "2020-01-01", "", "Alice", "1980-01-01", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=ellipse"));
    }

**Test 3:** Check that an Organization entity is rendered with shape `box`.

    @Test
    void ensureOrganizationNodeUsesBoxShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Organization("O-001", "company", "2010-01-01", "", "Acme", "private", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=box"));
    }

**Test 4:** Check that a Position entity is rendered with shape `diamond`.

    @Test
    void ensurePositionNodeUsesDiamondShape() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Position("J-001", "role", "2020-01-01", "", "Minister", "political", "O-001"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("shape=diamond"));
    }

**Test 5:** Check that an Asset entity is rendered with shape `triangle`.

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

**Test 9:** Check that entity nodes contain a tooltip with the entity details (AC1).

    @Test
    void ensureEntityNodeContainsTooltipWithDetails() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "2020-01-01", "", "Alice", "1980-01-01", "PT"));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("tooltip="));
        assertTrue(dot.contains("Alice"));
        assertTrue(dot.contains("politician"));
    }

**Test 10:** Check that edges contain a tooltip with relation details (AC1).

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

**Test 11:** Check that a node with a URL contains the `URL` attribute in the DOT output.

    @Test
    void ensureNodeWithUrlContainsUrlAttribute() {
        List<Entity> entities = new ArrayList<>();
        Person person = new Person("P-001", "politician", "", "", "Alice", "", "");
        person.setUrl("https://example.com/alice");
        entities.add(person);
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertTrue(dot.contains("URL=\"https://example.com/alice\""));
    }

**Test 12:** Check that a node without a URL does not emit a `URL` attribute.

    @Test
    void ensureNodeWithoutUrlOmitsUrlAttribute() {
        List<Entity> entities = new ArrayList<>();
        entities.add(new Person("P-001", "politician", "", "", "Alice", "", ""));
        String dot = GraphDotExporter.export(entities, new ArrayList<>());
        assertFalse(dot.contains("URL="));
    }

**Test 13:** End-to-end check that running the controller writes the SVG file produced by Graphviz.

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

**Test 14:** Check that the entity count reported by the controller matches the entities in the CSV.

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

**Test 15:** Check that missing CSV files raise an IOException.

    @Test
    void ensureExportThrowsOnMissingFile() {
        assertThrows(IOException.class, () ->
                controller.exportToSvg("/nonexistent/path.csv", "/nonexistent/rel.csv", "/tmp/out.svg"));
    }


## 5. Construction (Implementation)

### Class Entity (abstract)

Holds common fields for all entity types. The `url` field is optional and enables clickable hyperlinks in the exported SVG. The abstract method `getDetails()` is implemented by each subclass to return a human-readable summary of its specific fields, used in tooltips.

```java
public abstract class Entity {
    private final String id;
    private final String type;
    private final String startDate;
    private final String endDate;
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url == null ? "" : url; }

    public abstract String getDetails();
}
```

Each subclass implements `getDetails()`, for example in `Person`:

```java
@Override
public String getDetails() {
    StringBuilder sb = new StringBuilder("Name: ").append(name);
    if (!birthDate.isEmpty()) sb.append(" | Born: ").append(birthDate);
    if (!nationality.isEmpty()) sb.append(" | Nationality: ").append(nationality);
    return sb.toString();
}
```

### Class GraphDotExporter

Builds the Graphviz DOT representation from a list of entities and a list of edges. Each entity node carries a `tooltip` attribute with the entity details (AC1) and, when a URL is set, a `URL` attribute that makes the node clickable in the browser. Edges carry a `tooltip` with the relation details.

```java
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
        dot.append(", style=filled");
        dot.append(", tooltip=\"").append(escape(tooltipFor(entity))).append("\"");
        if (!entity.getUrl().isEmpty()) {
            dot.append(", URL=\"").append(escape(entity.getUrl())).append("\"");
        }
        dot.append("];\n");
    }

    for (Edge edge : edges) {
        dot.append("  \"").append(escape(edge.getFromId())).append("\" -> \"");
        dot.append(escape(edge.getToId())).append("\"");
        dot.append(" [label=\"").append(escape(edge.getLabel())).append("\"");
        dot.append(", tooltip=\"").append(escape(tooltipFor(edge))).append("\"");
        dot.append("];\n");
    }

    dot.append("}\n");
    return dot.toString();
}

private static String tooltipFor(Entity entity) {
    StringBuilder sb = new StringBuilder();
    sb.append("ID: ").append(entity.getId());
    sb.append(" | Type: ").append(entity.getType());
    if (!entity.getStartDate().isEmpty()) sb.append(" | Start: ").append(entity.getStartDate());
    if (!entity.getEndDate().isEmpty()) sb.append(" | End: ").append(entity.getEndDate());
    sb.append(" | ").append(entity.getDetails());
    return sb.toString();
}

private static String tooltipFor(Edge edge) {
    return "Relation: " + edge.getLabel()
            + " | From: " + edge.getFromId()
            + " | To: " + edge.getToId()
            + " | Weight: " + edge.getWeight();
}
```

Entity types are mapped to distinct shapes and colours:
- **Person**: `ellipse`, blue `#7ec8e3`
- **Organization**: `box`, yellow `#f9c74f`
- **Position**: `diamond`, green `#90be6d`
- **Asset**: `triangle`, orange `#f8961e`

### Class EntityCsvParser

Reads an optional 8th column from the entities CSV as the URL for that entity. When present and non-empty, `setUrl()` is called on the entity after construction. Existing CSVs without the URL column continue to work unchanged.

### Class ExportGraphSvgController

Parses both CSV files, writes the DOT source next to the output SVG, then invokes the Graphviz `dot` binary to convert the DOT into SVG.

```java
public int exportToSvg(String entitiesCsvPath, String relationsCsvPath, String outputSvgPath)
        throws IOException {
    List<Entity> entities = EntityCsvParser.parse(entitiesCsvPath);
    List<Edge> edges = RelationCsvParser.parse(relationsCsvPath);
    // ... writes DOT file and calls Graphviz dot binary
    return entities.size();
}
```


## 6. Integration and Demo

* An option **"Export Graph (SVG)"** is available in the Administrator menu.
* The user provides three paths: the entities CSV, the relations CSV, and the output SVG file.
* The controller writes a sibling `.dot` file and invokes the Graphviz `dot` binary to produce the SVG.
* Opening the SVG in a browser: hovering over any node or edge shows a tooltip with its full details; nodes with a URL in the entities CSV are also clickable.
* Sample CSV files for testing hyperlinks are available at `src/test/resources/graph/entities_us26_hyperlinks.csv` and `relations_us26_hyperlinks.csv`.
* A sample output is available at `docs/system-documentation/US26/us26_graph.svg`.


## 7. Observations

* The SVG is produced by Graphviz: layout, shapes, labels, and arrows are all rendered by the `dot` engine, which must be installed on the system PATH.
* Tooltips are shown natively by browsers when hovering over SVG elements with a `tooltip` attribute.
* The URL column in the entities CSV is optional: omitting it produces a non-clickable node with a tooltip only.
* Quotes in entity ids and labels are escaped in the DOT output so that special characters do not break the syntax.
