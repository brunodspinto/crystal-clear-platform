# US31 - Non-linear Pattern: Shareholding Percentage vs Stock Value

> *As a member of the Ethics Committee, I want to investigate whether there are non-linear patterns in financial behavior ... I would like to know if the relationship between the percentage of shareholding in companies and the total declared value of the shares follows a constant pattern or if it shows curvature or acceleration at higher levels of ownership. For each agent/company, only consider the most recent declaration.*

## Approach

The relationship under study is between `company_percentage` (x) and `total_value_in_stocks` (y) in the holdings dataset, keeping only the **most recent declaration of each agent/company pair**.

The syllabus only covers **simple linear regression** (Statistics ch. 6), so non-linearity is investigated with the tools available in the course — **no polynomial / non-linear model is fitted**:

1. Fit the least-squares **linear** line `value ~ percentage` and report its coefficient of determination **r²** (ch. 6). A linear model that explains little of the variance is the first hint that the simple linear pattern is weak.
2. Inspect the **residuals** of the linear fit against the ownership percentage (ch. 6). A random cloud around zero supports the linear model; a **systematic curve** in the residuals would signal a non-linear relationship.
3. Run a **segmented analysis** by ownership level: the percentage axis is split into equal-width ranges and, inside each range, the **mean stock value** and the **linear slope** are computed (ch. 6 applied per segment, ch. 2 descriptive measures). If the slope/mean **grows at higher ownership levels**, the relationship accelerates; if it stays roughly constant, it is linear.

### Alignment with the Statistics syllabus

- Linear regression, `stats.linregress`, residuals and the coefficient of determination r² are from **ch. 6**.
- Mean values and the scatter / bar charts are **ch. 2** (descriptive statistics).
- A **polynomial regression** (quadratic fit) or a formal curvature test are **not** used, because they are outside the syllabus. Non-linearity is assessed through the **inadequacy of the linear model** (low r², residual pattern) and the **segmented comparison**, all of which only use ch. 2 and ch. 6.


## 4. Tests

- `src/test/python/us31/test_shareholding_nonlinearity.py` — 15 unit tests for the pure functions (I/O and plotting functions are excluded per project guidelines).

| Test | Description |
|------|-------------|
| `test_one_row_per_agent_company` | `keep_most_recent` returns one row per (agent, company) pair |
| `test_keeps_latest_for_pair` | `keep_most_recent` keeps the most recent declaration of each pair |
| `test_perfect_line` | `fit_linear` recovers slope, intercept and r = 1 for a perfect line |
| `test_perfect` / `test_half` / `test_negative_r_gives_positive_r2` | `r_squared` returns r² (always ≥ 0) |
| `test_residual_is_observed_minus_predicted` | `compute_residuals` = y − (slope·x + intercept) |
| `test_two_segments_split_in_half` | `segment_statistics` splits an evenly-spread range into equal-width segments |
| `test_segment_mean_values` | per-segment mean value is correct |
| `test_segment_slope_matches_perfect_line` | per-segment slope matches the line inside the segment |
| `test_increasing` | `acceleration_verdict` detects a rising mean-value trend |
| `test_decreasing` | `acceleration_verdict` detects a falling trend |
| `test_no_monotonic_trend` | `acceleration_verdict` reports a non-monotonic trend |
| `test_ignores_nan` | `acceleration_verdict` ignores NaN entries |
| `test_indeterminate_with_one_value` | `acceleration_verdict` returns 'indeterminate' with a single value |

Run tests with:

```
python src/test/python/us31/test_shareholding_nonlinearity.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us31/shareholding_nonlinearity.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the holdings CSV into a DataFrame |
| `keep_most_recent(df)` | Most recent declaration of each agent/company pair |
| `fit_linear(x, y)` | Least-squares line via `stats.linregress`; returns `(slope, intercept, r)` |
| `r_squared(r)` | Coefficient of determination r² (ch. 6) |
| `compute_residuals(x, y, slope, intercept)` | Residuals `e_i = y_i − (slope·x_i + intercept)` |
| `segment_statistics(df, n_segments=5)` | Per ownership-level: percentage range, n, mean value and linear slope |
| `acceleration_verdict(mean_values)` | Classifies the per-segment mean trend: increasing / decreasing / no monotonic trend |
| `print_linear_fit(slope, intercept, r)` | Prints the linear fit and r² (stdout) |
| `print_segments(segment_stats)` | Prints the per-ownership-level table and the trend verdict (stdout) |
| `plot_scatter_with_fit(df, slope, intercept, output_path)` | Scatter of percentage vs value with the fitted line → SVG |
| `plot_residuals(df, slope, intercept, output_path)` | Residuals vs percentage → SVG |
| `plot_mean_by_ownership(segment_stats, output_path)` | Bar chart of mean value per ownership level → SVG |

### Output charts (SVG)

- `docs/system-documentation/US31/US31_scatter_fit.svg` — scatter with the linear fit (overall linearity).
- `docs/system-documentation/US31/US31_residuals.svg` — residuals vs ownership percentage (systematic pattern check).
- `docs/system-documentation/US31/US31_mean_by_ownership.svg` — mean stock value per ownership level (trend at higher ownership).


## 6. Integration and Demo

- Consumes `dataset2_holdings.csv` (produced by US25). The CSV path is hardcoded to `dataset2_holdings.csv`.
- Run directly with:
  ```
  py src/main/python/us31/shareholding_nonlinearity.py
  ```
- The generated SVGs are saved to `docs/system-documentation/US31/`.

### Sample run

```
LINEAR FIT (value ~ percentage)
  line : value = 1768.46 * percentage + 110491.36
  r    = 0.1434
  r^2  = 0.0206

STATISTICS BY OWNERSHIP LEVEL (equal-width segments)
  [ 0.77%,  5.60%]  n=  56  mean_value=    113,365.89  slope=      7,569.30
  [ 5.60%, 10.43%]  n=  60  mean_value=    138,712.89  slope=     -7,070.74
  [10.43%, 15.26%]  n=  48  mean_value=    115,657.53  slope=      7,095.73
  [15.26%, 20.09%]  n=  45  mean_value=    145,573.56  slope=       -938.49
  [20.09%, 24.92%]  n=  58  mean_value=    150,855.32  slope=     11,857.36
  -> mean-value trend with ownership level: no monotonic trend
```


## 7. Observations (conclusion)

- The **linear fit is very weak**: r² = 0.02, i.e. the shareholding percentage explains only about **2 %** of the variability of the stock value. The percentage is, by itself, a poor linear predictor of the declared value.
- The **residuals** form a wide cloud around zero with **no systematic curve** — so there is no evidence of a specific non-linear (e.g. quadratic / accelerating) functional form.
- The **mean stock value by ownership level is non-monotonic** (≈ 113 k → 139 k → 116 k → 146 k → 151 k). The two **highest** ownership levels do show the **highest** mean values (≈ 146 k–151 k versus ≈ 113 k at the lowest level), but the trend is not monotonic and the within-segment slopes alternate sign, so the rise is weak and dominated by dispersion.
- **Answer to the user story:** the relationship does **not** follow a clear constant/linear pattern (very low r²), and it also does **not** show a clean curvature or acceleration at higher levels of ownership. There is only a **mild tendency** for higher declared value at the highest ownership levels, which is not strong enough to conclude a disproportionate (accelerating) growth of financial exposure. Within the course material (linear regression + residuals + segmented analysis) this is the strongest statement the data supports.


## 8. Checklist

- [x] `load_data` — reads the holdings CSV
- [x] `keep_most_recent` — most recent declaration per agent/company pair
- [x] `fit_linear` + `r_squared` — linear fit and coefficient of determination (ch. 6)
- [x] `compute_residuals` — residuals of the linear fit
- [x] `segment_statistics` — mean value and slope per ownership level
- [x] `acceleration_verdict` — trend classification (constant vs accelerating)
- [x] `plot_scatter_with_fit` / `plot_residuals` / `plot_mean_by_ownership` → SVG
- [x] 15 unit tests (all passing)
- [x] SVG charts saved to `docs/system-documentation/US31/`
