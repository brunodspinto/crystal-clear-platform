# US22 - Detect Direct Nepotism

## Scope
Go through the relations graph (US20) and flag the cases where someone was appointed by a person they have a personal tie with. A case is a pair `appointedBy` + personal tie between the same two entities, where personal tie is one of `relativeOf`, `friendOf` or `associatedWith`. The symmetric ties are already mirrored by the `GraphBuilder`, so the detector only needs to look at outgoing edges.

## Dependencies
- **US19** — entities loaded from the CSV.
- **US20** — the `RelationGraph` with the edges (and the mirroring of the symmetric relation types).

## Main pieces
- `pt.ipp.isep.dei.domain.graph.NepotismDetector` — `detect(graph)` returns the list of `NepotismPair` (appointed, appointer, tie label).
- `pt.ipp.isep.dei.controller.DetectNepotismController` + `DetectNepotismUI` (console).

## Tests and implementation
See [04.tests-and-implementation/US22-tests-and-implementation.md](04.tests-and-implementation/US22-tests-and-implementation.md). Tests in `NepotismDetectorTest`.
