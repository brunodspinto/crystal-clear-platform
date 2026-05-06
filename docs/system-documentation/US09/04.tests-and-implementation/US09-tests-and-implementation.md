# US09 — Tests & Implementation

## 4. Tests

Unit tests in `ConsultIntegratedSituationControllerTest`:

- `ensureGetPoliticalAgentsReturnsAll` — controller exposes all registered agents (AC1).
- `ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered` — empty repo handled.
- `ensureIntegratedSituationOnlyIncludesValidatedDeclarationsOfRequestedAgent` — non-validated and other-agent declarations are filtered out (AC2).
- `ensureIntegratedSituationOnlyIncludesDeclarationsUpToReferenceDate` — only declarations submitted on or before the reference date are returned (AC2).
- `ensureIntegratedSituationReturnsEmptyWhenNoMatchingDeclarations` — empty list when nothing matches (AC3).
- `ensureNullAgentIsRejected` — defensive check on `getIntegratedSituation`.
- `ensureNullReferenceDateIsRejected` — defensive check on `getIntegratedSituation`.

I/O methods in the UI are excluded from unit testing as per the project's NFR.

## 5. Construction (Implementation)

### Files added

- `src/main/java/pt/ipp/isep/dei/controller/ConsultIntegratedSituationController.java`
- `src/main/java/pt/ipp/isep/dei/ui/console/ConsultIntegratedSituationUI.java`
- `src/test/java/pt/ipp/isep/dei/controller/ConsultIntegratedSituationControllerTest.java`

### Files changed

- `src/main/java/pt/ipp/isep/dei/repository/DeclarationRepository.java` — added `getValidatedDeclarationsForAgentUpTo(agent, referenceDate)`. The filter logic lives in the repository because, per the design rationale, the repository is the Information Expert for declarations and is the natural place to filter by agent, status and date.

### Flow

1. The UI presents the list of registered political agents (from `PoliticalAgentRepository#getAll`) and the user picks one.
2. The user types the reference date.
3. The controller asks `DeclarationRepository#getValidatedDeclarationsForAgentUpTo` for the matching declarations.
4. If the list is empty, the UI prints the AC3 message and returns.
5. Otherwise the UI iterates the declarations and prints each declaration's positions, subsidies, assets, and business participations using their respective `toString` representations.

### Acceptance criteria mapping

| AC | Where it is enforced |
|----|----------------------|
| AC1 — selected agent must be valid and registered | `displayAndSelectPoliticalAgent` only allows selection from `controller.getPoliticalAgents()`, which comes from the repository. |
| AC2 — only validated declarations up to the reference date | `DeclarationRepository#getValidatedDeclarationsForAgentUpTo` filters by `status == VALIDATED`, `agent.equals` and `submissionDate <= referenceDate`. |
| AC3 — empty result message | Handled in `ConsultIntegratedSituationUI#run` when the controller returns an empty list. |

## 6. Integration and Demo

- Hooked into the Ethics Committee menu the same way `SubmitComplaintUI` is hooked into the citizen menu (caller adds a new menu option pointing at `new ConsultIntegratedSituationUI().run()`).
- Demo plan: register an agent, submit two declarations on different dates, validate one of them, and run the consult flow with a date in between to show that only the validated, on-or-before declaration is presented.

## 7. Observations

- The integrated situation is presented as a chronological dump of validated declarations rather than as a single "merged" snapshot. The enunciado does not require deduplication of overlapping positions/assets across declarations, and the Ethics Committee member benefits from seeing each declaration individually.
- No new domain class was needed — the existing `Declaration` aggregate already exposes positions, subsidies, assets and business participations.
