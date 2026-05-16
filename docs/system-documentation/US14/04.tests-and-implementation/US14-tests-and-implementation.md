# US14 - Descriptive Statistical Analysis of Declarations

## 4. Tests

- `src/test/python/us14/test_declaration_stats.py` — 9 unit tests for the pure statistical functions (I/O and plotting functions are excluded per project guidelines)

| Test | Description |
|------|-------------|
| `test_mean` | Mean of a known series equals the expected value |
| `test_median` | Median of a known series equals the expected value |
| `test_mode` | Mode returns the most frequent value |
| `test_total_range` | Range equals max − min |
| `test_sample_variance` | Sample variance (ddof=1) equals the expected value |
| `test_quartile_q1` | Q1 matches `statistics.quantiles` output |
| `test_skewness_symmetric` | Skewness of a symmetric series is 0 |
| `test_skewness_right_skewed` | Skewness of a right-tailed series is positive |
| `test_skewness_left_skewed` | Skewness of a left-tailed series is negative |

Run tests with:

```
python src/test/python/us14/test_declaration_stats.py -v
```


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us14/declaration_stats.py`.

### Functions

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the declarations CSV, keeps the most recent declaration per agent, derives `total_income` and `total_assets` |
| `central_tendency(data)` | Returns `{mean, median, mode}` for a pandas Series |
| `compute_quantiles(data)` | Returns quartiles, key deciles and percentiles using `statistics.quantiles` |
| `variability(data)` | Returns range, IQR, sample variance, std dev, and coefficient of variation |
| `shape_stats(data)` | Returns sample skewness (`bias=False`) and real kurtosis (`fisher=False`) |
| `sturges_classes(n)` | Computes the number of classes using Sturges' rule (`c = Int[1 + 3.3·log₁₀(n)]`, slide 15) |
| `frequency_table(data)` | Builds an absolute/relative/cumulative frequency table over Sturges classes (slides 8, 13, 20) |
| `modal_class(data)` | Returns the modal class (class with the highest absolute frequency, slide 25) — the meaningful "mode" for continuous data |
| `print_central_tendency(label, ct, modal=None)` | Prints central tendency results; when `modal` is provided, prints the modal class instead of the raw `mode()` value |
| `print_quantiles(label, q)` | Prints quantile results to stdout |
| `print_variability(label, v)` | Prints variability results to stdout |
| `print_shape(label, s)` | Prints skewness and kurtosis with textual interpretation to stdout |
| `plot_histograms(df, output_path)` | Saves side-by-side histograms as SVG |
| `plot_boxplots(df, output_path)` | Saves side-by-side boxplots as SVG with Tukey-fence outlier detection |
| `full_summary(df)` | Returns a DataFrame with all statistical measures for both variables |

### Test Data

- `src/test/resources/us14/sample_declarations.csv` — 6 rows across 5 agents; agent A001 has two declarations (2024 and 2025), only the 2025 row is kept to verify the deduplication logic

After `load_data`, the 5 resulting agents have:

| agent_id | total_income | total_assets |
|----------|-------------|--------------|
| A001 | 60,000 | 130,000 |
| A002 | 80,000 | 250,000 |
| A003 | 70,000 | 190,000 |
| A004 | 110,000 | 380,000 |
| A005 | 110,000 | 315,000 |

### Output Charts (SVG)

- `docs/system-documentation/US14/US14_histograms.svg`
- `docs/system-documentation/US14/US14_boxplots.svg`


## 6. Integration and Demo

- Consumes the CSV exported by US24. The CSV path is hardcoded to `dataset1_declarations.csv` (the reference dataset supplied with the project).
- Run directly with:
  ```
  py src/main/python/us14/declaration_stats.py
  ```
- Generated SVG files are saved to `docs/system-documentation/US14/`.


## 7. Observations

- The original code had a bug on the `total_income` derivation — it referenced a non-existent column `side_income`. The correct derivation uses `side_income_consulting + side_income_board_memberships`, matching the schema defined by US24.
- `statistics.quantiles` requires at least 2 data points; the script will raise a `StatisticsError` if the dataset has fewer than 2 agents.
- I/O and plotting functions (`load_data`, `plot_histograms`, `plot_boxplots`) are not covered by unit tests per project guidelines.
- `total_income` and `total_assets` are continuous variables, so the slide-25 definition of mode (modal class on grouped data) is the appropriate one. The bare `pd.mode()[0]` is kept inside `central_tendency` for backwards compatibility with discrete test data, but the runtime output uses `modal_class` over Sturges' bins.


## 8. Checklist

- [x] `load_data` — reads CSV, deduplicates per agent, derives `total_income` and `total_assets`
- [x] `central_tendency` — mean, median, mode
- [x] `sturges_classes` / `frequency_table` / `modal_class` — frequency distribution and modal class for continuous data (slides 8, 13, 15, 20, 25)
- [x] `compute_quantiles` — quartiles, deciles, percentiles
- [x] `variability` — range, IQR, sample variance, std dev, CV
- [x] `shape_stats` — sample skewness and real kurtosis
- [x] `plot_histograms` — side-by-side histograms → SVG
- [x] `plot_boxplots` — side-by-side boxplots → SVG
- [x] `full_summary` — complete statistical DataFrame
- [x] `sample_declarations.csv` — 5-agent test dataset with deduplication case
- [x] 9 unit tests (all passing)
- [x] SVG charts saved to `docs/system-documentation/US14/`
