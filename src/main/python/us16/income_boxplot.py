import statistics as st
import pandas as pd
import matplotlib.pyplot as plt


def load_data(path):
    return pd.read_csv(path)


def summarise(df):
    return df.groupby("role")["total_income"].describe()


def quartis(values):
    return st.quantiles(values, n=4)


def plot_boxplot(df, output_path):
    df.groupby("role").boxplot()
    plt.tight_layout()
    plt.savefig(output_path)


if __name__ == "__main__":
    df = load_data("src/main/python/us16/sample_declarations.csv")
    print(summarise(df))
    plot_boxplot(df, "docs/system-documentation/US16/us16_boxplot.svg")
