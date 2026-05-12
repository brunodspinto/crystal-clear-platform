# US17 - Identify Top Companies by Share Values

## 4. Tests

- `src/test/python/us17/test_top_companies.py` — 7 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines)

| Test | Description |
|------|-------------|
| `test_single_company_sums_correctly` | Single company with multiple agents sums total_value_in_stocks correctly |
| `test_multiple_companies_aggregated` | Multiple companies aggregated independently and sorted descending |
| `test_sorted_descending` | Result of aggregate_by_company is always sorted highest-first |
| `test_returns_n_rows` | top_n returns exactly N rows |
| `test_returns_highest_values` | top_n returns the companies with the highest values |
| `test_deduplication_keeps_most_recent` | load_data keeps only the most recent row per (agent, company) pair |
| `test_all_unique_pairs_kept` | load_data returns the correct number of unique (agent, company) pairs |

Run tests with:

```
python src/test/python/us17/test_top_companies.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us17/top_companies.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the holdings CSV, keeps the most recent declaration per (agent, company) pair |
| `aggregate_by_company(df)` | Sums `total_value_in_stocks` by `company_NIF`, returns sorted DataFrame (descending) |
| `top_n(df_agg, n=10)` | Returns the top N companies by total share value |
| `print_top_companies(df_top)` | Prints a ranked table to stdout |
| `plot_top_companies(df_top, output_path)` | Saves a horizontal bar chart as SVG using `sns.barplot` (slide 11) |

### Test Data

- `src/test/resources/us17/sample_holdings.csv` — 8 rows across 5 agents and 5 companies; agent A003 has two entries for company C001 (2023 and 2024) to verify deduplication

After `load_data`, 7 unique (agent, company) pairs remain. After `aggregate_by_company`:

| Company NIF | Total Value (€) |
|-------------|----------------|
| C004 | 500,000 |
| C001 | 330,000 |
| C003 | 300,000 |
| C002 | 200,000 |
| C005 | 120,000 |

### Output Chart (SVG)

- `docs/system-documentation/US17/US17_top_companies.svg`


## 6. Integration and Demo

- Consumes `dataset2_holdings.csv` (produced by US25). The CSV path is hardcoded to `dataset2_holdings.csv`.
- Run directly with:
  ```
  py src/main/python/us17/top_companies.py
  ```
- Generated SVG is saved to `docs/system-documentation/US17/`.


## 7. Observations

- "Most recent declaration" is interpreted per (agent, company) pair: for each unique combination of agent and company, only the row with the latest `declaration_date` is kept.
- `statistics.quantiles` is not used here; no minimum data-point constraint applies.
- I/O and plotting functions (`load_data`, `plot_top_companies`) are not covered by unit tests per project guidelines.


## 8. Checklist

- [x] `load_data` — reads CSV, deduplicates per (agent, company), most recent kept
- [x] `aggregate_by_company` — sums total share value per company, sorted descending
- [x] `top_n` — slices top N companies
- [x] `print_top_companies` — ranked table to stdout
- [x] `plot_top_companies` — horizontal bar chart → SVG
- [x] `sample_holdings.csv` — 5-agent/5-company test dataset with deduplication case
- [x] 7 unit tests (all passing)
- [x] SVG chart saved to `docs/system-documentation/US17/`
