# US28 - Remuneration vs Asset Correlation by Role

> *As a member of the Ethics Committee, I want to understand if the total declared remuneration of political actors is aligned with their total declared assets. For a given role, I want to know with which type of asset (real estate, vehicles or stocks) the total declared remuneration (gross salary and total side income) has the strongest correlation, comparing the first declarations with the last declarations. (Pearson's correlation coefficient).*

## Approach

For a given **role**, the analysis is performed twice — once using each agent's **first** declaration and once using each agent's **last** declaration — so the two snapshots can be compared:

1. Filter the declarations dataset by the requested role.
2. Compute `total_remuneration = gross_salary + side_income_consulting + side_income_board_memberships`.
3. Reduce to one row per agent (earliest for *first*, most recent for *last*).
4. Compute **Pearson's r** between `total_remuneration` and each asset type
   (`assets_in_real_estate`, `assets_in_vehicles`, `assets_in_stocks`).
5. Identify the asset type with the strongest correlation (largest absolute *r*).
6. Compare the *first* vs *last* coefficients (delta per asset type) to assess whether the alignment changed over time.

> The preprocessing (most-recent-per-agent and the income/asset totals) is kept consistent with **US29** so that both analyses operate on the same derived variables.


## 4. Tests

- `src/test/python/us28/test_role_asset_correlation.py` — 19 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines).

| Test | Description |
|------|-------------|
| `test_keeps_only_matching_role` | `filter_by_role` keeps only rows whose role matches |
| `test_no_match_returns_empty` | `filter_by_role` returns an empty frame when no row matches |
| `test_sums_salary_and_side_incomes` | `total_remuneration` = gross salary + both side incomes |
| `test_does_not_mutate_input` | `compute_total_remuneration` does not mutate the caller's DataFrame |
| `test_first_picks_earliest_per_agent` | `first_declarations` keeps each agent's earliest declaration |
| `test_last_picks_latest_per_agent` | `last_declarations` keeps each agent's most recent declaration |
| `test_one_row_per_agent` | first/last reduce to exactly one row per agent |
| `test_perfect_positive_correlation` | `pearson_coefficient` returns +1.0 for a perfect positive relation |
| `test_perfect_negative_correlation` | `pearson_coefficient` returns −1.0 for a perfect negative relation |
| `test_constant_series_returns_nan` | `pearson_coefficient` returns NaN when a series is constant (undefined) |
| `test_single_point_returns_nan` | `pearson_coefficient` returns NaN with fewer than two points |
| `test_returns_all_asset_labels` | `pearson_correlations` returns one entry per asset type |
| `test_stocks_perfectly_correlated` | `pearson_correlations` detects a perfect correlation on stocks |
| `test_strongest_is_stocks` | `strongest_correlation` picks the asset with the highest \|r\| |
| `test_strongest_uses_absolute_value` | `strongest_correlation` ranks by magnitude (a strong negative beats a weak positive) |
| `test_strongest_ignores_nan` | `strongest_correlation` skips NaN coefficients |
| `test_strongest_all_nan_returns_none` | `strongest_correlation` returns `(None, NaN)` when nothing is comparable |
| `test_delta_is_last_minus_first` | `compare_correlations` computes delta = r_last − r_first |
| `test_tuple_holds_first_and_last` | `compare_correlations` keeps both first and last coefficients |

Run tests with:

```
python src/test/python/us28/test_role_asset_correlation.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us28/role_asset_correlation.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the declarations CSV into a DataFrame |
| `filter_by_role(df, role)` | Keeps only the declarations of the requested role |
| `compute_total_remuneration(df)` | Adds `total_remuneration` = `gross_salary` + `side_income_consulting` + `side_income_board_memberships` (does not mutate the input) |
| `first_declarations(df)` | Keeps each agent's earliest declaration (`sort_values('declaration_date')` + `drop_duplicates(agent_id, keep='first')`) |
| `last_declarations(df)` | Keeps each agent's most recent declaration (`keep='last'`) |
| `pearson_coefficient(x, y)` | Pearson's r via `scipy.stats.pearsonr`; returns NaN when undefined (fewer than 2 points or a constant series) |
| `pearson_correlations(df)` | Returns a dict mapping each asset label to its correlation with `total_remuneration` |
| `strongest_correlation(correlations)` | Returns the `(label, r)` with the largest \|r\|, ignoring NaN |
| `compare_correlations(corr_first, corr_last)` | Returns, per asset, `(r_first, r_last, delta)` with `delta = r_last − r_first` |
| `print_correlations(title, correlations)` | Prints each coefficient and highlights the strongest (stdout) |
| `print_comparison(comparison)` | Prints the first→last change per asset type (stdout) |
| `plot_comparison(corr_first, corr_last, role, output_path)` | Saves a grouped bar chart (first vs last) as SVG |

### Asset mapping

```python
ASSET_COLUMNS = {
    'real estate': 'assets_in_real_estate',
    'vehicles':    'assets_in_vehicles',
    'stocks':      'assets_in_stocks',
}
```

### Test fixtures

The unit tests build small in-memory DataFrames in `setUp` (no external resource file is required). Correlation tests use controlled data with a known outcome (e.g. `total_remuneration` perfectly tracking the *stocks* column) so the expected coefficient and the strongest asset are deterministic.

### Output chart (SVG)

- `docs/system-documentation/US28/US28_correlation.svg` — grouped bar chart of Pearson's *r* per asset type, comparing first vs last declarations for the selected role.


## 6. Integration and Demo

- Consumes `dataset1_declarations.csv` (produced by US24). The CSV path is hardcoded to `dataset1_declarations.csv`.
- The role is passed as a command-line argument (defaults to `MP`):
  ```
  py src/main/python/us28/role_asset_correlation.py MP
  ```
- Available roles in the dataset: `Judge`, `MP`, `Advisor`, `State Secretary`, `Minister`.
- The generated SVG is saved to `docs/system-documentation/US28/`.

### Sample run (role `MP`)

```
FIRST DECLARATIONS - role: MP
  real estate  r =   0.0470
  vehicles     r =   0.0658
  stocks       r =   0.0356
  -> strongest: vehicles (r = 0.0658)

LAST DECLARATIONS  - role: MP
  real estate  r =  -0.0234
  vehicles     r =   0.1235
  stocks       r =   0.0314
  -> strongest: vehicles (r = 0.1235)

CHANGE BETWEEN FIRST AND LAST DECLARATIONS
  real estate  first =   0.0470  last =  -0.0234  delta =  -0.0704
  vehicles     first =   0.0658  last =   0.1235  delta =  +0.0577
  stocks       first =   0.0356  last =   0.0314  delta =  -0.0042
```


## 7. Observations

- **Pearson's coefficient** is computed with `scipy.stats.pearsonr`, as required by the user story.
- The coefficient is **undefined** for fewer than two data points or for a constant series; in those cases `pearson_coefficient` returns NaN and `strongest_correlation` ignores it instead of raising.
- `strongest_correlation` ranks by **absolute** value, so a strong negative correlation correctly outranks a weak positive one.
- The dataset column names differ slightly from Table 2 of the assignment: side income is split into `side_income_consulting` and `side_income_board_memberships`, both summed into the total remuneration.
- For role `MP`, all correlations are weak (\|r\| < 0.13), indicating that, for this role, total remuneration is not strongly aligned with any single asset type — and the alignment shifts only marginally between first and last declarations.


## 8. Checklist

- [x] `load_data` — reads the declarations CSV
- [x] `filter_by_role` — restricts the analysis to a single role
- [x] `compute_total_remuneration` — gross salary + both side incomes
- [x] `first_declarations` / `last_declarations` — one row per agent (earliest / most recent)
- [x] `pearson_coefficient` — Pearson's r with NaN-safe guards
- [x] `pearson_correlations` — r per asset type vs total remuneration
- [x] `strongest_correlation` — strongest by \|r\|, NaN-aware
- [x] `compare_correlations` — first vs last delta per asset type
- [x] `plot_comparison` — grouped bar chart (first vs last) → SVG
- [x] 19 unit tests (all passing)
- [x] SVG chart saved to `docs/system-documentation/US28/`
