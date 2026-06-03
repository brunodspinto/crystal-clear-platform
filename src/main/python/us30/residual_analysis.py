"""US30 - Residual analysis of the income -> assets regression.

This module evaluates the validity of the statistical results produced in US29
(the agents that deviate the most from the expected income/assets pattern). It
fits the linear regression between total income and total assets, computes the
residuals, and analyses their distribution to check compatibility with the
assumptions of linear regression (Statistics ch. 6, slide 15): the errors
should be approximately Normal, centred at zero, with constant variance.

Two histograms are produced -- one for the residuals on the original data and
one for the residuals after normalizing the data with the z-score
(reduced variable Z = (X - mean) / std) -- so the distribution can be inspected
on both the original and the standardized scale. Only the most recent
declaration of each agent is considered.

The regression line is obtained with ``scipy.stats.linregress`` and the
residual is e_i = y_i - (slope * x_i + intercept), as defined in Statistics
ch. 6 (least-squares method).
"""

import sys

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats


def load_data(path):
    """Loads the declarations dataset from a CSV file."""
    return pd.read_csv(path)


def keep_most_recent(df):
    """Keeps, for each agent, only their most recent declaration (by date)."""
    return df.sort_values('declaration_date').drop_duplicates(
        subset='agent_id', keep='last')


def compute_totals(df):
    """Adds total income and total assets columns (same definition as US29)."""
    df = df.copy()
    df['total_income'] = (df['gross_salary']
                          + df['side_income_consulting']
                          + df['side_income_board_memberships'])
    df['total_assets'] = (df['assets_in_real_estate']
                          + df['assets_in_vehicles']
                          + df['assets_in_stocks'])
    return df


def fit_regression(x, y):
    """Least-squares regression line via scipy.stats.linregress (ch. 6).

    Returns the pair (slope, intercept).
    """
    model = stats.linregress(x, y)
    return model.slope, model.intercept


def compute_residuals(x, y, slope, intercept):
    """Regression residuals e_i = y_i - (slope * x_i + intercept) (ch. 6)."""
    predicted = slope * x + intercept
    return y - predicted


def standardize(series):
    """Z-score normalization: (value - mean) / std (reduced variable Z).

    Uses the sample standard deviation (pandas default, ddof=1), the same
    convention used by US29. If the series is constant it returns the centred
    series (all zeros) to avoid a division by zero.
    """
    std = series.std()
    if std == 0:
        return series - series.mean()
    return (series - series.mean()) / std


def residuals_original(df):
    """Residuals of the regression total_assets ~ total_income (original data)."""
    slope, intercept = fit_regression(df['total_income'], df['total_assets'])
    return compute_residuals(df['total_income'], df['total_assets'],
                             slope, intercept)


def residuals_normalized(df):
    """Residuals after z-score normalizing total_income and total_assets."""
    z_income = standardize(df['total_income'])
    z_assets = standardize(df['total_assets'])
    slope, intercept = fit_regression(z_income, z_assets)
    return compute_residuals(z_income, z_assets, slope, intercept)


def describe_residuals(residuals):
    """Descriptive measures of the residuals (ch. 2): mean, std, min, max."""
    return {
        'mean': residuals.mean(),
        'std': residuals.std(),
        'min': residuals.min(),
        'max': residuals.max(),
    }


def count_beyond_sigma(residuals, k=2.0):
    """Number of residuals whose standardized value exceeds +/- k (|z| > k).

    These are the agents with the greatest deviation (the ones US29 flags).
    """
    mean = residuals.mean()
    std = residuals.std()
    if std == 0:
        return 0
    z = (residuals - mean) / std
    return int((z.abs() > k).sum())


def normal_expected_count(n, k=2.0):
    """Number of observations expected beyond +/- k standard deviations under
    a Normal distribution (ch. 3): n * 2 * (1 - Phi(k))."""
    fraction = 2.0 * (1.0 - stats.norm.cdf(k))
    return n * fraction


def print_description(title, residuals):
    """Prints the descriptive measures of a residual series (stdout)."""
    print(title)
    description = describe_residuals(residuals)
    for label, value in description.items():
        print(f'  {label:<5} = {value:>16,.4f}')
    print()


def plot_residuals_histogram(residuals, title, output_path):
    """Saves a histogram of the residuals with an overlaid Normal curve.

    The histogram (ch. 2) shows the distribution of the residuals; the Normal
    density with the same mean and standard deviation (ch. 3) is drawn as a
    visual reference for the Normality assumption of linear regression (ch. 6).
    """
    mean = residuals.mean()
    std = residuals.std()

    plt.figure(figsize=(9, 6))
    plt.hist(residuals, bins=30, density=True, color='steelblue',
             edgecolor='black', alpha=0.7, label='Residuals')
    if std > 0:
        x = np.linspace(residuals.min(), residuals.max(), 200)
        plt.plot(x, stats.norm.pdf(x, mean, std), 'r-',
                 label='Normal (same mean and std)')
    plt.axvline(mean, color='black', linestyle='--', linewidth=1,
                label=f'mean = {mean:.2f}')
    plt.title(title)
    plt.xlabel('Residual')
    plt.ylabel('Density')
    plt.legend()
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    df = load_data('dataset1_declarations.csv')
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    df = keep_most_recent(df)
    df = compute_totals(df)

    res_original = residuals_original(df)
    res_normalized = residuals_normalized(df)

    print_description('RESIDUALS - ORIGINAL DATA', res_original)
    print_description('RESIDUALS - NORMALIZED DATA (z-score)', res_normalized)

    # Validity of the "number of agents with greatest deviation" (US29):
    # compare the observed counts beyond +/- k sigma with the Normal expectation.
    n = len(res_original)
    print('AGENTS WITH GREATEST DEVIATION (validity check vs Normal)')
    for k in (2.0, 3.0):
        observed = count_beyond_sigma(res_original, k)
        expected = normal_expected_count(n, k)
        print(f'  |z| > {k:.0f}: observed = {observed:4d} ({observed / n:6.2%})  '
              f'Normal-expected = {expected:6.1f} ({expected / n:6.2%})')
    print()

    plot_residuals_histogram(
        res_original, 'US30 - Residuals (original data)',
        'docs/system-documentation/US30/US30_residuals_original.svg')
    plot_residuals_histogram(
        res_normalized, 'US30 - Residuals (normalized data)',
        'docs/system-documentation/US30/US30_residuals_normalized.svg')
