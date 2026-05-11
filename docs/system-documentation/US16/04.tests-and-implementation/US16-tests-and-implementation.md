# US16 - Tests and Implementation

## Implementation

### Python Script

- `src/main/python/us16/income_boxplot.py` - reads a declarations CSV into a pandas DataFrame, groups total income by role, and produces summary statistics and a boxplot SVG, following the patterns covered in Estatistica (Capitulo 2).

| Function | Description |
|----------|-------------|
| `load_data(path)` | Reads the CSV with `pd.read_csv` and returns a `DataFrame` with columns `name`, `role`, `total_income`; rows with non-numeric income are dropped via `pd.to_numeric(errors="coerce")` + `dropna` |
| `summarise(df)` | Returns `df.groupby("role")["total_income"].describe()` - a DataFrame with `count, mean, std, min, 25%, 50%, 75%, max` per role |
| `print_summary(stats)` | Prints the describe DataFrame to stdout |
| `quartis(values)` | Returns `Q1, Q2, Q3` for a list of values, using `statistics.quantiles(values, n=4)` (linear interpolation, the method covered in the slides) |
| `plot_boxplot(df, output_path)` | Saves a boxplot SVG using `df.groupby("role").boxplot(column=["total_income"])`; shows median, Q1-Q3, whiskers up to 1.5*IQR and outliers |

### Output Chart (SVG)

- `docs/system-documentation/US16/us16_boxplot.svg` - boxplot comparing total income across the four roles in the sample CSV.

### Test Data

- `src/main/python/us16/sample_declarations.csv` - 16 sample declarations covering four roles (deputy, mayor, minister, secretary_of_state) with four income values each. Kept small on purpose so the boxplot is readable and the script runs end-to-end without external data.

## Tests

- `src/test/python/us16/test_income_boxplot.py` - 10 unit tests covering `load_data`, `summarise` and `quartis`. `plot_boxplot` is excluded from unit testing because it produces a binary SVG artefact - it is exercised manually via the script's `__main__` block.

| Test | Description |
|------|-------------|
| `test_returns_dataframe` | `load_data` returns a `pd.DataFrame` with the expected number of rows |
| `test_drops_non_numeric_income` | rows with bad `total_income` are dropped via `to_numeric(errors="coerce")` + `dropna` |
| `test_keeps_expected_columns` | the loaded DataFrame keeps `name`, `role`, `total_income` |
| `test_returns_describe_columns` | `summarise` returns a DataFrame with the 8 describe columns |
| `test_groups_by_role` | `summarise` correctly groups rows by role and counts them |
| `test_mean_per_role` | mean formula on a small known input |
| `test_median_per_role` | median (the `50%` column) matches the expected value |
| `test_min_max_per_role` | min and max per role match the expected values |
| `test_returns_three_values` | `quartis` returns exactly three values (Q1, Q2, Q3) |
| `test_median_is_second_quartile` | the middle quartile equals the median for a known input |

Run tests with:

```
python3 -m unittest src.test.python.us16.test_income_boxplot
```

## Sample Output

Running the script on the bundled `sample_declarations.csv`:

```
                    count      mean           std  ...       50%       75%       max
role                                               ...
deputy                4.0   89500.0  16822.603841  ...   88500.0   98750.0  110000.0
mayor                 4.0   61500.0   6952.217872  ...   60000.0   64250.0   71000.0
minister              4.0  145750.0  15671.098664  ...  145000.0  153750.0  165000.0
secretary_of_state    4.0  100500.0   9609.023537  ...  100500.0  105250.0  112000.0

Saved: docs/system-documentation/US16/us16_boxplot.svg
```

The boxplot lets the journalist compare the four roles at a glance: ministers are clearly highest, mayors lowest, with deputies and secretaries of state in between.

## Quartile Method (Note)

`summarise` uses `pandas.DataFrame.describe`, which returns the quartiles via **linear interpolation** between adjacent sorted values - the same method as `statistics.quantiles(values, n=4)` covered in the Estatistica slides (Capitulo 2). On a sample of size 4 this matches the textbook values (e.g. for `[71000, 82000, 95000, 110000]`: Q1 = 79250, median = 88500, Q3 = 98750).

The helper `quartis(values)` is kept as a thin wrapper around `statistics.quantiles(values, n=4)` so the algorithm can be defended in isolation during the oral.

## Integration and Demo

- Demo plan: run `python3 -m src.main.python.us16.income_boxplot` from the project root. The script prints the per-role describe table and saves the boxplot SVG into `docs/system-documentation/US16/`.
- For the sprint review the boxplot is generated offline and shown side-by-side with the table.
- The CSV schema (`name, role, total_income`) is intentionally simple; if US24 finalises a richer canonical schema, `load_data` is the single place to adapt.

## Checklist

- [x] `load_data` - reads declarations CSV into a pandas DataFrame
- [x] `summarise` - per-role describe (count, mean, std, min, Q1, median, Q3, max)
- [x] `quartis` - Q1/Q2/Q3 via `statistics.quantiles`
- [x] `print_summary` - tabular stdout output
- [x] `plot_boxplot` - SVG figure with one box per role
- [x] `sample_declarations.csv` - 16-row test dataset
- [x] 10 unit tests for `load_data`, `summarise` and `quartis`
- [x] SVG chart generated in `docs/system-documentation/US16/`
- [x] `requirements.txt` lists matplotlib and pandas
