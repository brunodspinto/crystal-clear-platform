# US33 - Global Support Adjacency Matrix for a Temporal Snapshot

> As a Product Owner, I aim to represent the network's support graph in a global adjacency matrix (without considering edge directions or weights) for a given temporal snapshot.

## Implementation

### Classes

| Class | Path |
|-------|------|
| `GlobalSupportMatrixController` | `src/main/java/pt/ipp/isep/dei/controller/GlobalSupportMatrixController.java` |
| `GlobalSupportMatrixUI` | `src/main/java/pt/ipp/isep/dei/ui/console/GlobalSupportMatrixUI.java` |
| `SupportGraph` (reused from US34) | `src/main/java/pt/ipp/isep/dei/domain/graph/SupportGraph.java` |
| `NetworkSnapshot` (reused from US32) | `src/main/java/pt/ipp/isep/dei/domain/graph/NetworkSnapshot.java` |

**`GlobalSupportMatrixController`** receives a snapshot date and returns the support graph (undirected, unweighted) of the network active at that date. The pipeline reuses already existing building blocks:

1. `GraphRepository` is queried for the full entity and edge lists.
2. `NetworkSnapshot.of(date, entities, edges)` filters both lists down to whatever is active at the requested date and builds a directed `RelationGraph` for that slice.
3. `new SupportGraph(snapshot.getGraph())` converts the directed graph into an undirected, unweighted N×N boolean adjacency matrix. Edge directions and weights are dropped; self-loops are ignored.

The controller exposes:

- `hasData()` — true when there are entities loaded in the repository, used by the UI to guard against running before US19 has populated the repository.
- `buildFor(String date)` — validates the date is non-blank, builds the snapshot and returns the resulting `SupportGraph`.

**`GlobalSupportMatrixUI`** prompts the user for a snapshot date (`yyyy-MM-dd`), invokes the controller and prints the global adjacency matrix in tabular form with the entity ids as row and column headers. A 0/1 cell value indicates whether the row entity is adjacent to the column entity in the snapshot's support graph.

The UI is wired into `AdminUI` next to the US32 "Network Dynamics Over Time" entry so that the Product Owner / administrator can request the matrix on demand (AC1).

### Acceptance criteria mapping

| AC | Where enforced |
|----|----------------|
| AC1 – The global adjacency matrix must be shown if asked by the Product Owner | `GlobalSupportMatrixUI` reads the snapshot date, calls `GlobalSupportMatrixController.buildFor` and prints the full N×N matrix with id headers. The entry point is added to `AdminUI`. |

### Key design decisions

- The support graph is rebuilt for each requested date instead of being cached, because each snapshot date produces a different active sub-network and any cache would have to be keyed by date anyway.
- Edge directions are erased symmetrically in `SupportGraph`: a directed edge `u→v` sets both `adj[i][j]` and `adj[j][i]`. This is also why a single `boolean` value is used per cell, since AC says "without considering edge directions or weights".
- Self-loops are ignored to match the existing `SupportGraph` semantics (an entity is not adjacent to itself).
- The controller delegates date filtering entirely to `NetworkSnapshot`, so US33 stays consistent with US32 and US34 about what counts as "active at this date".

---

## Tests

### `src/test/java/pt/ipp/isep/dei/controller/GlobalSupportMatrixControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureHasDataReturnsFalseWhenEmpty` | An empty repository reports no data |
| `ensureHasDataReturnsTrueWhenEntitiesLoaded` | A repository with at least one entity reports data available |
| `ensureBuildForBlankDateThrows` | `null` and `""` dates throw `IllegalArgumentException` |
| `ensureBuildForReturnsSupportGraphSizedToActiveEntities` | The resulting support graph has one node per active entity |
| `ensureMatrixIsSymmetric` | The adjacency matrix is symmetric (`m[i][j] == m[j][i]`) for every pair |
| `ensureMatrixHasNoSelfLoops` | A self-edge in the source graph does not produce a diagonal `true` |
| `ensureReverseDirectionEdgeProducesSameAdjacency` | `a→b` and `b→a` yield the same undirected adjacency |
| `ensureEdgesInactiveAtDateAreExcluded` | An edge whose validity window does not contain the requested date is not represented in the matrix |
| `ensureEntitiesInactiveAtDateAreExcluded` | An entity whose validity window does not contain the requested date is not represented in the matrix |
