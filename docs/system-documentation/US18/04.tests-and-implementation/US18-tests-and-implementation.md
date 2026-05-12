# US18 - Identify Top Stock Value Increases

## 4. Tests

- `src/test/python/us18/test_stock_increases.py` — 8 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines)

| Test | Description |
|------|-------------|
| `test_increase_calculated_correctly` | Increase is computed as latest_value − initial_value for a given (agent, company) pair |
| `test_sorted_descending` | Result of compute_increases is always sorted highest increase first |
| `test_excludes_single_declaration` | Pairs with only one declaration are excluded (no increase can be computed) |
| `test_includes_negative_increase` | Pairs where the most recent value is lower than the initial are included (negative increase) |
| `test_returns_n_rows` | top_n_increases returns exactly N rows |
| `test_returns_highest_increases` | top_n_increases returns the pairs with the highest increases |
| `test_contains_all_dates_for_pair` | get_evolution returns all historical rows for a top pair (not just the first and last) |
| `test_excludes_other_pairs` | get_evolution does not include rows for pairs not in the top list |

Run tests with:

```
python src/test/python/us18/test_stock_increases.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us18/stock_increases.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the holdings CSV; all rows are kept (no deduplication — full history is required) |
| `compute_increases(df)` | For each (agent, company) pair, computes increase from earliest to most recent declaration; excludes pairs with fewer than 2 declarations; returns DataFrame sorted by increase descending |
| `top_n_increases(df_inc, n=10)` | Returns the top N rows of the increases DataFrame |
| `get_evolution(df, df_top)` | Returns all historical rows from `df` for the (agent, company) pairs in `df_top` using set-based boolean filtering |
| `print_top_increases(df_top)` | Prints a ranked table of the top pairs and their increases to stdout |
| `plot_top_increases(df_top, output_path)` | Saves a horizontal bar chart of the top 10 increases as SVG using `sns.barplot` (slide 11) |
| `plot_evolution(df_evolution, output_path)` | Saves boxplots of `total_value_in_stocks` grouped by `agent_id` showing value distribution using `df.groupby().boxplot()` (slide 39) |

### Test Data

- `src/test/resources/us18/sample_holdings.csv` — 12 rows across 4 agents and 5 companies

| Agent | Company | Declarations | Increase |
|-------|---------|-------------|---------|
| A001 | C001 | 3 (100k → 120k → 150k) | +50,000 |
| A001 | C002 | 2 (200k → 180k) | −20,000 |
| A002 | C001 | 2 (80k → 200k) | +120,000 |
| A002 | C003 | 1 (single, excluded) | — |
| A003 | C004 | 3 (50k → 70k → 100k) | +50,000 |
| A004 | C005 | 1 (single, excluded) | — |

After `compute_increases`: 4 rows (A002/C003 and A004/C005 excluded), sorted descending by increase.

### Output Charts (SVG)

- `docs/system-documentation/US18/US18_top_increases.svg` — bar chart of the top 10 (agent, company) pairs by stock value increase
- `docs/system-documentation/US18/US18_evolution.svg` — boxplots of `total_value_in_stocks` grouped by agent, showing distribution of values over time


## 6. Integration and Demo

- Consumes `dataset2_holdings.csv` (produced by US25). The CSV path is hardcoded to `dataset2_holdings.csv`.
- Run directly with:
  ```
  py src/main/python/us18/stock_increases.py
  ```
- Generated SVGs are saved to `docs/system-documentation/US18/`.


## 7. Observations

- Unlike US17, `load_data` does **not** deduplicate — the full declaration history is required to compute increases and plot evolution over time.
- A pair must have at least 2 declarations to appear in the results; single-declaration pairs are silently excluded.
- Negative increases (value decreased over time) are included in the results and sorted accordingly.
- `get_evolution` uses set-based boolean filtering with `df.values` to select rows matching the top pairs — avoids `df.merge()` which is not covered in the Statistics slides.


## 8. Checklist

- [x] `load_data` — reads CSV, keeps all rows (full history)
- [x] `compute_increases` — computes initial→latest increase per (agent, company) pair, sorted descending
- [x] `top_n_increases` — slices top N pairs
- [x] `get_evolution` — retrieves full history for top pairs
- [x] `print_top_increases` — ranked table to stdout
- [x] `plot_top_increases` — horizontal bar chart → SVG
- [x] `plot_evolution` — boxplots grouped by agent (`df.groupby().boxplot()`, slide 39) → SVG
- [x] `sample_holdings.csv` — 4-agent/5-company test dataset with single-declaration exclusion cases
- [x] 8 unit tests (all passing)
- [x] SVG charts saved to `docs/system-documentation/US18/`
