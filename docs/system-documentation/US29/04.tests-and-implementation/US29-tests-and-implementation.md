# US29 - Detect Unusual Wealth Accumulation

## 4. Tests

- `src/test/python/us29/test_wealth_outliers.py` — 10 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines)

| Test | Description |
|------|-------------|
| `test_keeps_latest_per_agent` | keep_most_recent returns only the most recent declaration for each agent |
| `test_single_declaration_kept` | keep_most_recent does not drop agents with a single declaration |
| `test_income_and_assets_sum` | compute_totals adds salary + side incomes and real estate + vehicles + stocks |
| `test_perfect_linear` | fit_regression returns slope=2, intercept=0, r=1 for a perfect positive linear relation |
| `test_inverse_relation` | fit_regression returns negative slope and r=-1 for a perfect inverse linear relation |
| `test_residual_is_actual_minus_predicted` | compute_residuals computes residual = total_assets − (slope * total_income + intercept) |
| `test_returns_top_by_absolute` | top_n_deviations sorts by absolute residual and returns the most extreme N first |
| `test_n_larger_than_df` | top_n_deviations returns all rows when n exceeds the dataset size |
| `test_extreme_values_flagged` | detect_outliers flags entries whose z-score exceeds the threshold |
| `test_no_outliers_when_uniform` | detect_outliers returns no rows when residuals are uniform (std = 0 → NaN z-scores → no match) |

Run tests with:

```
python src/test/python/us29/test_wealth_outliers.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us29/wealth_outliers.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the declarations CSV |
| `keep_most_recent(df)` | Sorts by `declaration_date` and keeps the most recent row per `agent_id` (US29 requires the most recent declaration only) |
| `compute_totals(df)` | Adds `total_income` (gross_salary + side_income_consulting + side_income_board_memberships) and `total_assets` (assets_in_real_estate + assets_in_vehicles + assets_in_stocks) |
| `fit_regression(df)` | Fits a simple linear regression `total_assets ~ total_income` with `scipy.stats.linregress`; returns slope, intercept, and Pearson r |
| `compute_residuals(df, slope, intercept)` | Adds `predicted_assets` and `residual = total_assets − predicted_assets` |
| `top_n_deviations(df, n=10)` | Returns the N rows with the largest absolute residual (sorted descending) |
| `detect_outliers(df, threshold=2.0)` | Computes z-score over residuals and returns rows with `\|z\| > threshold` |
| `print_top_deviations(df_top)` | Prints the top deviations table to stdout |
| `print_outliers(df_out)` | Prints the outliers table to stdout |
| `plot_regression(df, df_top, slope, intercept, output_path)` | Saves a scatter of (income, assets) with the regression line and the top 10 deviations highlighted in red, as SVG |

### Output Chart (SVG)

- `docs/system-documentation/US29/US29_regression.svg` — scatter of total income vs total assets, with regression line and the top 10 deviations highlighted


## 6. Integration and Demo

- Consumes `dataset1_declarations.csv` (produced by US25). The CSV path is hardcoded to `dataset1_declarations.csv`.
- Run directly with:
  ```
  py src/main/python/us29/wealth_outliers.py
  ```
- Generated SVG is saved to `docs/system-documentation/US29/`.


## 7. Observations

- US29 requires the most recent declaration per agent; `keep_most_recent` performs sort-then-drop_duplicates(keep='last').
- The regression model is `total_assets ~ total_income`; the residuals (`actual − predicted`) are used to rank agents by deviation from the expected pattern.
- Outliers are detected via z-score on the residuals with a default threshold of |z| > 2. The threshold is a parameter so that a stricter |z| > 3 cut-off can also be reported.
- A weak Pearson r in the dataset is expected when income and assets do not have a clear linear relation; the residuals are still informative as an "unexpected wealth" signal.


## 8. Checklist

- [x] `load_data` — reads CSV
- [x] `keep_most_recent` — most recent declaration per agent
- [x] `compute_totals` — total income and total assets
- [x] `fit_regression` — simple linear regression with scipy.stats.linregress
- [x] `compute_residuals` — actual − predicted
- [x] `top_n_deviations` — top N by absolute residual
- [x] `detect_outliers` — z-score on residuals
- [x] `print_top_deviations`, `print_outliers` — ranked tables to stdout
- [x] `plot_regression` — scatter + regression line + top 10 highlighted → SVG
- [x] 10 unit tests (all passing)
- [x] SVG saved to `docs/system-documentation/US29/`
