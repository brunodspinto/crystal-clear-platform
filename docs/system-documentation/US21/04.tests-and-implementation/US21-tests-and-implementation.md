# US21 — Tests & Implementation

## 4. Tests

Unit tests live in three classes under `src/test/java/pt/ipp/isep/dei/domain/graph/`.

### `AdjacencyMatrixTest` (9 tests)

| Test | What it checks |
|------|----------------|
| `ensureSizeIsKept` | constructor remembers the size |
| `ensureNonPositiveSizeIsRejected` | size 0 and -1 throw |
| `ensureNewMatrixHasNoEdges` | a fresh matrix has all zeros |
| `ensureAddEdgeStoresWeight` | `addEdge` writes the cell |
| `ensureMultiplyComputesPathsOfLengthTwo` | for `A->B (2.0)` and `B->C (3.0)`, `M*M` puts `6.0` at `A->C` |
| `ensureMultiplyRejectsNull` | null other → throws |
| `ensureMultiplyRejectsDifferentSize` | size mismatch → throws |
| `ensureTransposeSwapsRowsAndColumns` | `M[i][j]` ends up at `T[j][i]` |
| `ensureTransposeOfTransposeIsOriginal` | `M.transpose().transpose()` equals `M` cell-wise |

### `IndexRegistryTest` (8 tests)

| Test | What it checks |
|------|----------------|
| `ensureFirstIdGetsIndexZero` | first registered id gets index 0 |
| `ensureNewIdsGetSequentialIndexes` | next ids get 1, 2, 3, … |
| `ensureSameIdReturnsSameIndex` | re-asking gives the same index |
| `ensureSizeReflectsRegisteredIds` | `size()` counts unique ids |
| `ensureIdAtReturnsRegisteredId` | reverse lookup index → id works |
| `ensureIdAtRejectsOutOfRange` | negative or too-big index throws |
| `ensureContainsReportsRegisteredIds` | `contains` reflects registration; null returns false |
| `ensureBlankIdIsRejected` | null / empty / whitespace ids throw |

### `RelationGraphTest` (8 tests)

The 4 pre-existing tests for `addEdge`, `neighbors`, `nodes`, and null-edge plus 4 new ones for the US21 bridge:

| Test | What it checks |
|------|----------------|
| `ensureToAdjacencyMatrixCopiesEdgeWeights` | every edge stored in the graph appears in the matrix with its weight |
| `ensureToAdjacencyMatrixWithLabelFiltersEdges` | the `(label, registry)` overload only includes edges of the given label |
| `ensureToAdjacencyMatrixRejectsNullRegistry` | null registry throws |
| `ensureToAdjacencyMatrixWithLabelRejectsBlankLabel` | blank/null label throws |

All 25 tests pass locally.

## 5. Construction (Implementation)

### Files added

- `src/main/java/pt/ipp/isep/dei/domain/graph/IndexRegistry.java`
- `src/test/java/pt/ipp/isep/dei/domain/graph/IndexRegistryTest.java`

### Files changed

- `src/main/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrix.java` — added `multiply(other)` and `transpose()`.
- `src/main/java/pt/ipp/isep/dei/domain/graph/RelationGraph.java` — added the bridge methods `toAdjacencyMatrix(registry)` and `toAdjacencyMatrix(label, registry)`. The earlier replacement of `computeIfAbsent` with explicit `get`/`put` (for consistency with the rest of the team's code style) is also part of this US.
- `src/test/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrixTest.java` — 5 new tests for the new methods.
- `src/test/java/pt/ipp/isep/dei/domain/graph/RelationGraphTest.java` — 4 new tests for the bridge.

### Pieces

- **`AdjacencyMatrix`** — square `double[][]` of size `n x n`. Cells default to 0. `addEdge(from, to, w)` writes a cell, `getWeight` reads it, `hasEdge` returns true when the cell is non-zero.
- **`AdjacencyMatrix#multiply(other)`** — standard matrix multiplication. Three nested `for` loops; the inner one accumulates the dot product of row `i` of `this` with column `j` of `other`. `M * M` gives length-2 chains, `M * M * M` length-3, and so on. This is the primitive US22 / US23 will lean on.
- **`AdjacencyMatrix#transpose()`** — returns a new matrix where row `i` of the input is column `i` of the output. Used by US23 when a query has to walk the relation in the opposite direction.
- **`IndexRegistry`** — small helper that maps an entity id (`String`) to a contiguous integer index. The first id seen gets `0`, the next `1`, and so on. Asking for the index of an id already registered returns the same number. Backed by a single `ArrayList<String>`; the index of an id is its position in the list.
- **`RelationGraph#toAdjacencyMatrix(registry)`** — walks every edge in the graph, asks the registry for the from/to indexes (registering ids as needed) and writes the edge weight into the matrix.
- **`RelationGraph#toAdjacencyMatrix(label, registry)`** — same flow but skips edges whose label does not match. This is how US21 produces "one matrix per relation type".

### Design notes

- One matrix per relation type. Mixing types in the same matrix would confuse multiplication: a chain "father-of → employed-at" is a different relation from "father-of → father-of", and they should not collapse.
- The same `IndexRegistry` should be passed to every `toAdjacencyMatrix(label, …)` call so that all matrices share a consistent row/column layout and can be multiplied together later.
- Square matrix is enough for now (every entity is treated as the same kind of node). Once US19's entity taxonomy lands and we start having relations between different entity classes, this may need to grow into a rectangular `m x n` layout — flagged in the README checklist.

## 6. Integration and Demo

- US20 produces a `RelationGraph` from US19's extracted entities.
- US21 turns each relevant relation into an `AdjacencyMatrix` via the new bridge.
- US22 and US23 will consume those matrices via `multiply` and `transpose`.

Demo plan (sprint review):

1. Build a small `RelationGraph` by hand with 3 nodes and 2 edges of the same label.
2. Call `toAdjacencyMatrix("ownership", registry)` — show that the resulting matrix has 2 non-zero cells.
3. Call `m.multiply(m)` — show that the matrix now has the length-2 chain populated.
4. Call `m.transpose()` — show that rows and columns have been swapped.

## 7. Observations

- The bridge reads edges as directed, but since US20's `GraphBuilder` already mirrors the symmetric relation types (`relativeOf`, `friendOf`, `associatedWith`), the matrices for those labels come out symmetric without any extra work here. Directional labels (`appointedBy`, `ownerOf`, ...) keep their single direction.
- `IndexRegistry` uses a single `ArrayList` and linear search for lookups (`indexOf`). For the demo dataset this is fine; if the registry ever grows large, replacing it with a `Map<String, Integer>` is a one-liner change.
