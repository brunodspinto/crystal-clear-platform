# US20 — Tests & Implementation

## 4. Tests

Unit tests live under `src/test/java/pt/ipp/isep/dei/domain/graph/`.

### `EdgeTest` (3 tests)

| Test | What it checks |
|------|----------------|
| `ensureFieldsAreKept` | constructor stores `fromId`, `toId`, `label`, `weight` |
| `ensureBlankIdsAreRejected` | null/blank `fromId`, `toId`, or `label` throw |
| `ensureEqualityIsValueBased` | two edges with the same fields are equal |

### `RelationGraphTest` (11 tests)

| Test | What it checks |
|------|----------------|
| `ensureAddEdgeStoresIt` | edge is stored under its `fromId` |
| `ensureUnknownNodeReturnsEmpty` | `neighbors` of an unknown id returns empty |
| `ensureNodesContainsBothEnds` | both `fromId` and `toId` are registered as nodes |
| `ensureNullEdgeIsRejected` | null edge throws |
| `ensureToAdjacencyMatrixCopiesEdgeWeights` | bridge to US21 copies every edge weight |
| `ensureToAdjacencyMatrixWithLabelFiltersEdges` | label-filtered overload only includes matching edges |
| `ensureToAdjacencyMatrixRejectsNullRegistry` | null registry throws |
| `ensureToAdjacencyMatrixWithLabelRejectsBlankLabel` | blank label throws |
| `ensureAddNodeRegistersIsolatedNode` | `addNode("X")` makes `X` appear in `nodes()` with no neighbours |
| `ensureAddNodeIsIdempotent` | re-calling `addNode` after `addEdge` does not erase neighbours |
| `ensureAddNodeRejectsBlankId` | blank/null id throws |

### `RelationCsvParserTest` (9 tests)

| Test | What it checks |
|------|----------------|
| `ensureSampleFileIsParsed` | `relations_sample.csv` parses to 6 edges |
| `ensureCommentLinesAreSkipped` | lines starting with `#` are ignored |
| `ensureBlankLinesAreSkipped` | empty lines are ignored |
| `ensureLineWithTooFewFieldsIsSkipped` | lines with less than 4 fields are ignored |
| `ensureLineWithBadWeightIsSkipped` | unparseable weight skips the line |
| `ensureLineWithBlankIdsIsSkipped` | blank `fromId`, `toId`, or `label` skip the line |
| `ensureFieldsAreReadIntoTheEdge` | parsed values end up in the right `Edge` fields |
| `ensureMissingFileThrows` | non-existent file throws `IOException` |
| `ensureEmptyFileGivesEmptyList` | empty file gives an empty list |

### `GraphBuilderTest` (7 tests)

| Test | What it checks |
|------|----------------|
| `ensureAllEntitiesAreRegisteredAsNodes` | every entity passed in becomes a node |
| `ensureEdgesAreAdded` | every edge passed in is stored |
| `ensureIsolatedEntitiesStillAppearAsNodes` | entities without edges still appear in `nodes()` |
| `ensureEdgeWithUnknownEntityIsStillAddedAsNode` | edges referencing unknown ids still register them |
| `ensureNullEntitiesIsRejected` | null entity list throws |
| `ensureNullEdgesIsRejected` | null edge list throws |
| `ensureEmptyInputsGiveEmptyGraph` | empty inputs give an empty graph |

All graph package tests (47 in total, including `AdjacencyMatrixTest` and `IndexRegistryTest` from US21) pass locally.

## 5. Construction (Implementation)

### Class Edge

A value object holding `fromId`, `toId`, `label`, and `weight`. Validates that the three string fields are non-blank in the constructor; equality is value-based across all four fields.

### Class RelationGraph

Adjacency-list aggregate keyed by node id.

```java
public void addEdge(Edge e) {
    if (e == null) {
        throw new IllegalArgumentException("edge must not be null");
    }
    List<Edge> outgoing = adj.get(e.fromId());
    if (outgoing == null) {
        outgoing = new ArrayList<>();
        adj.put(e.fromId(), outgoing);
    }
    outgoing.add(e);
    if (!adj.containsKey(e.toId())) {
        adj.put(e.toId(), new ArrayList<>());
    }
}
```

`addEdge` registers the source node and appends the edge to its outgoing list. The target node is also registered (with an empty list) so it shows up in `nodes()` even when nothing leaves it.

```java
public void addNode(String id) {
    if (id == null || id.isBlank()) {
        throw new IllegalArgumentException("id must not be blank");
    }
    if (!adj.containsKey(id)) {
        adj.put(id, new ArrayList<>());
    }
}
```

`addNode` was added so that an entity from US19 can be registered as a graph node even when no relation involves it. It is idempotent: calling it again on a node that already has edges does not erase them.

`neighbors(id)` returns an unmodifiable list of the outgoing edges; `nodes()` returns the set of registered ids; `nodeCount()` returns its size.

The bridge to US21 lives here too:

```java
public AdjacencyMatrix toAdjacencyMatrix(String label, IndexRegistry registry) {
    // ... validation
    for (String id : adj.keySet()) {
        registry.indexFor(id);
    }
    AdjacencyMatrix m = new AdjacencyMatrix(registry.size());
    for (String fromId : adj.keySet()) {
        for (Edge e : adj.get(fromId)) {
            if (label.equals(e.label())) {
                int from = registry.indexFor(e.fromId());
                int to = registry.indexFor(e.toId());
                m.addEdge(from, to, e.weight());
            }
        }
    }
    return m;
}
```

### Class RelationCsvParser

Reads relations from a CSV file (`from_id;to_id;label;weight`). Lines that are empty or start with `#` are ignored. Lines with fewer than 4 fields, blank ids/label, or an unparseable weight are silently skipped — keeps the parser tolerant to dirty input without throwing in the middle of a batch.

```java
public static List<Edge> parse(String filePath) throws IOException {
    List<Edge> edges = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Edge edge = parseLine(line);
            if (edge != null) {
                edges.add(edge);
            }
        }
    }
    return edges;
}
```

### Class GraphBuilder

Composes a `RelationGraph` from a list of entities (US19) and a list of edges (parsed by `RelationCsvParser`). Every entity is registered first, so isolated entities still appear in graph queries; then every edge is added.

```java
public static RelationGraph build(List<Entity> entities, List<Edge> edges) {
    if (entities == null) {
        throw new IllegalArgumentException("entities must not be null");
    }
    if (edges == null) {
        throw new IllegalArgumentException("edges must not be null");
    }
    RelationGraph graph = new RelationGraph();
    for (Entity entity : entities) {
        graph.addNode(entity.getId());
    }
    for (Edge edge : edges) {
        graph.addEdge(edge);
    }
    return graph;
}
```

### Sample data

`src/test/resources/graph/relations_sample.csv` — 6 relations spread across 6 entities and 4 labels (employment, ownership, kinship, membership). Used by `RelationCsvParserTest#ensureSampleFileIsParsed` and as a starting point for the demo.

## 6. Integration and Demo

End-to-end pipeline:

```
[ entities_sample.csv ]                  [ relations_sample.csv ]
        |                                          |
        v                                          v
  EntityCsvParser.parse                  RelationCsvParser.parse
        |                                          |
        +------------------+-----------------------+
                           |
                           v
                  GraphBuilder.build
                           |
                           v
                    RelationGraph
                           |
              .toAdjacencyMatrix(label, registry)  ← bridge to US21
                           |
                           v
                    AdjacencyMatrix
```

Demo plan (sprint review):

1. `EntityCsvParser.parse("src/test/resources/graph/entities_sample.csv")` → list of entities.
2. `RelationCsvParser.parse("src/test/resources/graph/relations_sample.csv")` → list of edges.
3. `GraphBuilder.build(entities, edges)` → a populated `RelationGraph`.
4. Show `g.nodeCount()` and `g.neighbors("P-001")` — all 4 expected entities visible.
5. Hand the graph over to US21 to produce per-label adjacency matrices.

## 7. Observations

- `RelationType` is still kept as a free-form `String` on `Edge`. Promoting it to an enum is blocked on the US19 taxonomy being final — flagged in the README checklist.
- The graph is stored as **directed** adjacency lists, but `GraphBuilder` mirrors the symmetric relation types (`relativeOf`, `friendOf`, `associatedWith`): when the CSV only has one direction, the reverse edge is added automatically (keeping label, weight and dates). Directional types like `appointedBy` or `ownerOf` are never mirrored. This way US21/US22/US23 can just traverse outgoing edges without missing the other half of a bidirectional relation.
- `RelationCsvParser` swallows malformed lines instead of throwing, by design — the goal is to keep the demo running even when the source CSV has noise. A stricter mode could be added later if needed.
- `GraphBuilder` accepts edges that point to ids not in the entity list and registers them as nodes anyway. This keeps the graph self-consistent (every endpoint of an edge is a node) at the cost of letting unknown ids slip through silently.
