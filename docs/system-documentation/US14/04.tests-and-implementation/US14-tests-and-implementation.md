# US14 - Descriptive Statistical Analysis of Declarations

## 4. Tests

_To be elaborated later._


## 5. Construction (Implementation)

The full implementation lives in `src/main/python/us14/us14.py`.

Pieces in place:
- **Cell 2** reads `dataset1_declarations.csv` (exported by US24), keeps only the most recent declaration per agent (`drop_duplicates` on `agent_id`), and derives `total_income = gross_salary + side_income_consulting + side_income_board_memberships` and `total_assets = assets_in_real_estate + assets_in_vehicles + assets_in_stocks`.
- **Cell 3** computes measures of central tendency (mean, median, mode) for both variables.
- **Cell 4** computes non-central location quantiles — quartiles, deciles, and percentiles — using `statistics.quantiles`.
- **Cell 5** computes measures of variability: range, interquartile range, sample variance (ddof=1), sample standard deviation, and coefficient of variation.
- **Cell 6** computes sample skewness (`bias=False`) and real kurtosis (`fisher=False`) using `scipy.stats`, with automatic textual interpretation.
- **Cell 7** produces side-by-side histograms with mean and median overlaid, saved as `US14_histogramas.svg`.
- **Cell 8** produces side-by-side boxplots with Tukey-fence outlier detection, saved as `US14_boxplots.svg`.
- **Cell 9** prints a full summary DataFrame covering all measures for both variables.

Running the script against the demo `dataset1_declarations.csv` (one agent, one declaration) produces:

```
Número de agentes políticos (declaração mais recente): 1
=======================================================
  RENDIMENTO TOTAL
=======================================================
  Média    (x̄)  :          67,000.00 €
  Mediana  (x̃)  :          67,000.00 €
  Moda          :          67,000.00 €
```


## 6. Integration and Demo

- Consumes the canonical `dataset1_declarations.csv` produced by US24. The file must be in the working directory when the script is run.
- For demo purposes, the `dataset1_declarations.csv` bootstrapped by US24 (at least one validated declaration) is sufficient to run all cells end-to-end.
- Generated SVG files (`US14_histogramas.svg`, `US14_boxplots.svg`) are saved in the working directory.


## 7. Observations

- The original code had a bug on the `total_income` derivation — it referenced a non-existent column `side_income`. The correct derivation uses `side_income_consulting + side_income_board_memberships`, matching the schema defined by US24.
- `statistics.quantiles` requires at least 2 data points; the script will raise a `StatisticsError` if the dataset has fewer than 2 agents.
