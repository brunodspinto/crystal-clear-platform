# US13 - Tests and Implementation

## Implementation

### Python Script

- `src/main/python/us13/declaration_analysis.py` — reads the declarations CSV and produces two SVG charts

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the declarations CSV and returns a list of dicts, one per row; invalid numeric fields default to 0.0 |
| `count_by_field(declarations, field)` | Returns a `{value: count}` dict for any field across all declarations |
| `print_counts(label, counts)` | Prints a formatted count summary to stdout |
| `plot_type_distribution(declarations, output_path)` | Saves a pie chart of declaration type proportions as SVG |
| `plot_by_role_and_institution(declarations, output_path)` | Saves a bar chart with total declarations by role and by institution as SVG |

### Output Charts (SVG)

- `docs/system-documentation/US13/us13_chart1_type_distribution.svg` — pie chart: proportion of declarations by type (Initial / Regular / Exceptional)
- `docs/system-documentation/US13/us13_chart2_role_institution.svg` — bar charts: total declarations by role and by institution

### Test Data

- `src/test/resources/us13/sample_declarations.csv` — 16 sample declarations covering all types (Initial, Regular, Exceptional), roles (MP, Minister, Judge, State Secretary, Advisor), and institutions (Parliament, Government, Courts)

## Tests

- `src/test/python/us13/test_declaration_analysis.py` — 6 unit tests for `count_by_field` (pure function; I/O functions are excluded per project guidelines)

| Test | Description |
|------|-------------|
| `test_count_by_type` | Counts declarations correctly grouped by declaration_type |
| `test_count_by_role` | Counts declarations correctly grouped by role |
| `test_count_by_institution` | Counts declarations correctly grouped by institution |
| `test_empty_list` | Returns empty dict for an empty declarations list |
| `test_single_entry` | Returns count of 1 for a single-entry list |
| `test_all_same_value` | Returns correct count when all entries share the same value |

Run tests with:

```
python3 src/test/python/us13/test_declaration_analysis.py -v
```

## Checklist

- [x] `load_data` — reads declarations CSV
- [x] `count_by_field` — counts by any field
- [x] `plot_type_distribution` — pie chart by type → SVG
- [x] `plot_by_role_and_institution` — bar charts by role and institution → SVG
- [x] `sample_declarations.csv` — 16-row test dataset
- [x] 6 unit tests for `count_by_field` (all passing)
- [x] SVG charts generated in `docs/system-documentation/US13/`
