# US21 - Adjacency matrices for entity relations

## Scope
Take the relations from US20 and put them in adjacency matrices, one matrix per relation type (kinship, employment, ownership, ...). Each cell `M[i][j]` holds the weight of the edge between entity `i` and entity `j` (0 means no edge). These matrices are then consumed by US22 (chain queries) and US23 (path queries) where matrix multiplication and transpose are needed.

We keep one matrix per relation type because mixing relation types in the same matrix would lose information when multiplying (a chain "father-of -> employed-at" is different from "employed-at -> employed-at"). The aggregator that holds the map `RelationType -> AdjacencyMatrix` will be added once US19's taxonomy is final.

## Dependencies
- US20 (also mine, Tomás) - has the `RelationGraph` and `Edge` aggregate. The bridge `RelationGraph#toAdjacencyMatrix()` is still TODO on the US20 side.
- US19 (André) - entity extraction. Defines the entity ids that map to row/column indexes. Not in the repo yet, so for now indexes are assigned in the order edges get added (caller has to keep its own id->index map).


## Tests
File: `src/test/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrixTest.java`
- `ensureSizeIsKept`
- `ensureNonPositiveSizeIsRejected` (size 0 and -1)
- `ensureNewMatrixHasNoEdges`
- `ensureAddEdgeStoresWeight`


