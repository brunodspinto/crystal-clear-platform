# US23 - Detect Potential (Indirect) Conflicts of Interest

## Scope
Answer at least 5 questions over the relations graph about indirect conflicts of interest. Each answer is a `Chain` of entity ids that shows the path that creates the potential conflict. The queries implemented:

1. persons with relatives holding public positions (`relativeOf -> holdsPosition`);
2. persons with relatives in a given organisation (`relativeOf -> holdsPosition -> inOrganization`);
3. public officials that influence companies (`holdsPosition -> influences`);
4. persons associated with asset owners (`associatedWith -> ownerOf`);
5. persons appointed by members of an organisation (`appointedBy -> memberOf`).

## Dependencies
- **US19/US20** — entities + `RelationGraph` (symmetric ties already mirrored).
- **US22** — the direct nepotism case is the 2-edge version of these chains; the queries here go one hop further.

## Main pieces
- `pt.ipp.isep.dei.domain.graph.ConflictDetector` — one method per query, all returning `List<Chain>`.
- `pt.ipp.isep.dei.controller.DetectConflictsController` + `DetectConflictsUI` (console).

## Tests and implementation
See [04.tests-and-implementation/US23-tests-and-implementation.md](04.tests-and-implementation/US23-tests-and-implementation.md). Tests in `ConflictDetectorTest` (per query: chain found / empty graph / null guard).
