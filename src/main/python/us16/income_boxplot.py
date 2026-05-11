"""
US16 - Comparison of Total Income Across Roles (Boxplots).

Reads a CSV with declarations and produces, per role:
- summary statistics (count, mean, std, min, Q1, median, Q3, max) using pandas
- a boxplot figure with one box per role, saved as SVG.

Follows the patterns covered in Estatistica (Capitulo 2):
- pandas DataFrame + groupby + describe / boxplot
- statistics.quantiles for quartiles
"""

import os
import statistics as st

import pandas as pd

import matplotlib
matplotlib.use("svg")
import matplotlib.pyplot as plt


DEFAULT_CSV = os.path.join(os.path.dirname(__file__), "sample_declarations.csv")
DEFAULT_OUTPUT = os.path.join(os.path.dirname(__file__),
                              "..", "..", "..", "..", "docs", "system-documentation", "US16",
                              "us16_boxplot.svg")


def load_data(path):
    df = pd.read_csv(path)
    df["total_income"] = pd.to_numeric(df["total_income"], errors="coerce")
    df = df.dropna(subset=["total_income"])
    return df


def summarise(df):
    grouped = df.groupby("role")
    return grouped["total_income"].describe()


def print_summary(stats):
    print(stats)


def quartis(values):
    return st.quantiles(values, n=4)


def plot_boxplot(df, output_path):
    grouped = df.groupby("role")
    grouped.boxplot(column=["total_income"], subplots=False)
    plt.title("Total Income Distribution by Role")
    plt.xlabel("Role")
    plt.ylabel("Total Income")
    plt.xticks(rotation=15)
    plt.grid(axis="y", linestyle="--", alpha=0.5)
    plt.tight_layout()
    plt.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close()
    print("Saved: " + output_path)


if __name__ == "__main__":
    df = load_data(DEFAULT_CSV)
    stats = summarise(df)
    print_summary(stats)

    out_path = os.path.normpath(DEFAULT_OUTPUT)
    plot_boxplot(df, out_path)
