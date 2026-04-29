# US16 - Comparison of Total Income Across Roles (Boxplots)

## 4. Tests

_To be elaborated later._


## 5. Construction (Implementation)

Initial Python skeleton lives in `src/main/python/us16/income_boxplot.py`.

Pieces in place:
- `load_data(path)` reads a csv with columns `name, role, total_income` and groups income by role.
- `summarise(by_role)` computes per-role n, min, Q1, median, Q3, max, mean. Quartiles are approximated by index (Q1 = values[n/4], median = values[n/2], Q3 = values[3n/4-1]) — not exact but enough for now.
- `print_summary(stats)` prints a comma-separated line per role to stdout.
- `plot_boxplot` is declared but not implemented yet — needs matplotlib in the env (waiting on the python `requirements.txt`).

A small `sample_declarations.csv` with 16 rows across 4 roles is included so the script runs end-to-end without US24 input. Running the script today gives:

```
role, n, min, q1, median, q3, max, mean
deputy 4 71000.0 82000.0 95000.0 95000.0 110000.0 89500.0
mayor 4 55000.0 58000.0 62000.0 62000.0 71000.0 61500.0
minister 4 128000.0 140000.0 150000.0 150000.0 165000.0 145750.0
secretary_of_state 4 89000.0 98000.0 103000.0 103000.0 112000.0 100500.0
```

## 6. Integration and Demo

- Will consume the canonical csv produced by US24 once the schema is final.
- Demo plan: run `python3 -m us16.income_boxplot path/to/declarations.csv` and show the per-role table + boxplot side by side.
- For the sprint review the boxplot can be generated offline and pasted as an image — does not need to be live.


## 7. Observations

Blocked on the csv schema finalised by US24.
