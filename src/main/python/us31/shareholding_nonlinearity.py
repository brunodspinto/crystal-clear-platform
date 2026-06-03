"""US31 - Non-linear pattern between shareholding percentage and stock value.

As a member of the Ethics Committee, this module investigates whether the
relationship between the *percentage of shareholding* in a company
(``company_percentage``) and the *total declared value of the shares*
(``total_value_in_stocks``) follows a constant (linear) pattern or whether it
shows curvature / acceleration at higher levels of ownership. Only the most
recent declaration of each agent/company pair is considered.

The course only covers *simple linear regression* (Statistics ch. 6), so
non-linearity is investigated with the tools available in the syllabus:

* a least-squares linear fit and its coefficient of determination r^2 (ch. 6);
* the **residuals** of that linear fit (ch. 6): a systematic pattern in the
  residuals signals that the linear model is inadequate (non-linearity);
* a **segmented analysis** by ownership level -- the mean value and the linear
  slope are computed inside equal-width ownership ranges (ch. 6 applied per
  segment, ch. 2 descriptive measures). If the slope/mean grows at higher
  ownership levels, the relationship accelerates; if it stays roughly constant,
  the relationship is linear.

No polynomial / non-linear model is fitted, since that is outside the syllabus.
"""

import sys

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats


def load_data(path):
    """Loads the holdings dataset from a CSV file."""
    return pd.read_csv(path)


def keep_most_recent(df):
    """Keeps the most recent declaration of each agent/company pair."""
    return df.sort_values('declaration_date').drop_duplicates(
        subset=['agent_id', 'company_NIF'], keep='last')


def fit_linear(x, y):
    """Least-squares line via scipy.stats.linregress (ch. 6).

    Returns the triple (slope, intercept, r).
    """
    model = stats.linregress(x, y)
    return model.slope, model.intercept, model.rvalue


def r_squared(r):
    """Coefficient of determination r^2 (ch. 6)."""
    return r * r


def compute_residuals(x, y, slope, intercept):
    """Residuals e_i = y_i - (slope * x_i + intercept) of the linear fit."""
    predicted = slope * x + intercept
    return y - predicted


def segment_statistics(df, n_segments=5):
    """Splits the ownership percentage into equal-width segments.

    For each ownership level (segment) it returns a dict with the percentage
    range, the number of points, the mean stock value, and the linear slope of
    ``total_value_in_stocks`` against ``company_percentage`` inside the segment
    (NaN when the slope cannot be computed).
    """
    pct = df['company_percentage']
    edges = np.linspace(pct.min(), pct.max(), n_segments + 1)
    labelled = df.copy()
    labelled['segment'] = pd.cut(pct, bins=edges, labels=False,
                                 include_lowest=True)

    result = []
    for segment in range(n_segments):
        group = labelled[labelled['segment'] == segment]
        if len(group) == 0:
            continue
        entry = {
            'segment': segment,
            'pct_low': edges[segment],
            'pct_high': edges[segment + 1],
            'n': len(group),
            'mean_value': group['total_value_in_stocks'].mean(),
            'slope': float('nan'),
        }
        if len(group) >= 2 and group['company_percentage'].std() > 0:
            model = stats.linregress(group['company_percentage'],
                                     group['total_value_in_stocks'])
            entry['slope'] = model.slope
        result.append(entry)
    return result


def acceleration_verdict(mean_values):
    """Classifies the trend of the per-segment mean values.

    Returns 'increasing', 'decreasing' or 'no monotonic trend'. NaN entries are
    ignored. 'increasing' (mean value rising with the ownership level) is the
    signal of acceleration at higher levels of ownership.
    """
    clean = [value for value in mean_values if value == value]
    if len(clean) < 2:
        return 'indeterminate'
    increasing = all(clean[i] < clean[i + 1] for i in range(len(clean) - 1))
    decreasing = all(clean[i] > clean[i + 1] for i in range(len(clean) - 1))
    if increasing:
        return 'increasing'
    if decreasing:
        return 'decreasing'
    return 'no monotonic trend'


def print_linear_fit(slope, intercept, r):
    """Prints the linear fit summary (stdout)."""
    print('LINEAR FIT (value ~ percentage)')
    print(f'  line : value = {slope:.2f} * percentage + {intercept:.2f}')
    print(f'  r    = {r:.4f}')
    print(f'  r^2  = {r_squared(r):.4f}')
    print()


def print_segments(segment_stats):
    """Prints the per-ownership-level statistics and the trend verdict."""
    print('STATISTICS BY OWNERSHIP LEVEL (equal-width segments)')
    for entry in segment_stats:
        print(f"  [{entry['pct_low']:5.2f}%, {entry['pct_high']:5.2f}%]  "
              f"n={entry['n']:4d}  mean_value={entry['mean_value']:14,.2f}  "
              f"slope={entry['slope']:14,.2f}")
    mean_trend = acceleration_verdict([e['mean_value'] for e in segment_stats])
    slope_trend = acceleration_verdict([e['slope'] for e in segment_stats])
    print(f'  -> mean-value trend with ownership level: {mean_trend}')
    print(f'  -> slope trend with ownership level (acceleration): {slope_trend}')
    print()


def plot_scatter_with_fit(df, slope, intercept, output_path):
    """Saves the scatter of percentage vs value with the fitted linear line."""
    x = df['company_percentage']
    y = df['total_value_in_stocks']
    plt.figure(figsize=(9, 6))
    plt.scatter(x, y, color='steelblue', alpha=0.5, label='Agent/company')
    line_x = np.linspace(x.min(), x.max(), 100)
    plt.plot(line_x, slope * line_x + intercept, color='red',
             label=f'linear fit: y = {slope:.0f}x + {intercept:.0f}')
    plt.title('US31 - Shareholding percentage vs stock value')
    plt.xlabel('Company percentage (%)')
    plt.ylabel('Total value in stocks')
    plt.legend()
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


def plot_residuals(df, slope, intercept, output_path):
    """Saves the residuals of the linear fit against the ownership percentage.

    A random cloud around zero supports the linear model; a systematic curve
    would signal non-linearity.
    """
    x = df['company_percentage']
    residuals = compute_residuals(x, df['total_value_in_stocks'],
                                  slope, intercept)
    plt.figure(figsize=(9, 6))
    plt.scatter(x, residuals, color='steelblue', alpha=0.5)
    plt.axhline(0, color='red', linewidth=1)
    plt.title('US31 - Residuals of the linear fit vs ownership percentage')
    plt.xlabel('Company percentage (%)')
    plt.ylabel('Residual')
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


def plot_mean_by_ownership(segment_stats, output_path):
    """Saves a bar chart of the mean stock value per ownership level."""
    labels = [f"{e['pct_low']:.1f}-{e['pct_high']:.1f}%" for e in segment_stats]
    means = [e['mean_value'] for e in segment_stats]
    plt.figure(figsize=(9, 6))
    plt.bar(labels, means, color='steelblue', edgecolor='black')
    plt.title('US31 - Mean stock value by ownership level')
    plt.xlabel('Ownership level (company percentage range)')
    plt.ylabel('Mean total value in stocks')
    plt.xticks(rotation=30)
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    df = load_data('dataset2_holdings.csv')
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    df = keep_most_recent(df)

    slope, intercept, r = fit_linear(df['company_percentage'],
                                     df['total_value_in_stocks'])
    print_linear_fit(slope, intercept, r)

    segment_stats = segment_statistics(df, n_segments=5)
    print_segments(segment_stats)

    plot_scatter_with_fit(df, slope, intercept,
                          'docs/system-documentation/US31/US31_scatter_fit.svg')
    plot_residuals(df, slope, intercept,
                   'docs/system-documentation/US31/US31_residuals.svg')
    plot_mean_by_ownership(segment_stats,
                           'docs/system-documentation/US31/US31_mean_by_ownership.svg')
