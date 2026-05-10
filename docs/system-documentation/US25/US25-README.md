# US25 — Export Holdings Dataset to CSV

## Scope

As Product Owner, I want to draw information from Declarations of Interest and export it to a `.csv` file according to the Holdings Dataset (Table 3).

Output columns: `agent_id, company_NIF, total_value_in_stocks, company_percentage, declaration_date`

One row is produced per (validated declaration × business participation) pair.

## Dependencies

- **US24** (Bruno) — establishes the CSV export pattern; US25 follows the same structure for the holdings dataset instead of the declaration dataset.
- **US06** (Marcelo) — `Declaration` aggregate with `BusinessParticipation` entries.

## Implementation

| Layer      | Class                                                  |
|------------|--------------------------------------------------------|
| Domain     | `pt.ipp.isep.dei.domain.HoldingsCsvExporter`           |
| Controller | `pt.ipp.isep.dei.controller.ExportHoldingsCsvController` |
| UI         | `pt.ipp.isep.dei.ui.console.ExportHoldingsCsvUI`       |
| Menu       | `ProductOwnerUI` — "Export Holdings (CSV)"             |

## Tests

File: `src/test/java/pt/ipp/isep/dei/domain/HoldingsCsvExporterTest.java` (7 tests, all passing)

- `ensureEmptyListProducesHeaderOnly`
- `ensureDeclarationWithNoParticipationsProducesHeaderOnly`
- `ensureOneParticipationProducesOneDataRow`
- `ensureTwoParticipationsProduceTwoDataRows`
- `ensureMultipleDeclarationsProduceCorrectRowCount`
- `ensureExportReturnsTrueOnSuccess`
- `ensureAgentIdAndNifAreCorrectInRow`
