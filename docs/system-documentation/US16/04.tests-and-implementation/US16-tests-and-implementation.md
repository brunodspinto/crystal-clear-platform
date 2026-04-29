# US16 - Comparison of Total Income Across Roles (Boxplots)

## 4. Tests

_To be elaborated later._


## 5. Construction (Implementation)

Initial Python skeleton lives in `src/main/python/us16/income_boxplot.py`.

Pieces in place:
- `_load(path)` reads a csv with columns `name, role, total_income` and groups income by role.
- `summarise(by_role)` computes per-role n, min, Q1, median, Q3, max, mean using `statistics`.
- `print_summary(stats)` prints a fixed-width table to stdout.
- `plot_boxplot` is declared but not implemented yet — needs matplotlib in the env (waiting on the python `requirements.txt`).

A small `sample_declarations.csv` with 16 rows across 4 roles is included so the script runs end-to-end without US24 input. Running the script today gives:

```
role                         n        min         q1     median         q3        max       mean
deputy                       4      71000      73750      88500     106250     110000      89500
mayor                        4      55000      55750      60000      68750      71000      61500
minister                     4     128000     131000     145000     161250     165000     145750
secretary_of_state           4      89000      91250     100500     109750     112000     100500
```

## 6. Integration and Demo

- Will consume the canonical csv produced by US24 once the schema is final.
- Demo plan: run `python3 -m us16.income_boxplot path/to/declarations.csv` and show the per-role table + boxplot side by side.
- For the sprint review the boxplot can be generated offline and pasted as an image — does not need to be live.


## 7. Observations

Blocked on the csv schema finalised by US24.
