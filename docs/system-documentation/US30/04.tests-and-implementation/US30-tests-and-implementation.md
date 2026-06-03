# US30 - Residual Analysis of the Income/Assets Regression

> *As a member of the Ethics Committee, I intend to evaluate the validity of the statistical results about the number of agents that exhibit the greatest deviation from the expected pattern, obtained by the expected relationship between total income sources (salary and extra income) and total declared assets (real estate, vehicles, and stocks). For this, I want to analyze the distribution of the model's deviations (residuals) to verify its compatibility with the assumptions of linear regression. I wish to obtain two graphs: one corresponding to the residuals in the original data and another relating to the residuals after data normalization. For each agent, consider the most recent declaration only.*

## Approach

The user story builds directly on the regression of **US29** (the expected relationship between total income and total assets). US30 checks whether the residuals of that model are compatible with the assumptions of linear regression and, in doing so, validates the outlier/deviation results of US29.

1. Keep only the **most recent declaration** of each agent.
2. Compute `total_income = gross_salary + side_income_consulting + side_income_board_memberships` and `total_assets = assets_in_real_estate + assets_in_vehicles + assets_in_stocks` (same definitions as US29).
3. Fit the least-squares line `total_assets ~ total_income` with `scipy.stats.linregress` and compute the **residuals** `e_i = y_i - (slope·x_i + intercept)` (Statistics ch. 6).
4. Repeat the fit on the **normalized data**, where each variable is standardized with the z-score (reduced variable `Z = (X − mean) / std`), and compute the residuals on that scale.
5. Plot **two histograms** — residuals on the original data and residuals on the normalized data — overlaid with the Normal curve of the same mean and standard deviation, and report the descriptive measures (mean, std, min, max).

### Alignment with the Statistics syllabus

- The **assumptions of linear regression** checked here are exactly those stated in ch. 6 (slide 15): the errors should be approximately **Normal**, **centred at zero** and with **constant variance**.
- The **residual** definition and the regression line (`stats.linregress`) are from ch. 6.
- The **distribution** of the residuals is assessed with a **histogram** and **descriptive measures** (mean, standard deviation, min, max) from ch. 2, and with the **Normal** reference curve from ch. 3.
- **Normalization** is the z-score / reduced variable `Z = (X − mean)/σ` (ch. 3–4).
- A formal **normality test** (e.g. Shapiro–Wilk / Kolmogorov–Smirnov) is **not** used, because such goodness-of-fit tests are **outside** the syllabus (ch. 5 covers hypothesis tests for means and proportions only). The assumption is therefore assessed **visually and descriptively**, as the course material supports.


## 4. Tests

- `src/test/python/us30/test_residual_analysis.py` — 16 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines).

| Test | Description |
|------|-------------|
| `test_one_row_per_agent` | `keep_most_recent` returns one declaration per agent |
| `test_keeps_latest` | `keep_most_recent` keeps each agent's most recent declaration |
| `test_total_income` | `compute_totals` sums gross salary and both side incomes |
| `test_total_assets` | `compute_totals` sums real estate, vehicles and stocks |
| `test_does_not_mutate_input` | `compute_totals` does not mutate the caller's DataFrame |
| `test_perfect_line_recovers_slope_and_intercept` | `fit_regression` recovers slope/intercept of a perfect line |
| `test_residual_is_observed_minus_predicted` | `compute_residuals` = y − (slope·x + intercept) |
| `test_perfect_fit_gives_zero_residuals` | residuals are ~0 when the fit is perfect |
| `test_mean_is_zero` | `standardize` produces mean 0 |
| `test_std_is_one` | `standardize` produces standard deviation 1 |
| `test_constant_series_does_not_divide_by_zero` | `standardize` handles a constant series safely |
| `test_original_residuals_sum_to_zero` | OLS residuals on the original data have mean 0 |
| `test_normalized_residuals_have_mean_zero` | residuals on normalized data have mean 0 |
| `test_one_residual_per_row` | one residual is produced per observation |
| `test_mean_and_std` | `describe_residuals` reports the correct mean and std |
| `test_min_and_max` | `describe_residuals` reports the correct min and max |

Run tests with:

```
python src/test/python/us30/test_residual_analysis.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us30/residual_analysis.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the declarations CSV into a DataFrame |
| `keep_most_recent(df)` | Keeps each agent's most recent declaration |
| `compute_totals(df)` | Adds `total_income` and `total_assets` (same definition as US29) |
| `fit_regression(x, y)` | Least-squares line via `scipy.stats.linregress`; returns `(slope, intercept)` |
| `compute_residuals(x, y, slope, intercept)` | Residuals `e_i = y_i − (slope·x_i + intercept)` |
| `standardize(series)` | Z-score normalization `(value − mean)/std` (constant-safe) |
| `residuals_original(df)` | Residuals of `total_assets ~ total_income` on the original data |
| `residuals_normalized(df)` | Residuals after z-score normalizing income and assets |
| `describe_residuals(residuals)` | Mean, std, min and max of the residuals (ch. 2) |
| `print_description(title, residuals)` | Prints the descriptive measures (stdout) |
| `plot_residuals_histogram(residuals, title, output_path)` | Saves the residual histogram with the Normal reference curve as SVG |

### Output charts (SVG)

- `docs/system-documentation/US30/US30_residuals_original.svg` — histogram of the residuals on the original data (euro scale).
- `docs/system-documentation/US30/US30_residuals_normalized.svg` — histogram of the residuals after z-score normalization (standardized scale).


## 6. Integration and Demo

- Consumes `dataset1_declarations.csv` (produced by US24). The CSV path is hardcoded to `dataset1_declarations.csv`.
- Run directly with:
  ```
  py src/main/python/us30/residual_analysis.py
  ```
- The generated SVGs are saved to `docs/system-documentation/US30/`.

### Sample run

```
RESIDUALS - ORIGINAL DATA
  mean  =           0.0000
  std   =      34,475.8566
  min   =     -57,706.4157
  max   =     182,974.3523

RESIDUALS - NORMALIZED DATA (z-score)
  mean  =           0.0000
  std   =           0.9997
  min   =          -1.6733
  max   =           5.3055
```

> **Consistency with US29.** US30 reuses exactly the same preprocessing and model as US29 (`keep_most_recent`, `compute_totals`, and the `total_assets ~ total_income` regression via `stats.linregress`). The residuals computed here are numerically identical to those of US29 (slope = 0.028296, intercept = 56 546.27), and the standard deviation uses the same sample convention (`pandas .std()`, ddof = 1) as US29's z-score, so the two analyses agree. US29 flags 101 agents with `|z| > 2`.


## 7. Observations

- The residual **mean is 0** in both cases, as expected from the least-squares method (ch. 6) — this part of the assumption holds.
- The residuals are **strongly right-skewed**: on the normalized scale the minimum is only −1.67 standard deviations while the maximum reaches **+5.31** standard deviations. The histogram therefore departs from the symmetric Normal reference curve, so the **Normality assumption of linear regression is not fully satisfied**.
- Normalization with the z-score is a **linear transformation**, so it does **not** change the *shape* of the distribution — it only rescales it (std becomes ≈ 1). The normalized histogram is the right scale to read deviations directly in standard-deviation units and to apply the ±2 rule used in US29.
- **Validity of the US29 results:** because the residuals have a heavy positive tail, the agents flagged by US29 as having the greatest deviation (residuals beyond +5 standard deviations) are genuine extreme cases and not an artefact of a symmetric model. At the same time, the lack of Normality means the exact *count* of agents obtained from a Normal-based ±2 threshold should be read with caution — the deviations are real, but the distributional assumption underlying a strict probabilistic interpretation is only approximate.


## 8. Checklist

- [x] `load_data` — reads the declarations CSV
- [x] `keep_most_recent` — most recent declaration per agent
- [x] `compute_totals` — total income and total assets (consistent with US29)
- [x] `fit_regression` — least-squares line via `stats.linregress` (ch. 6)
- [x] `compute_residuals` — `e_i = y_i − ŷ_i`
- [x] `standardize` — z-score normalization (reduced variable)
- [x] `residuals_original` / `residuals_normalized` — residuals on both scales
- [x] `describe_residuals` — descriptive measures (ch. 2)
- [x] `plot_residuals_histogram` — histogram + Normal reference curve → SVG
- [x] two graphs (original data + normalized data)
- [x] 16 unit tests (all passing)
- [x] SVG charts saved to `docs/system-documentation/US30/`
