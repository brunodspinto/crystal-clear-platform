# US22 - Detect Direct Nepotism

## Implementation

### Classes

| Class | Path |
|-------|------|
| `NepotismDetector` | `src/main/java/pt/ipp/isep/dei/domain/graph/NepotismDetector.java` |
| `NepotismDetector.NepotismPair` | (inner class of `NepotismDetector`) |
| `DetectNepotismController` | `src/main/java/pt/ipp/isep/dei/controller/DetectNepotismController.java` |
| `DetectNepotismUI` | `src/main/java/pt/ipp/isep/dei/ui/console/DetectNepotismUI.java` |
| `RelationGraph` | `src/main/java/pt/ipp/isep/dei/domain/graph/RelationGraph.java` |
| `Edge` | `src/main/java/pt/ipp/isep/dei/domain/graph/Edge.java` |
| `GraphRepository` | `src/main/java/pt/ipp/isep/dei/repository/GraphRepository.java` |

**`NepotismDetector`** contains the detection algorithm. It iterates every node in the graph looking for outgoing `appointedBy` edges. For each such edge `B → A` (meaning "B was appointed by A"), it calls `findPersonalTie`, which searches both the `A → B` and the `B → A` directions for any edge labelled `relativeOf`, `friendOf`, or `associatedWith`. Checking both directions makes the detector robust to inconsistent CSV data where the personal-tie edge may be stored in either direction. When a personal tie is found, a `NepotismPair(appointer=A, appointed=B, label)` is appended to the results list. AC1 is met because the loop never short-circuits — all pairs are collected before returning.

**`NepotismPair`** is an immutable value object. Its constructor validates that none of the three fields (appointer, appointed, relationshipLabel) are null or blank, throwing `IllegalArgumentException` otherwise. It overrides `equals` based on all three fields and provides a readable `toString`.

**`DetectNepotismController`** retrieves the `RelationGraph` from `GraphRepository` and throws `IllegalStateException` if no graph has been built yet (i.e., US19/US20 have not been run). It then delegates to `NepotismDetector.detect()`. The constructor accepts an injected `GraphRepository` to allow unit testing without the singleton.

**`DetectNepotismUI`** requires no user input. It calls `controller.detect()`, handles the `IllegalStateException` with a clear message, and prints all detected pairs with appointer, appointed, and relationship type clearly labelled.

### Acceptance criteria mapping

| AC | Where enforced |
|----|----------------|
| AC1 – All pairs of individuals involved in direct nepotism must be listed | `NepotismDetector.detect()` always appends every found pair without early exit; `DetectNepotismController.detect()` returns the full list |

### Key design decisions

- The personal-tie lookup is symmetric (checks both edge directions) because the US specification does not mandate a particular direction for `relativeOf`, `friendOf`, or `associatedWith` in the CSV; polarity is often inconsistent in real data.
- The detection runs over the full graph in O(V·E) time, acceptable for the dataset sizes expected in this application.
- The `appointedBy` edge is treated as directional (`B appointedBy A` = A appointed B) because appointment is an asymmetric act.

---

## Tests

### `src/test/java/pt/ipp/isep/dei/domain/graph/NepotismDetectorTest.java`

#### NepotismPair value object

| Test | Description |
|------|-------------|
| `ensureNepotismPairStoresFields` | `getAppointer()`, `getAppointed()`, and `getRelationshipLabel()` return the values supplied at construction |
| `ensureNepotismPairRejectsBlankAppointer` | Null and blank appointer throw `IllegalArgumentException` |
| `ensureNepotismPairRejectsBlankAppointed` | Null and blank appointed throw `IllegalArgumentException` |
| `ensureNepotismPairRejectsBlankLabel` | Null and blank label throw `IllegalArgumentException` |
| `ensureNepotismPairEqualityConsidersAllFields` | Two pairs with identical fields are equal; differing label breaks equality |
| `ensureNepotismPairToStringContainsIds` | `toString()` contains both person ids and the relationship label |

#### detect() — guards

| Test | Description |
|------|-------------|
| `ensureDetectRejectsNullGraph` | `detect(null)` throws `IllegalArgumentException` |
| `ensureDetectReturnsEmptyForEmptyGraph` | Empty graph returns an empty list |

#### detect() — positive cases

| Test | Description |
|------|-------------|
| `ensureDetectFindsPairWhenAppointedByRelative` | `relativeOf` edge triggers detection; appointer and appointed are correct |
| `ensureDetectFindsPairWhenAppointedByFriend` | `friendOf` edge triggers detection |
| `ensureDetectFindsPairWhenAppointedByAssociate` | `associatedWith` edge triggers detection |
| `ensureDetectHandlesPersonalTieInReverseDirection` | Personal tie stored as `B → A` instead of `A → B` is still detected |

#### detect() — negative cases

| Test | Description |
|------|-------------|
| `ensureDetectReturnsEmptyWhenNoPersonalTie` | Appointment exists but the two persons share no personal tie |
| `ensureDetectReturnsEmptyWhenNoAppointmentEdge` | Personal tie exists but no `appointedBy` edge |
| `ensureDetectDoesNotFlagUnrelatedAppointment` | A second appointment with no personal tie is not included in results |

#### detect() — AC1

| Test | Description |
|------|-------------|
| `ensureDetectReturnsAllPairs` | Two independent nepotism pairs are both returned |

### `src/test/java/pt/ipp/isep/dei/controller/DetectNepotismControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureDetectThrowsWhenGraphIsMissing` | `IllegalStateException` is thrown when no graph has been built |
| `ensureDetectReturnsEmptyForEmptyGraph` | Empty graph produces an empty list |
| `ensureDetectReturnsPairThroughController` | Correct pair is returned end-to-end through the controller |
| `ensureDetectFlagsFriendAppointment` | `friendOf` trigger is routed correctly through the controller |
| `ensureDetectFlagsAssociateAppointment` | `associatedWith` trigger is routed correctly through the controller |
| `ensureDetectReturnsAllPairsAC1` | Two pairs are both returned, satisfying AC1 |
| `ensureDetectIgnoresAppointmentWithNoPersonalTie` | Appointment without a personal tie is not flagged |
