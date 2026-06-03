"""US28 - Pearson correlation between total remuneration and asset types, by role.

As a member of the Ethics Committee, for a given role, this module computes the
Pearson correlation coefficient between the total declared remuneration
(gross salary + total side income) and each type of declared asset
(real estate, vehicles, stocks), and identifies which asset type has the
strongest correlation. The analysis is performed twice -- using each agent's
first declaration and using each agent's last declaration -- so the two results
can be compared.
"""

import sys

import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats

# Human-readable asset label -> dataset column name.
ASSET_COLUMNS = {
    'real estate': 'assets_in_real_estate',
    'vehicles':    'assets_in_vehicles',
    'stocks':      'assets_in_stocks',
}


def load_data(path):
    """Loads the declarations dataset from a CSV file."""
    return pd.read_csv(path)


def filter_by_role(df, role):
    """Returns only the declarations whose role matches the given one."""
    return df[df['role'] == role]


def compute_total_remuneration(df):
    """Adds a 'total_remuneration' column = gross salary + total side income."""
    df = df.copy()
    df['total_remuneration'] = (df['gross_salary']
                                + df['side_income_consulting']
                                + df['side_income_board_memberships'])
    return df


def compute_total_assets(df):
    """Adds a 'total_assets' column = real estate + vehicles + stocks."""
    df = df.copy()
    df['total_assets'] = (df['assets_in_real_estate']
                          + df['assets_in_vehicles']
                          + df['assets_in_stocks'])
    return df


def first_declarations(df):
    """Keeps, for each agent, only their earliest declaration (by date)."""
    return df.sort_values('declaration_date').drop_duplicates(
        subset='agent_id', keep='first')


def last_declarations(df):
    """Keeps, for each agent, only their most recent declaration (by date)."""
    return df.sort_values('declaration_date').drop_duplicates(
        subset='agent_id', keep='last')


def _linear_fit(x, y):
    """Fits a simple linear regression and returns (rvalue, pvalue).

    Uses ``scipy.stats.linregress`` (Statistics ch. 6). Returns (NaN, NaN)
    when the fit is undefined (fewer than two points or a constant series).
    The pvalue corresponds to the two-sided test H0: slope = 0, i.e. no
    linear association between the two variables (Statistics ch. 5).
    """
    if len(x) < 2 or x.std() == 0 or y.std() == 0:
        return float('nan'), float('nan')
    model = stats.linregress(x, y)
    return model.rvalue, model.pvalue


def pearson_coefficient(x, y):
    """Pearson's correlation coefficient r between two series.

    Returns NaN when it cannot be computed (fewer than two points or a
    constant series, where the coefficient is undefined).
    """
    r, _ = _linear_fit(x, y)
    return r


def pearson_pvalue(x, y):
    """P-value of the test H0: no linear association (slope = 0).

    Returns NaN when the fit is undefined.
    """
    _, p = _linear_fit(x, y)
    return p


def interpret_correlation(r):
    """Classifies a correlation coefficient (Statistics ch. 6, slide 11)."""
    if r != r:  # NaN
        return 'indeterminada'
    if r == 1:
        return 'perfeita positiva'
    if r >= 0.8:
        return 'forte positiva'
    if r >= 0.5:
        return 'moderada positiva'
    if r >= 0.1:
        return 'fraca positiva'
    if r > 0:
        return 'ínfima positiva'
    if r == 0:
        return 'nula'
    if r > -0.1:
        return 'ínfima negativa'
    if r > -0.5:
        return 'fraca negativa'
    if r > -0.8:
        return 'moderada negativa'
    if r > -1:
        return 'forte negativa'
    return 'perfeita negativa'


def overall_correlation(df):
    """Pearson's r and p between total remuneration and TOTAL declared assets.

    Answers the overall question of whether the total declared remuneration is
    aligned with the total declared assets. Returns the pair (r, p); both NaN
    when the coefficient is undefined.
    """
    return _linear_fit(df['total_remuneration'], df['total_assets'])


def pearson_correlations(df):
    """Pearson's r between total remuneration and each asset type.

    Returns a dict mapping the asset label to its correlation coefficient.
    """
    correlations = {}
    remuneration = df['total_remuneration']
    for label, column in ASSET_COLUMNS.items():
        correlations[label] = pearson_coefficient(remuneration, df[column])
    return correlations


def pearson_pvalues(df):
    """P-value (slope significance) for each asset type vs total remuneration.

    Returns a dict mapping the asset label to its p-value.
    """
    pvalues = {}
    remuneration = df['total_remuneration']
    for label, column in ASSET_COLUMNS.items():
        pvalues[label] = pearson_pvalue(remuneration, df[column])
    return pvalues


def strongest_correlation(correlations):
    """Returns the (label, r) pair with the largest absolute correlation.

    NaN values are ignored. Returns (None, NaN) when nothing is comparable.
    """
    best_label = None
    best_magnitude = -1.0
    for label, r in correlations.items():
        if r != r:  # skip NaN
            continue
        if abs(r) > best_magnitude:
            best_magnitude = abs(r)
            best_label = label
    if best_label is None:
        return None, float('nan')
    return best_label, correlations[best_label]


def compare_correlations(corr_first, corr_last):
    """Compares first vs last correlations per asset type.

    Returns a dict mapping the asset label to a (r_first, r_last, delta) tuple,
    where delta = r_last - r_first.
    """
    comparison = {}
    for label in ASSET_COLUMNS:
        r_first = corr_first[label]
        r_last = corr_last[label]
        comparison[label] = (r_first, r_last, r_last - r_first)
    return comparison


def print_correlations(title, correlations, pvalues=None):
    """Prints r, its interpretation and (optionally) the significance."""
    print(title)
    for label, r in correlations.items():
        line = f'  {label:<12} r = {r:>8.4f}  ({interpret_correlation(r)})'
        if pvalues is not None:
            p = pvalues[label]
            significance = 'significativa' if (p == p and p < 0.05) \
                else 'não significativa'
            line += f'  p = {p:>7.4f} [{significance}]'
        print(line)
    label, r = strongest_correlation(correlations)
    if label is None:
        print('  -> strongest: n/a (not enough data)')
    else:
        print(f'  -> strongest: {label} '
              f'(r = {r:.4f}, {interpret_correlation(r)})')
    print()


def print_overall(title, r, p):
    """Prints the overall remuneration vs total-assets correlation (stdout)."""
    significance = 'significativa' if (p == p and p < 0.05) \
        else 'não significativa'
    print(f'{title}: r = {r:.4f} ({interpret_correlation(r)})  '
          f'p = {p:.4f} [{significance}]')


def print_comparison(comparison):
    """Prints the change in correlation between first and last declarations."""
    print('CHANGE BETWEEN FIRST AND LAST DECLARATIONS')
    for label, (r_first, r_last, delta) in comparison.items():
        print(f'  {label:<12} first = {r_first:>8.4f}  '
              f'last = {r_last:>8.4f}  delta = {delta:>+8.4f}')
    print()


def plot_comparison(corr_first, corr_last, role, output_path):
    """Saves a grouped bar chart comparing first vs last correlations."""
    labels = list(ASSET_COLUMNS.keys())
    first_values = [corr_first[label] for label in labels]
    last_values = [corr_last[label] for label in labels]
    positions = range(len(labels))
    width = 0.35

    plt.figure(figsize=(9, 6))
    plt.bar([p - width / 2 for p in positions], first_values, width,
            label='First declarations', color='steelblue')
    plt.bar([p + width / 2 for p in positions], last_values, width,
            label='Last declarations', color='indianred')
    plt.axhline(0, color='black', linewidth=0.8)
    plt.xticks(list(positions), labels)
    plt.ylabel("Pearson's r (total remuneration vs asset type)")
    plt.ylim(-1, 1)
    plt.title(f"US28 - Remuneration vs Asset Correlation ({role})")
    plt.legend()
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    role = sys.argv[1] if len(sys.argv) > 1 else 'MP'

    df = load_data('dataset1_declarations.csv')
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    df = filter_by_role(df, role)
    df = compute_total_remuneration(df)
    df = compute_total_assets(df)

    df_first = first_declarations(df)
    df_last = last_declarations(df)

    corr_first = pearson_correlations(df_first)
    corr_last = pearson_correlations(df_last)
    pval_first = pearson_pvalues(df_first)
    pval_last = pearson_pvalues(df_last)

    print_correlations(f'FIRST DECLARATIONS - role: {role}', corr_first, pval_first)
    print_correlations(f'LAST DECLARATIONS  - role: {role}', corr_last, pval_last)
    print_comparison(compare_correlations(corr_first, corr_last))

    print('OVERALL - total remuneration vs TOTAL assets')
    r_first_total, p_first_total = overall_correlation(df_first)
    r_last_total, p_last_total = overall_correlation(df_last)
    print_overall('  first', r_first_total, p_first_total)
    print_overall('  last ', r_last_total, p_last_total)
    print()

    plot_comparison(corr_first, corr_last, role,
                    'docs/system-documentation/US28/US28_correlation.svg')
