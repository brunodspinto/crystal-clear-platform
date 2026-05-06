# US11 — Tests & Implementation

## 4. Tests

Unit tests in `ConsultAssetsControllerTest`:

- `ensureGetPoliticalAgentsReturnsAll` — controller exposes all registered agents (AC1).
- `ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered` — empty repo handled.
- `ensureAssetsFromAllValidatedDeclarationsUpToDateAreReturned` — assets from every validated declaration on or before the reference date are aggregated (AC2).
- `ensureDeclarationsAfterReferenceDateAreIgnored` — declarations submitted after the reference date are filtered out (AC2).
- `ensureNonValidatedDeclarationsAreIgnored` — pending/rejected declarations contribute no assets (AC2).
- `ensureEmptyResultWhenNoDeclarationsExistForAgent` — empty list when nothing matches (AC3).
- `ensureNullAgentIsRejected` — defensive check on `getAssetsAt`.
- `ensureNullDateIsRejected` — defensive check on `getAssetsAt`.
- `ensureIsCurrentUserJournalistReturnsFalseWhenNoAuthRepository` — masking helper degrades safely when no auth context is available.

I/O methods in the UI are excluded from unit testing as per the project's NFR.

## 5. Construction (Implementation)

### Files added

- `src/main/java/pt/ipp/isep/dei/controller/ConsultAssetsController.java`
- `src/main/java/pt/ipp/isep/dei/ui/console/ConsultAssetsUI.java`
- `src/test/java/pt/ipp/isep/dei/controller/ConsultAssetsControllerTest.java`

### Files changed

- `src/main/java/pt/ipp/isep/dei/ui/console/menu/JournalistUI.java` — added "Consult Assets" menu option.
- `src/main/java/pt/ipp/isep/dei/ui/console/menu/CitizenUI.java` — added "Consult Assets" menu option.

No change was needed in `DeclarationRepository`: the existing `getValidatedDeclarationsForAgentUpTo(agent, referenceDate)` (introduced for US09) already returns the right slice. Reusing it keeps the date/status filtering in a single place.

### Flow

1. The UI presents the list of registered political agents (from `PoliticalAgentRepository#getAll`) and the user picks one.
2. The user types the reference date.
3. The controller asks `DeclarationRepository#getValidatedDeclarationsForAgentUpTo` for the matching declarations and aggregates every `AssetEntry` across them.
4. If the resulting list is empty, the UI prints the AC3 message and returns.
5. Otherwise, the controller is asked whether the current user is a journalist; the UI then iterates the assets and prints each one. Citizens see asset values replaced by `***` and vehicle/stocks details masked; journalists see the full values and details (AC4).

### Acceptance criteria mapping

| AC | Where it is enforced |
|----|----------------------|
| AC1 — selected agent must be valid and registered | `displayAndSelectPoliticalAgent` only allows selection from `controller.getPoliticalAgents()`, which comes from the repository. |
| AC2 — only validated declarations up to the reference date | `DeclarationRepository#getValidatedDeclarationsForAgentUpTo` filters by `status == VALIDATED`, `agent.equals` and `submissionDate <= referenceDate`. |
| AC3 — empty result message | Handled in `ConsultAssetsUI#run` when the controller returns an empty list. |
| AC4 — citizens see masked sensitive values, journalists see full details | `ConsultAssetsController#isCurrentUserJournalist` checks the current `UserSession` for the `JOURNALIST` role; the UI masks asset values and detail strings when this returns `false`. |

## 6. Integration and Demo

- The "Consult Assets" option is plugged into both the Citizen menu (`CitizenUI`) and the Journalist menu (`JournalistUI`). The same `ConsultAssetsUI` is reused — the masking decision is taken at runtime based on the role of the logged-in user.
- Demo plan: log in as `journalist@news.pt` / `journalist`, pick "António Félix", reference date `31-12-2024`, and show the full asset list. Then log out and log in as `citizen@this.app` / `citizen`, repeat the flow, and show the same list with values replaced by `***`.

## 7. Observations

- The assets list is not chronologically ordered; the enunciado does not require ordering for this US. The presentation order matches the order in which declarations were saved in the repository.
- The masking is applied at presentation time only. The controller still returns the full `AssetEntry` objects to the UI; the UI is responsible for hiding the sensitive fields. This keeps the controller simple and lets future UIs (e.g., a JavaFX screen) reuse the same controller with their own masking strategy.
