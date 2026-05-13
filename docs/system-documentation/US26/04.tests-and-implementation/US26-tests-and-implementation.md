# US26 - Export Graph as Interactive SVG with Hyperlinks

## 4. Tests

**Test 1:** Check that the export creates the output file.

    @Test
    void ensureExportCreatesFile() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.emptyList(), Collections.emptyList(), out.toString());
        assertTrue(Files.exists(out));
    }

**Test 2:** Check that the output is a valid SVG document.

    @Test
    void ensureOutputIsValidSvg() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.emptyList(), Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.startsWith("<?xml"));
        assertTrue(content.contains("<svg "));
        assertTrue(content.endsWith("</svg>" + System.lineSeparator()));
    }

**Test 3:** Check that a Person entity is rendered as a circle (AC1: distinct shape per type).

    @Test
    void ensurePersonNodeAppearsAsCircle() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("<circle "));
    }

**Test 4:** Check that an Organization entity is rendered as a rectangle (AC1).

    @Test
    void ensureOrganizationNodeAppearsAsRect() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makeOrg("O-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("<rect "));
    }

**Test 5:** Check that a Position entity is rendered as a diamond polygon with 4 coordinate pairs (AC1).

    @Test
    void ensurePositionNodeAppearsAsDiamondWithFourPoints() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePosition("J-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        Matcher m = Pattern.compile("points=\"([^\"]+)\"").matcher(content);
        assertTrue(m.find());
        long pairs = Arrays.stream(m.group(1).trim().split("\\s+")).count();
        assertEquals(4, pairs);
    }

**Test 6:** Check that an Asset entity is rendered as a triangle polygon with 3 coordinate pairs (AC1).

    @Test
    void ensureAssetNodeAppearsAsTriangleWithThreePoints() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makeAsset("A-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        Matcher m = Pattern.compile("points=\"([^\"]+)\"").matcher(content);
        assertTrue(m.find());
        long pairs = Arrays.stream(m.group(1).trim().split("\\s+")).count();
        assertEquals(3, pairs);
    }

**Test 7:** Check that each entity node has an `href` anchor to its detail card and that the detail card `id` is present (AC1: hyperlinks depict entity details).

    @Test
    void ensureEntityHyperlinkAnchorIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-001")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("href=\"#detail-P-001\""));
        assertTrue(content.contains("id=\"detail-P-001\""));
    }

**Test 8:** Check that an edge between two entities is drawn as a line with the correct label.

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

**Test 9:** Check that each edge has a hyperlink anchor to its detail entry (AC1: hyperlinks depict relation details).

    @Test
    void ensureEdgeHyperlinkAnchorIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        List<Entity> entities = Arrays.asList(makePerson("P-1"), makeOrg("O-1"));
        List<Edge> edges = Collections.singletonList(new Edge("P-1", "O-1", "employment", 1.0));
        GraphSvgExporter.export(entities, edges, out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("id=\"detail-rel-P-1-O-1\""));
    }

**Test 10:** Check that the detail section title is present in the SVG.

    @Test
    void ensureDetailSectionTitleIsPresent() throws IOException {
        Path out = tempDir.resolve("graph.svg");
        GraphSvgExporter.export(Collections.singletonList(makePerson("P-1")),
                Collections.emptyList(), out.toString());
        String content = Files.readString(out);
        assertTrue(content.contains("Entity &amp; Relation Details"));
    }

**Test 11:** Check that multiple entities of different types all have their anchor ids in the SVG.

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


## 5. Construction (Implementation)

### Class GraphSvgExporter

```java
public class GraphSvgExporter {

    private static final int CX = 450;
    private static final int CY = 350;
    private static final int RADIUS = 270;

    private GraphSvgExporter() {}

    public static void export(List<Entity> entities, List<Edge> edges, String filePath) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            writeHeader(w, totalHeight);
            writeStyles(w);
            writeEdges(w, entities, edges);
            writeNodes(w, entities);
            writeDetailSection(w, entities, edges);
            writeFooter(w);
        }
    }
}
```

Entity types are mapped to distinct SVG shapes (AC1):
- **Person** → `<circle>` (blue `#7ec8e3`)
- **Organization** → `<rect>` (yellow `#f9c74f`)
- **Position** → `<polygon>` with 4 points / diamond (green `#90be6d`)
- **Asset** → `<polygon>` with 3 points / triangle (orange `#f8961e`)

Every node and edge label is wrapped in `<a href="#detail-ID">` linking to a detail card at the bottom of the SVG (AC1). Each card has `id="detail-ID"` so the in-page navigation works without JavaScript.

### Class ExportGraphSvgController

```java
public int exportToSvg(String entitiesCsvPath, String relationsCsvPath, String outputSvgPath)
        throws IOException {
    List<Entity> entities = EntityCsvParser.parse(entitiesCsvPath);
    List<Edge> edges = RelationCsvParser.parse(relationsCsvPath);
    GraphSvgExporter.export(entities, edges, outputSvgPath);
    return entities.size();
}
```


## 6. Integration and Demo

* An option **"Export Graph (SVG with hyperlinks)"** was added to the Product Owner menu.
* The user provides three paths: entities CSV, relations CSV, and the output SVG file.
* A sample output is available at `docs/system-documentation/US26/us26_graph.svg`.


## 7. Observations

* The SVG is generated in pure Java with `PrintWriter`: no external SVG or XML libraries are used.
* Special XML characters (`&`, `<`, `>`, `"`, `'`) in entity data are escaped via `xmlEscape()` to ensure valid SVG.
* Node positions are computed with a circular layout: `angle = 2π·i/n − π/2`, placing the first node at the top.
