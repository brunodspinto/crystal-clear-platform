"""
US13 - Exploratory Analysis of Declarations by Type, Role, and Institution.

Reads the declarations CSV and produces:
1. Pie chart: distribution of declarations by type (SVG)
2. Bar chart: total declarations by role and by institution (SVG)

Uses the Python libraries taught in ESTAT Chapter 2:
- pandas: data loading and frequency tables
- seaborn: bar plot
- matplotlib: pie chart and figure handling
"""

import os

import pandas as pd

DEFAULT_CSV = os.path.join(os.path.dirname(__file__),
                           "..", "..", "..", "test", "resources", "us13", "sample_declarations.csv")

OUTPUT_DIR = os.path.join(os.path.dirname(__file__),
                          "..", "..", "..", "..", "docs", "system-documentation", "US13")

NUMERIC_FIELDS = ["gross_salary", "side_income",
                  "assets_in_real_estate", "assets_in_vehicles", "assets_in_stocks"]

STRING_FIELDS = ["declaration_id", "agent_id", "role",
                 "institution", "declaration_type", "declaration_date"]


def load_data(path):
    """
    Reads the declarations CSV using pandas and returns a list of dicts,
    one per row, preserving the public contract used by the test suite.
    Invalid numeric values default to 0.0.
    """
    df = pd.read_csv(path, dtype=str, keep_default_na=False)

    for field in STRING_FIELDS:
        if field in df.columns:
            df[field] = df[field].astype(str).str.strip()

    for field in NUMERIC_FIELDS:
        if field in df.columns:
            df[field] = pd.to_numeric(df[field], errors="coerce").fillna(0.0)
        else:
            df[field] = 0.0

    return df.to_dict(orient="records")


def count_by_field(declarations, field):
    """
    Returns a dict {value: count} for the given field across all declarations.
    Equivalent to pandas value_counts but kept as a dict to preserve the
    public API used by the test suite.
    """
    if not declarations:
        return {}
    series = pd.Series([decl[field] for decl in declarations])
    return series.value_counts().to_dict()


def print_counts(label, counts):
    print(label)
    for key in sorted(counts.keys()):
        print("  " + str(key) + ": " + str(counts[key]))


def plot_type_distribution(declarations, output_path):
    """
    Saves a pie chart of declaration type proportions as SVG
    (ESTAT Chapter 2, slide 12: gráfico circular with matplotlib).
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    import seaborn as sns

    df = pd.DataFrame(declarations)
    counts = df["declaration_type"].value_counts().sort_index()

    fig, ax = plt.subplots(figsize=(6, 6))
    ax.pie(counts.values,
           labels=counts.index,
           autopct="%1.1f%%",
           startangle=90,
           shadow=True,
           colors=sns.color_palette("muted"))
    ax.axis("equal")
    ax.set_title("Distribution of Declarations by Type")

    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


def plot_by_role_and_institution(declarations, output_path):
    """
    Saves a bar chart with two subplots (by role, by institution) as SVG
    (ESTAT Chapter 2, slide 11: gráfico de barras with seaborn).
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt
    import seaborn as sns

    df = pd.DataFrame(declarations)

    role_counts = df["role"].value_counts().sort_index()
    inst_counts = df["institution"].value_counts().sort_index()

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))

    sns.barplot(x=role_counts.index, y=role_counts.values,
                color="steelblue", ax=ax1)
    ax1.set_title("Total Declarations by Role")
    ax1.set_xlabel("Role")
    ax1.set_ylabel("Number of Declarations")
    ax1.tick_params(axis="x", rotation=30)

    sns.barplot(x=inst_counts.index, y=inst_counts.values,
                color="darkorange", ax=ax2)
    ax2.set_title("Total Declarations by Institution")
    ax2.set_xlabel("Institution")
    ax2.set_ylabel("Number of Declarations")
    ax2.tick_params(axis="x", rotation=30)

    fig.tight_layout()
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


if __name__ == "__main__":
    data = load_data(DEFAULT_CSV)
    print("Total declarations loaded: " + str(len(data)))
    print()
    print_counts("By declaration type:", count_by_field(data, "declaration_type"))
    print()
    print_counts("By role:", count_by_field(data, "role"))
    print()
    print_counts("By institution:", count_by_field(data, "institution"))
    print()

    out_dir = os.path.normpath(OUTPUT_DIR)
    plot_type_distribution(data, os.path.join(out_dir, "us13_chart1_type_distribution.svg"))
    plot_by_role_and_institution(data, os.path.join(out_dir, "us13_chart2_role_institution.svg"))
