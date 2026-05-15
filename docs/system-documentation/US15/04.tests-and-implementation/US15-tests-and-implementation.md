# US15 - Examine the Evolution of a Political Agent's Total Assets and Net Worth

## Implementation

### Classes

| Class | Path |
|-------|------|
| `AssetEvolutionController` | `src/main/java/pt/ipp/isep/dei/controller/AssetEvolutionController.java` |
| `AssetEvolutionUI` | `src/main/java/pt/ipp/isep/dei/ui/console/AssetEvolutionUI.java` |
| `Declaration` | `src/main/java/pt/ipp/isep/dei/domain/Declaration.java` |
| `PositionEntry` | `src/main/java/pt/ipp/isep/dei/domain/PositionEntry.java` |
| `AssetEntry` | `src/main/java/pt/ipp/isep/dei/domain/AssetEntry.java` |
| `AssetType` | `src/main/java/pt/ipp/isep/dei/domain/AssetType.java` |
| `us15_asset_evolution.py` | `src/main/python/us15/us15_asset_evolution.py` |

**`AssetEvolutionController`** exposes two methods consumed by the UI. `getPoliticalAgents()` delegates directly to `PoliticalAgentRepository.getAll()`. `getAllDeclarationsForAgent(PoliticalAgent)` filters the full declaration list by agent identity, sorts the results chronologically by submission date using `Collections.sort`, and returns all matches regardless of declaration type (initial, regular, exceptional) and status (pending, validated, rejected) — as required by the US specification.

**`AssetEvolutionUI`** iterates the returned list and for each declaration sums income and asset values by iterating `getPositionEntries()` and `getAssetEntries()` respectively. The textual summary table is printed to the console. The three graphical evolution charts (gross salary, side income by type, assets by category) are produced by the Python script `us15_asset_evolution.py`, which reads the CSV exported by US24.

**`us15_asset_evolution.py`** reads `agent_id`, `declaration_date`, `gross_salary`, `side_income_consulting`, `side_income_board_memberships`, `assets_in_real_estate`, `assets_in_vehicles`, and `assets_in_stocks` from the CSV. It produces three SVG line charts saved under `docs/system-documentation/US15/`.

### Key design decisions

- The controller sorts in memory after filtering rather than relying on repository insertion order, so the chronological guarantee holds regardless of submission order.
- All declaration statuses are included (not just `VALIDATED`) because US15 analyses the agent's own evolution — including declarations under review or returned for correction — giving the most complete picture.
- The graphical output is delegated to Python to leverage `matplotlib`, keeping the Java application free of charting dependencies.

---

## Tests

### `src/test/java/pt/ipp/isep/dei/controller/AssetEvolutionControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureGetPoliticalAgentsReturnsAll` | Two saved agents are both returned by `getPoliticalAgents()` |
| `ensureGetPoliticalAgentsReturnsEmptyWhenNone` | Empty repository returns an empty list |
| `ensureNullAgentThrows` | `getAllDeclarationsForAgent(null)` throws `IllegalArgumentException` |
| `ensureReturnsOnlyDeclarationsOfSelectedAgent` | Declarations of agent A2 are not included in the result for A1 |
| `ensureIncludesAllDeclarationTypes` | Initial, regular, and exceptional declarations are all returned |
| `ensureIncludesAllStatuses` | Both a validated and a pending declaration are returned for the same agent |
| `ensureDeclarationsAreReturnedChronologically` | Three declarations saved out of order are returned sorted by submission date |
| `ensureEmptyListReturnedWhenNoDeclarationsForAgent` | Agent with no declarations returns an empty list |
