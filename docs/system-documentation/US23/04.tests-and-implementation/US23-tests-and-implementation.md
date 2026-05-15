# US23 - Detect Potential (Indirect) Conflicts of Interest

## Implementation

### Classes

| Class | Path |
|-------|------|
| `ConflictDetector` | `src/main/java/pt/ipp/isep/dei/domain/graph/ConflictDetector.java` |
| `ConflictDetector.Chain` | (inner class of `ConflictDetector`) |
| `DetectConflictsController` | `src/main/java/pt/ipp/isep/dei/controller/DetectConflictsController.java` |
| `DetectConflictsUI` | `src/main/java/pt/ipp/isep/dei/ui/console/DetectConflictsUI.java` |
| `RelationGraph` | `src/main/java/pt/ipp/isep/dei/domain/graph/RelationGraph.java` |
| `Edge` | `src/main/java/pt/ipp/isep/dei/domain/graph/Edge.java` |
| `GraphRepository` | `src/main/java/pt/ipp/isep/dei/repository/GraphRepository.java` |

**`ConflictDetector`** implements five graph-traversal queries over a `RelationGraph`. Each query returns a list of `Chain` objects. Every `Chain` stores an ordered sequence of entity IDs and exposes `getFirst()` and `getLast()` to satisfy AC2.

The five queries are:

| Index | Question | Traversal |
|-------|----------|-----------|
| Q1 | Which individuals have relatives who hold prominent positions? | `personA --relativeOf→ personB --holdsPosition→ position` |
| Q2 | Which individuals have relatives in a specific organisation? | `personA --relativeOf→ personB --holdsPosition→ position --inOrganization→ org` (filtered by org ID; blank/null = any) |
| Q3 | Which individuals in public organisations influence companies? | `org ← inOrganization ← position ← holdsPosition ← personA --influences→ company` (chain: org → position → person → company) |
| Q4 | Which individuals are associated with asset owners? | `personA --associatedWith→ personB --ownerOf→ asset` |
| Q5 | Which individuals were appointed by organisation members? | `personA --appointedBy→ personB --memberOf→ org` |

**`Chain`** is an immutable value object built with the factory method `Chain.of(String... ids)`, which requires at least two IDs. It exposes `getFirst()`, `getLast()`, and `getAll()` (defensive copy), and overrides `equals` based on the full ID sequence.

**`DetectConflictsController`** holds the question catalogue as a private `String[]` and exposes it via `getAvailableQuestions()` (returns a defensive copy). `runQuery(int queryIndex, String organisationId)` retrieves the `RelationGraph` from `GraphRepository` (throwing `IllegalStateException` if absent), then dispatches to the appropriate `ConflictDetector` method via a `switch`. An out-of-range index throws `IllegalArgumentException`. The `organisationId` parameter is only forwarded for Q2; it is ignored for all other queries.

**`DetectConflictsUI`** prints the numbered question catalogue, reads the user's choice, optionally reads an organisation ID filter for Q2, invokes `runQuery`, and prints each chain with its full path and clearly labelled first and last entities.

### Acceptance criteria mapping

| AC | Where enforced |
|----|----------------|
| AC1 – The question must be selected from a predefined list of at least 5 | `QUESTION_LABELS` array has 5 entries; `getAvailableQuestions()` returns all of them; `DetectConflictsUI` numbers and displays each one before reading input |
| AC2 – For every detected chain, the first and last entities must be revealed | `Chain.getFirst()` and `Chain.getLast()`; `DetectConflictsUI` prints both explicitly for each chain |

### Key design decisions

- Q3 collects all organisations the person holds a position in and all companies they influence, then emits one `Chain` per `(org, company)` pair. This produces a complete picture even when a person holds multiple positions or influences multiple companies.
- The Q2 organisation filter accepts null or blank as "match any", making it usable both when the user knows the specific organisation and when they want a broad scan.
- All five queries guard against a null graph at the controller level (`requireGraph`) rather than each query individually, keeping query code clean.

---

## Tests

### `src/test/java/pt/ipp/isep/dei/domain/graph/ConflictDetectorTest.java`

#### Chain value object

| Test | Description |
|------|-------------|
| `ensureChainOfReturnsCorrectFirstAndLast` | `getFirst()` returns the first id; `getLast()` returns the last id |
| `ensureChainGetAllReturnsAllIds` | `getAll()` returns all ids in the correct order |
| `ensureChainOfRejectsTooFewIds` | Fewer than two ids throws `IllegalArgumentException` |
| `ensureChainToStringContainsArrow` | `toString()` uses `->` as separator |

#### Q1 — relatives in positions

| Test | Description |
|------|-------------|
| `ensureQ1FindsChainWhenRelativeHoldsPosition` | Chain is found; first = personA, last = position |
| `ensureQ1ReturnsEmptyWhenNoRelatives` | No `relativeOf` edge → empty list |
| `ensureQ1ReturnsEmptyWhenRelativeHasNoPosition` | `relativeOf` edge present but no `holdsPosition` → empty list |
| `ensureQ1RejectsNullGraph` | `null` graph throws `IllegalArgumentException` |

#### Q2 — relatives in a specific organisation

| Test | Description |
|------|-------------|
| `ensureQ2FindsChainWhenRelativeIsInOrganisation` | Chain found; first = personA, last = matching org |
| `ensureQ2WithBlankFilterMatchesAllOrganisations` | `null` filter returns chains for both organisations linked to the position |
| `ensureQ2FiltersOutNonMatchingOrganisation` | Non-matching org ID returns empty list |

#### Q3 — public officials influencing companies

| Test | Description |
|------|-------------|
| `ensureQ3FindsChainWhenPublicOfficialInfluencesCompany` | Chain found; first = org, last = company |
| `ensureQ3ReturnsEmptyWhenNoInfluenceEdge` | Position and org exist but no `influences` edge → empty list |
| `ensureQ3ReturnsEmptyWhenNoPositionEdge` | `influences` edge exists but no `holdsPosition` → empty list |

#### Q4 — associated with asset owners

| Test | Description |
|------|-------------|
| `ensureQ4FindsChainWhenAssociateOwnsAsset` | Chain found; first = personA, last = asset |
| `ensureQ4ReturnsEmptyWhenAssociateOwnsNothing` | `associatedWith` edge present but no `ownerOf` → empty list |

#### Q5 — appointed by organisation member

| Test | Description |
|------|-------------|
| `ensureQ5FindsChainWhenAppointedByOrgMember` | Chain found; first = personA, last = org |
| `ensureQ5ReturnsEmptyWhenAppointingPersonIsNotOrgMember` | `appointedBy` edge present but appointer has no `memberOf` → empty list |

#### Multiple chains

| Test | Description |
|------|-------------|
| `ensureMultipleChainsAreAllReturned` | Two independent Q1 chains are both returned |

### `src/test/java/pt/ipp/isep/dei/controller/DetectConflictsControllerTest.java`

#### getAvailableQuestions — AC1

| Test | Description |
|------|-------------|
| `ensureAtLeastFiveQuestionsAreAvailable` | List has at least 5 entries |
| `ensureQuestionsListIsDefensiveCopy` | Clearing the returned list does not affect subsequent calls |

#### runQuery — guards

| Test | Description |
|------|-------------|
| `ensureRunQueryThrowsWhenGraphIsMissing` | `IllegalStateException` when no graph exists |
| `ensureRunQueryThrowsOnUnknownIndex` | Index 999 throws `IllegalArgumentException` |

#### runQuery — Q1 through Q5 (AC2 verified on each)

| Test | Description |
|------|-------------|
| `ensureQ1ReturnsChainThroughController` | Chain returned; `getFirst()` = P1, `getLast()` = J1 |
| `ensureQ2FiltersOrganisationThroughController` | Chain returned with correct org; wrong org returns empty |
| `ensureQ2ReturnsEmptyForWrongOrganisation` | Non-matching org ID produces empty result |
| `ensureQ3DetectsPublicOfficialInfluencingCompany` | Chain returned; first = org, last = company |
| `ensureQ4DetectsAssociationWithAssetOwner` | Chain returned; first = personA, last = asset |
| `ensureQ5DetectsAppointmentByOrgMember` | Chain returned; first = personA, last = org |
