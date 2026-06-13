import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from scipy import stats


def load_data(path):
    df = pd.read_csv(path)
    return df


def keep_most_recent(df):
    return df.sort_values('declaration_date').drop_duplicates(subset='agent_id', keep='last')


def compute_totals(df):
    df = df.copy()
    df['total_income'] = (df['gross_salary']
                          + df['side_income_consulting']
                          + df['side_income_board_memberships'])
    df['total_assets'] = (df['assets_in_real_estate']
                          + df['assets_in_vehicles']
                          + df['assets_in_stocks'])
    return df


def fit_regression(df):
    slope, intercept, r_value, p_value, std_err = stats.linregress(
        df['total_income'], df['total_assets'])
    return slope, intercept, r_value


def compute_residuals(df, slope, intercept):
    df = df.copy()
    df['predicted_assets'] = slope * df['total_income'] + intercept
    df['residual'] = df['total_assets'] - df['predicted_assets']
    return df


def top_n_deviations(df, n=10):
    df = df.copy()
    df['abs_residual'] = df['residual'].abs()
    return df.sort_values('abs_residual', ascending=False).head(n)


def detect_outliers(df, threshold=2.0):
    df = df.copy()
    mean_r = df['residual'].mean()
    std_r = df['residual'].std()
    df['z_score'] = (df['residual'] - mean_r) / std_r
    return df[df['z_score'].abs() > threshold]


def print_top_deviations(df_top):
    print(f'TOP {len(df_top)} AGENTS BY DEVIATION')
    for _, row in df_top.iterrows():
        print(f"  {str(row['agent_id']):<10} residual={row['residual']:>14,.2f}")
    print()


def print_outliers(df_out):
    print(f'OUTLIERS (|z| > 2): {len(df_out)}')
    for _, row in df_out.iterrows():
        print(f"  {str(row['agent_id']):<10} z={row['z_score']:>6.2f} residual={row['residual']:>14,.2f}")
    print()


def plot_regression(df, df_top, slope, intercept, output_path):
    plt.figure(figsize=(10, 6))
    plt.scatter(df['total_income'], df['total_assets'], color='blue', alpha=0.5, label='Agents')
    x_line = np.linspace(df['total_income'].min(), df['total_income'].max(), 100)
    plt.plot(x_line, slope * x_line + intercept, color='black',
             label=f'y = {slope:.4f}x + {intercept:.2f}')
    plt.scatter(df_top['total_income'], df_top['total_assets'],
                color='red', label='Top 10 deviation')
    plt.title('US29 - Income vs Assets Regression')
    plt.xlabel('Total income')
    plt.ylabel('Total assets')
    plt.legend()
    plt.tight_layout()
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    df = load_data('dataset1_declarations.csv')
    df = keep_most_recent(df)
    df = compute_totals(df)
    slope, intercept, r_value = fit_regression(df)
    print(f'Regression: assets = {slope:.4f} * income + {intercept:.2f}')
    print(f'r = {r_value:.4f}, r2 = {r_value**2:.4f}')
    df = compute_residuals(df, slope, intercept)
    df_top = top_n_deviations(df, n=10)
    print_top_deviations(df_top)
    df_out = detect_outliers(df, threshold=2.0)
    print_outliers(df_out)
    plot_regression(df, df_top, slope, intercept,
                    'docs/system-documentation/US29/US29_regression.svg')
