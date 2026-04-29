# US20 — Tests & Implementation

## Implementation files
- `src/main/java/pt/ipp/isep/dei/domain/graph/Edge.java` — relation between two entities (fromId, toId, label, weight); blank-id validation; value equality.
- `src/main/java/pt/ipp/isep/dei/domain/graph/RelationGraph.java` — adjacency-list aggregate; `addEdge`, `neighbors`, `nodes`, `nodeCount`. Skeleton, no graph algorithms yet.

## Tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/EdgeTest.java`
  - `ensureFieldsAreKept`
  - `ensureBlankIdsAreRejected`
  - `ensureEqualityIsValueBased`
- `src/test/java/pt/ipp/isep/dei/domain/graph/RelationGraphTest.java`
  - `ensureAddEdgeStoresIt`
  - `ensureUnknownNodeReturnsEmpty`
  - `ensureNodesContainsBothEnds`
  - `ensureNullEdgeIsRejected`

## Checklist
- [x] Edge value object
- [x] RelationGraph aggregate (skeleton)
- [ ] RelationType enum (waiting on US19 taxonomy)
- [ ] `RelationGraph#toAdjacencyMatrix()` — bridge to US21
- [ ] Adapter that builds a graph from US19's extracted entities
- [ ] Coverage ≥ 90% on `domain/graph`
