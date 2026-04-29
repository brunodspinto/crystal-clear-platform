"""
US16 - Comparison of Total Income Across Roles (Boxplots).

Reads a CSV with declarations and produces, per role:
- per-role summary stats (n, min, q1, median, q3, max, mean)
- (TODO) a boxplot figure

The csv schema is supposed to come from US24 (Integration).
For now we assume columns: name, role, total_income.
"""

import csv
import os

# default sample, replace with the real csv once US24 lands
DEFAULT_CSV = os.path.join(os.path.dirname(__file__), "sample_declarations.csv")


def load_data(path):
    by_role = {}
    f = open(path, "r")
    reader = csv.DictReader(f)
    for row in reader:
        role = row["role"]
        try:
            income = float(row["total_income"])
        except ValueError:
            # ignore bad rows for now
            continue
        if role not in by_role:
            by_role[role] = []
        by_role[role].append(income)
    f.close()
    return by_role


def summarise(by_role):
    out = {}
    for role in by_role:
        values = sorted(by_role[role])
        n = len(values)
        if n == 0:
            continue
        # quartiles by index — not exact like statistics.quantiles but ok for now
        q1 = values[n // 4]
        median = values[n // 2]
        q3 = values[(3 * n) // 4 - 1] if n >= 4 else values[-1]

        total = 0
        for v in values:
            total = total + v
        mean = total / n

        out[role] = {
            "n": n,
            "min": values[0],
            "q1": q1,
            "median": median,
            "q3": q3,
            "max": values[-1],
            "mean": mean,
        }
    return out


def print_summary(stats):
    print("role, n, min, q1, median, q3, max, mean")
    for role in sorted(stats.keys()):
        s = stats[role]
        print(role, s["n"], s["min"], s["q1"], s["median"], s["q3"], s["max"], s["mean"])


def plot_boxplot(by_role, output=None):
    # TODO: matplotlib import + boxplot. Holding off because the lab
    # machines still don't have matplotlib in the default env (André
    # is supposed to add a requirements.txt for the python side).
    raise NotImplementedError("plot_boxplot - pending requirements.txt")


if __name__ == "__main__":
    data = load_data(DEFAULT_CSV)
    stats = summarise(data)
    print_summary(stats)
