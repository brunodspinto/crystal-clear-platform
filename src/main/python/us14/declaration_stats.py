# =============================================================
# US14 - Descriptive Statistical Analysis of Declarations
# =============================================================

import os
import pandas as pd
import matplotlib.pyplot as plt
import statistics as st
from scipy import stats


def load_data(path):
    """Read CSV, keep most recent declaration per agent, derive total_income and total_assets."""
    df = pd.read_csv(path)
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    df_recent = (
        df.sort_values('declaration_date', ascending=False)
          .drop_duplicates(subset='agent_id', keep='first')
          .reset_index(drop=True)
    )
    # total_income = gross salary + secondary income (consulting and board memberships)
    df_recent['total_income'] = (df_recent['gross_salary']
                                 + df_recent['side_income_consulting']
                                 + df_recent['side_income_board_memberships'])
    df_recent['total_assets'] = (df_recent['assets_in_real_estate']
                                 + df_recent['assets_in_vehicles']
                                 + df_recent['assets_in_stocks'])
    return df_recent


def central_tendency(data):
    """Return mean, median and mode for a pandas Series."""
    return {
        'mean':   data.mean(),
        'median': data.median(),
        'mode':   data.mode()[0],
    }


def compute_quantiles(data):
    """Return quartiles, key deciles and key percentiles using statistics.quantiles.
    Requires at least 2 data points."""
    lst = data.tolist()
    quartiles   = st.quantiles(lst, n=4)
    deciles     = st.quantiles(lst, n=10)
    percentiles = st.quantiles(lst, n=100)
    return {
        'q1': quartiles[0],    'q2': quartiles[1],   'q3': quartiles[2],
        'd1': deciles[0],      'd5': deciles[4],     'd9': deciles[8],
        'p10': percentiles[9], 'p25': percentiles[24],
        'p75': percentiles[74],'p90': percentiles[89],
    }


def variability(data):
    """Return range, IQR, sample variance, sample std dev and coefficient of variation."""
    quartiles_v = st.quantiles(data.tolist(), n=4)
    q1, q3 = quartiles_v[0], quartiles_v[2]
    std_dev = data.std()
    return {
        'range':          data.max() - data.min(),
        'iqr':            q3 - q1,
        'variance':       data.var(),
        'std_dev':        std_dev,
        'coef_variation': std_dev / abs(data.mean()),
    }


def shape_stats(data):
    """Return sample skewness (bias=False) and real kurtosis (fisher=False)."""
    return {
        'skewness': float(stats.skew(data, bias=False)),
        'kurtosis': float(stats.kurtosis(data, fisher=False)),
    }


def print_central_tendency(label, ct):
    print(f'{"="*55}')
    print(f'  {label.upper()}')
    print(f'{"="*55}')
    print(f'  Mean     (x̄)  : {ct["mean"]:>15,.2f} €')
    print(f'  Median   (x̃)  : {ct["median"]:>15,.2f} €')
    print(f'  Mode          : {ct["mode"]:>15,.2f} €')
    print()


def print_quantiles(label, q):
    print(f'{"="*55}')
    print(f'  {label.upper()} — Quantiles')
    print(f'{"="*55}')
    print(f'  Quartile 1 (q1 = p25)  : {q["q1"]:>12,.2f} €')
    print(f'  Quartile 2 (q2 = p50)  : {q["q2"]:>12,.2f} €  ← Median')
    print(f'  Quartile 3 (q3 = p75)  : {q["q3"]:>12,.2f} €')
    print()
    print(f'  Decile 1  (d1 = p10)   : {q["d1"]:>12,.2f} €')
    print(f'  Decile 5  (d5 = p50)   : {q["d5"]:>12,.2f} €')
    print(f'  Decile 9  (d9 = p90)   : {q["d9"]:>12,.2f} €')
    print()
    print(f'  Percentile 10  (p10)   : {q["p10"]:>12,.2f} €')
    print(f'  Percentile 25  (p25)   : {q["p25"]:>12,.2f} €')
    print(f'  Percentile 75  (p75)   : {q["p75"]:>12,.2f} €')
    print(f'  Percentile 90  (p90)   : {q["p90"]:>12,.2f} €')
    print()


def print_variability(label, v):
    cv = v['coef_variation']
    print(f'{"="*55}')
    print(f'  {label.upper()} — Variability')
    print(f'{"="*55}')
    print(f'  Total range (r)         : {v["range"]:>15,.2f} €')
    print(f'  Interquartile range     : {v["iqr"]:>15,.2f} €')
    print(f'  Sample variance (s²)    : {v["variance"]:>15,.2f}')
    print(f'  Standard deviation (s)  : {v["std_dev"]:>15,.2f} €')
    print(f'  Coeff. of variation (cv): {cv:>14.4f}  ({cv*100:.2f}%)')
    print()


def print_shape(label, s):
    skewness = s['skewness']
    kurtosis = s['kurtosis']
    if skewness > 0:
        interp_skew = 'RIGHT-skewed (right tail)'
    elif skewness < 0:
        interp_skew = 'LEFT-skewed (left tail)'
    else:
        interp_skew = 'SYMMETRIC'
    if kurtosis > 3:
        interp_kurt = 'more LEPTOKURTIC than Normal (heavier tails)'
    elif kurtosis < 3:
        interp_kurt = 'more PLATYKURTIC than Normal (lighter tails)'
    else:
        interp_kurt = 'equal to the NORMAL distribution'
    print(f'{"="*60}')
    print(f'  {label.upper()} — Skewness and Kurtosis')
    print(f'{"="*60}')
    print(f'  Skewness coeff. (a3)    : {skewness:.4f}')
    print(f'  Interpretation          : Distribution {interp_skew}')
    print()
    print(f'  Kurtosis coeff. (a4)    : {kurtosis:.4f}')
    print(f'  Interpretation          : Distribution {interp_kurt}')
    print()


def plot_histograms(df, output_path):
    """Save side-by-side histograms of total_income and total_assets as SVG."""
    fig, axes = plt.subplots(1, 2, figsize=(14, 5))
    for variable, xlabel, ax in [
        ('total_income', 'Total Income (€)', axes[0]),
        ('total_assets', 'Total Assets (€)', axes[1]),
    ]:
        data   = df[variable].dropna()
        mean   = data.mean()
        median = data.median()
        ax.hist(data, bins='auto', color='steelblue', edgecolor='white', alpha=0.85)
        ax.axvline(mean,   color='red',    linestyle='--', linewidth=1.5,
                   label=f'Mean = {mean:,.0f} €')
        ax.axvline(median, color='orange', linestyle='-',  linewidth=1.5,
                   label=f'Median = {median:,.0f} €')
        ax.set_title(f'Distribution of {xlabel}', fontsize=12)
        ax.set_xlabel(xlabel, fontsize=10)
        ax.set_ylabel('Absolute frequency', fontsize=10)
        ax.legend(fontsize=9)
        ax.grid(axis='y', linestyle='--', alpha=0.4)
    plt.suptitle('US14 — Histograms: Total Income and Total Assets\n'
                 '(most recent declaration per agent)', fontsize=13, y=1.02)
    plt.tight_layout()
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    plt.savefig(output_path, format='svg', bbox_inches='tight')
    plt.show()


def plot_boxplots(df, output_path):
    """Save side-by-side boxplots of total_income and total_assets as SVG."""
    fig, axes = plt.subplots(1, 2, figsize=(14, 6))
    for variable, ylabel, ax in [
        ('total_income', 'Total Income (€)', axes[0]),
        ('total_assets', 'Total Assets (€)', axes[1]),
    ]:
        data = df[variable].dropna()
        ax.boxplot(data, vert=True, patch_artist=True,
                   boxprops=dict(facecolor='lightsteelblue', color='steelblue'),
                   medianprops=dict(color='red', linewidth=2),
                   whiskerprops=dict(color='steelblue'),
                   capprops=dict(color='steelblue'),
                   flierprops=dict(marker='o', color='steelblue', alpha=0.5))
        quartiles_b = st.quantiles(data.tolist(), n=4)
        q1, q3      = quartiles_b[0], quartiles_b[2]
        iqr         = q3 - q1
        lower_fence = q1 - 1.5 * iqr
        upper_fence = q3 + 1.5 * iqr
        outliers    = data[(data < lower_fence) | (data > upper_fence)]
        ax.set_title(f'Boxplot — {ylabel}', fontsize=12)
        ax.set_ylabel(ylabel, fontsize=10)
        ax.set_xticks([])
        ax.grid(axis='y', linestyle='--', alpha=0.4)
        print(f'{ylabel}: {len(outliers)} outlier(s) detected')
        if len(outliers) > 0:
            print(f'  Values: {sorted(outliers.tolist())}')
    plt.suptitle('US14 — Boxplots: Total Income and Total Assets\n'
                 '(most recent declaration per agent)', fontsize=13, y=1.02)
    plt.tight_layout()
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    plt.savefig(output_path, format='svg', bbox_inches='tight')
    plt.show()


def full_summary(df):
    """Return a DataFrame with all statistical measures for total_income and total_assets."""
    summary = pd.DataFrame()
    for variable, label in [('total_income', 'Total Income'), ('total_assets', 'Total Assets')]:
        data      = df[variable].dropna()
        quartiles = st.quantiles(data.tolist(), n=4)
        s         = shape_stats(data)
        summary[label] = pd.Series({
            'n (observations)'         : len(data),
            'Mean (x̄)'                : round(data.mean(), 2),
            'Median (x̃)'              : round(data.median(), 2),
            'Mode'                     : round(data.mode()[0], 2),
            'Quartile 1 (q1)'          : round(quartiles[0], 2),
            'Quartile 3 (q3)'          : round(quartiles[2], 2),
            'Total range (r)'          : round(data.max() - data.min(), 2),
            'Interquartile range (rq)' : round(quartiles[2] - quartiles[0], 2),
            'Sample variance (s²)'     : round(data.var(), 2),
            'Std deviation (s)'        : round(data.std(), 2),
            'Coeff. variation (cv)'    : round(data.std() / abs(data.mean()), 4),
            'Skewness coeff. (a3)'     : round(s['skewness'], 4),
            'Kurtosis coeff. (a4)'     : round(s['kurtosis'], 4),
        })
    return summary


def main():
    import sys
    csv_path = sys.argv[1] if len(sys.argv) > 1 else 'dataset1_declarations.csv'
    docs_dir = os.path.join('docs', 'system-documentation', 'US14')

    df = load_data(csv_path)
    print('Number of political agents (most recent declaration):', len(df))
    print(df[['agent_id', 'declaration_date', 'total_income', 'total_assets']].head().to_string())
    print()

    for variable, label in [('total_income', 'Total Income'), ('total_assets', 'Total Assets')]:
        data = df[variable].dropna()
        print_central_tendency(label, central_tendency(data))
        print_quantiles(label, compute_quantiles(data))
        print_variability(label, variability(data))
        print_shape(label, shape_stats(data))

    plot_histograms(df, os.path.join(docs_dir, 'US14_histograms.svg'))
    plot_boxplots(df,   os.path.join(docs_dir, 'US14_boxplots.svg'))

    summary = full_summary(df)
    print('FULL STATISTICAL SUMMARY — US14')
    print('=' * 60)
    print(summary.to_string())


if __name__ == '__main__':
    main()
