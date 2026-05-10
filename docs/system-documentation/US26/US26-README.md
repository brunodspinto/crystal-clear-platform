# US26 — Interactive Graph SVG with Hyperlinks

## Scope

As Product Owner, I want to interact with the heterogeneous and multi-relational graph using hyperlinks.

**AC1:** The hyperlinks for entities and relations should depict their details.

## How it works

The system generates an SVG file containing:

1. **Graph visualisation (top)** — entities placed in a circular layout; each entity type uses a distinct shape and colour:
   - Person → circle (blue)
   - Organization → rectangle (yellow)
   - Position → diamond (green)
   - Asset → triangle (orange)

2. **Hyperlinks** — every entity node and every edge label is wrapped in an SVG `<a href="#detail-ID">` element. Clicking navigates to the entity's detail card. Hovering shows a `<title>` tooltip with all fields.

3. **Detail section (bottom)** — one card per entity (`id="detail-ID"`) listing all fields; one line per relation.

## Dependencies

- **US19** (André) — entity types (Person, Organization, Position, Asset) and EntityCsvParser.
- **US20** (Tomás) — Edge and RelationCsvParser.

## Implementation

| Layer      | Class                                                       |
|------------|-------------------------------------------------------------|
| Domain     | `pt.ipp.isep.dei.domain.graph.GraphSvgExporter`             |
| Controller | `pt.ipp.isep.dei.controller.ExportGraphSvgController`       |
| UI         | `pt.ipp.isep.dei.ui.console.ExportGraphSvgUI`               |
| Menu       | `ProductOwnerUI` — "Export Graph (SVG with hyperlinks)"     |

## Tests

File: `src/test/java/pt/ipp/isep/dei/domain/graph/GraphSvgExporterTest.java` (11 tests, all passing)

- `ensureExportCreatesFile`
- `ensureOutputIsValidSvg`
- `ensurePersonNodeAppearsAsCircle`
- `ensureOrganizationNodeAppearsAsRect`
- `ensurePositionNodeAppearsAsDiamond`
- `ensureAssetNodeAppearsAsTriangle`
- `ensureEntityHyperlinkAnchorIsPresent`
- `ensureEdgeLineIsDrawn`
- `ensureEdgeHyperlinkAnchorIsPresent`
- `ensureDetailSectionTitleIsPresent`
- `ensureMultipleEntitiesAllHaveAnchors`

## Sample output

See `us26_graph.svg` in this folder (generated from the test dataset).
