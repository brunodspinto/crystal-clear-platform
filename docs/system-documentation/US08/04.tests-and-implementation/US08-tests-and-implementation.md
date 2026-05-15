# US08 - Validate a Declaration of Interests

## Implementation

### Classes

| Class | Path |
|-------|------|
| `ValidateDeclarationController` | `src/main/java/pt/ipp/isep/dei/controller/ValidateDeclarationController.java` |
| `ValidateDeclarationUI` | `src/main/java/pt/ipp/isep/dei/ui/console/ValidateDeclarationUI.java` |
| `ValidationRecord` | `src/main/java/pt/ipp/isep/dei/domain/ValidationRecord.java` |
| `ValidationComment` | `src/main/java/pt/ipp/isep/dei/domain/ValidationComment.java` |
| `ValidationOutcome` | `src/main/java/pt/ipp/isep/dei/domain/ValidationOutcome.java` |
| `Declaration` | `src/main/java/pt/ipp/isep/dei/domain/Declaration.java` |
| `ValidationRecordRepository` | `src/main/java/pt/ipp/isep/dei/repository/ValidationRecordRepository.java` |
| `DeclarationRepository` | `src/main/java/pt/ipp/isep/dei/repository/DeclarationRepository.java` |

**`ValidateDeclarationController`**: retrieves all `PENDING` declarations from `DeclarationRepository`, resolves the currently authenticated Ethics Committee Member from the session, and orchestrates the validation. On `processValidation()`, it enforces that the declaration is `PENDING` before mutating any state (returning `false` otherwise), updates the declaration status via `Declaration.setStatus(ValidationOutcome)`, creates a `ValidationRecord`, attaches any correction comments when the outcome is `RETURNED_FOR_CORRECTION`, and persists both the updated declaration and the record.

**`Declaration.setStatus(ValidationOutcome)`**: maps `VALIDATED` → `DeclarationStatus.VALIDATED` and `RETURNED_FOR_CORRECTION` → `DeclarationStatus.REJECTED`. This mapping is in the domain layer so the business rule is not scattered across UI or controller code.

**`ValidationRecord`**: audit record created for every validation action. Holds the member who acted, the declaration validated, the date, the outcome, and an ordered list of `ValidationComment` objects. The list is only populated when the outcome is `RETURNED_FOR_CORRECTION`. Getters return defensive copies.

**`ValidationComment`**: value object pairing a section name with a description of the inconsistency found. Both fields are validated as non-null and non-blank on construction (AC2 — the section/item with the inconsistency must be identified).

**`ValidateDeclarationUI`**: lists pending declarations, allows the member to select one, shows its full details via `getDeclarationDetails()`, prompts for the outcome, collects any correction comments if applicable, and confirms the result.

### Acceptance criteria mapping

| AC | Where enforced |
|----|----------------|
| AC1 – When correct, the declaration is validated | `processValidation()` sets status to `VALIDATED` and saves a `ValidationRecord` with outcome `VALIDATED` |
| AC2 – When incorrect, the section/item with inconsistencies must be commented | `ValidationComment` requires a non-blank section and comment; the UI prompts for at least one comment before accepting `RETURNED_FOR_CORRECTION` |

### Key design decisions

- The PENDING guard (`declaration.getStatus() != DeclarationStatus.PENDING → return false`) runs before any state change, so the controller is safe to call on any declaration without risking corrupt state.
- `ValidationRecord` is persisted separately from `Declaration`, providing a permanent, independently queryable audit trail without coupling the declaration aggregate to validation history.
- The validation date is set to `new Date()` inside the controller, consistent with the same convention used in US06 for the submission date.

## Tests

### `src/test/java/pt/ipp/isep/dei/domain/ValidationRecordTest.java`

| Test | Description |
|------|-------------|
| `ensureCreationWorks` | A valid `ValidationRecord` is created without exception |
| `ensureCreationFailsWithNullMember` | `null` member throws `IllegalArgumentException` |
| `ensureCreationFailsWithNullDeclaration` | `null` declaration throws `IllegalArgumentException` |
| `ensureCreationFailsWithNullDate` | `null` validation date throws `IllegalArgumentException` |
| `ensureCreationFailsWithNullOutcome` | `null` outcome throws `IllegalArgumentException` |
| `ensureCommentsListStartsEmpty` | Comment list is empty on creation |
| `ensureAddCommentWorks` | A single comment is added and the list has size 1 |
| `ensureMultipleCommentsCanBeAdded` | Three comments are all stored in order |
| `ensureAddCommentFailsWithNullSection` | `null` section throws `IllegalArgumentException` |
| `ensureAddCommentFailsWithBlankComment` | Blank comment text throws `IllegalArgumentException` |
| `ensureGettersReturnCorrectValues` | `getMember()`, `getDeclaration()`, `getValidationDate()`, and `getOutcome()` return the values provided at construction |
| `ensureGetCommentsReturnsDefensiveCopy` | Two successive calls to `getComments()` return different list instances |

### `src/test/java/pt/ipp/isep/dei/domain/ValidationCommentTest.java`

| Test | Description |
|------|-------------|
| `ensureCreationWorks` | A valid `ValidationComment` is created without exception |
| `ensureNullSectionFails` | `null` section throws `IllegalArgumentException` |
| `ensureBlankSectionFails` | Blank section throws `IllegalArgumentException` |
| `ensureNullCommentFails` | `null` comment text throws `IllegalArgumentException` |
| `ensureBlankCommentFails` | Blank comment text throws `IllegalArgumentException` |
| `ensureGetSectionReturnsCorrectValue` | `getSection()` returns the value provided at construction |
| `ensureGetCommentReturnsCorrectValue` | `getComment()` returns the value provided at construction |
| `ensureToStringContainsSection` | `toString()` output includes the section name |

### `src/test/java/pt/ipp/isep/dei/domain/DeclarationTest.java` (US08-relevant subset)

| Test | Description |
|------|-------------|
| `ensureSetStatusWithValidatedOutcomeSetsValidated` | `setStatus(ValidationOutcome.VALIDATED)` results in `DeclarationStatus.VALIDATED` |
| `ensureSetStatusWithReturnedForCorrectionSetsRejected` | `setStatus(RETURNED_FOR_CORRECTION)` results in `DeclarationStatus.REJECTED` |
| `ensureSetStatusWithNullOutcomeFails` | `setStatus((ValidationOutcome) null)` throws `IllegalArgumentException` |

### `src/test/java/pt/ipp/isep/dei/repository/ValidationRecordRepositoryTest.java`

| Test | Description |
|------|-------------|
| `ensureSaveWorks` | Saving a valid record returns `true` |
| `ensureSaveNullFails` | Saving `null` throws `IllegalArgumentException` |
| `ensureGetAllReturnsAllSaved` | After saving two records, `getAll()` returns a list of size 2 |
| `ensureEmptyRepositoryReturnsEmptyList` | `getAll()` on a fresh repository returns an empty list |
| `ensureGetAllReturnsDefensiveCopy` | Two successive calls to `getAll()` return different list instances |

## Checklist

- [x] `ValidationRecord`: audit aggregate with outcome, member, date, and ordered comment list
- [x] `ValidationComment`: value object with non-blank section and comment validation (AC2)
- [x] `Declaration.setStatus(ValidationOutcome)`: domain-level outcome-to-status mapping (AC1)
- [x] `ValidateDeclarationController`: PENDING guard, status update, record creation, comment attachment, dual persistence
- [x] `ValidateDeclarationUI`: declaration listing, detail display, outcome selection, comment collection
- [x] `EthicsCommitteeUI`: "Validate Declaration of Interests" option wired to `ValidateDeclarationUI`
- [x] 20 unit tests covering record construction, comment validation, status mapping, and repository behaviour
