# US21 - Adjacency matrices for entity relations

## Scope
Take the relations from US20 and put them in adjacency matrices, one matrix per relation type (kinship, employment, ownership, ...). Each cell `M[i][j]` holds the weight of the edge between entity `i` and entity `j` (0 means no edge). These matrices are then consumed by US22 (chain queries) and US23 (path queries) where matrix multiplication and transpose are needed.

We keep one matrix per relation type because mixing relation types in the same matrix would lose information when multiplying (a chain "father-of -> employed-at" is different from "employed-at -> employed-at"). The aggregator that holds the map `RelationType -> AdjacencyMatrix` will be added once US19's taxonomy is final.

## Dependencies
- US20 (also mine, Tomás) - has the `RelationGraph` and `Edge` aggregate. The bridge `RelationGraph#toAdjacencyMatrix()` is still TODO on the US20 side.
- US19 (André) - entity extraction. Defines the entity ids that map to row/column indexes. Not in the repo yet, so for now indexes are assigned in the order edges get added (caller has to keep its own id->index map).

## Current state (Sprint 2, WIP)
- `pt.ipp.isep.dei.domain.graph.AdjacencyMatrix` - square matrix backed by `double[][]`. Methods:
    - `addEdge(from, to, weight)` - sets the cell
    - `getWeight(from, to)` - reads the cell
    - `hasEdge(from, to)` - true if weight != 0
    - `getSize()` - returns n (matrix is n x n)
- Construtor rejects size <= 0.
- Single relation type per matrix; the multi-relation case is one level above (one matrix per `RelationType`).

## Tests
File: `src/test/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrixTest.java`
- `ensureSizeIsKept`
- `ensureNonPositiveSizeIsRejected` (size 0 and -1)
- `ensureNewMatrixHasNoEdges`
- `ensureAddEdgeStoresWeight`

## Checklist
- [x] AdjacencyMatrix value object (square)
- [x] Basic tests
- [ ] `multiply(AdjacencyMatrix other)` - needed by US22/US23 to walk chains of length k
- [ ] `transpose()` - reverse direction queries
- [ ] Decide directed vs undirected (matches the pending decision in US20)
- [ ] Move from square (m=n) to m x n once we have relations between entities of different types
- [ ] Index registry mapping entity id (`String`) -> matrix index, so the matrix doesn't depend on the US19 id format
- [ ] Coverage >= 90% on `domain/graph`

## Sprint review demo plan
- Build a small graph by hand (3-4 nodes, 2 edges) using `RelationGraph` from US20
- Convert to `AdjacencyMatrix` (once `toAdjacencyMatrix()` lands)
- Show `getWeight` / `hasEdge` and (if `multiply` is in by then) a length-2 chain
