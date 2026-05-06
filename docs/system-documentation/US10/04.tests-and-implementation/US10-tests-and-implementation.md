# US10 - Tests & Implementation

## 4. Tests

Tests live in `src/test/java/pt/ipp/isep/dei/controller/AnalyseIncomeEvolutionControllerTest.java` and cover the AC's of the US.

| Test | What it covers |
|---|---|
| `ensureGetPoliticalAgentsReturnsAll` | AC1 - the journalist gets the list of registered political agents |
| `ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered` | AC1 edge case |
| `ensureOnlyValidatedDeclarationsOfTheRequestedAgentAreReturned` | AC3 - only validated, only of the chosen agent |
| `ensureOnlyDeclarationsInsideThePeriodAreReturned` | AC3 - only declarations whose submission date is inside [start, end] |
| `ensureResultIsOrderedChronologicallyBySubmissionDate` | AC4 - declarations are sorted by submission date |
| `ensureEmptyResultWhenNoValidatedDeclarationsInPeriod` | AC5 - empty list when nothing matches (UI converts to message) |
| `ensureStartAfterEndIsRejected` | AC2 - start date must be <= end date |
| `ensureNullAgentIsRejected` | defensive |
| `ensureNullDatesAreRejected` | defensive |

The repository method `getValidatedDeclarationsForAgentBetween` is exercised through the controller (no separate repo test for now, can add if coverage is too low).

## 5. Construction (Implementation)

Files added/changed:

- `src/main/java/pt/ipp/isep/dei/controller/AnalyseIncomeEvolutionController.java` - new controller. Two constructors (singleton repos for normal use, injected repos for tests). Validates inputs (null agent, null dates, start after end) before going to the repository.
- `src/main/java/pt/ipp/isep/dei/repository/DeclarationRepository.java` - new method `getValidatedDeclarationsForAgentBetween(agent, startDate, endDate)`. Filters by status=VALIDATED, agent equality, and `start <= submissionDate <= end`. Sorts the result by submission date so AC4 is satisfied at the source.

The UI (`AnalyseIncomeEvolutionUI`) is not yet wired; will be added together with the journalist menu hook in a follow up. The controller is fully usable on its own and is what the tests exercise.

### Reuse from US09
US09 already had `getValidatedDeclarationsForAgentUpTo`. The new method is a small variation (period instead of "up to"), kept side by side rather than refactoring US09 - the existing US09 callers and tests stay untouched.

## 6. Integration and Demo

- Integrates with US06 (declarations exist) and US08 (only validated ones are considered). No code change needed there - the controller just queries by status.
- Demo plan for sprint review:
    1. seed the repo with 3-4 declarations for a single agent across different dates and statuses
    2. call `getIncomeEvolution(agent, start, end)` from a small driver
    3. show the chronologically ordered list of declarations + each one's income entries

## 7. Observations

- AC4 is implemented at the repository layer (sort there) and not at the UI - keeps the UI dumb.
- If later US needs the same period filter for non-validated declarations, the repo method can be generalised.
- The agent's role/income entries inside each declaration are read by the UI directly via `Declaration#get...` - no extra DTO for now.
