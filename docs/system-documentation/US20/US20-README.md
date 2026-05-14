# US20 — Graph relations between entities

## Scope
Model the relations (edges) between the entities extracted by US19. Each relation has:
- a source entity id and a target entity id,
- a relation label (kinship, employment, ownership, membership, ...),
- a weight, used later by US21 (adjacency matrices) and US22/US23 (graph queries).

## Dependencies
- **US19** (André) — entity extraction from `.csv`. Provides the entity ids that appear on both ends of an edge. While US19 is not yet in the repo, edges are decoupled by carrying ids as `String`s.

## Current state (Sprint 2, WIP)
- `pt.ipp.isep.dei.domain.graph.Edge` — minimal value type (from/to/label/weight) with validation and value equality.
- `pt.ipp.isep.dei.domain.graph.RelationGraph` — adjacency-list aggregate, supports `addEdge`, `neighbors`, `nodes`. Skeleton only.
- Tests in `EdgeTest`, `RelationGraphTest`.
- See `04.tests-and-implementation/README.md` for the running checklist.
