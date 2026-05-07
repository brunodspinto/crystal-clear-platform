"""
US17 - Identify Top Companies by Share Values.

Reads the holdings CSV (one row per agent/company/date) and, for each
political actor, considers only their most recent declaration per company.
Aggregates total share value per company and identifies the ten companies
with the highest total values held by political actors.

Produces one SVG chart saved to docs/system-documentation/US17/:
  1. US17_top_companies.svg -- horizontal bar chart of the top 10 companies
"""

import os
import sys
import pandas as pd
import matplotlib.pyplot as plt


def load_data(path):
    """
    Reads the holdings CSV and returns a DataFrame with one row per
    (agent_id, company_NIF) pair, keeping only the most recent declaration_date
    for each pair.
    """
    df = pd.read_csv(path)
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    df_recent = (
        df.sort_values('declaration_date', ascending=False)
          .drop_duplicates(subset=['agent_id', 'company_NIF'], keep='first')
          .reset_index(drop=True)
    )
    return df_recent


def aggregate_by_company(df):
    """
    Groups the holdings DataFrame by company_NIF, sums total_value_in_stocks
    for each company, and returns the result sorted in descending order.
    """
    agg = (
        df.groupby('company_NIF', as_index=False)['total_value_in_stocks']
          .sum()
          .sort_values('total_value_in_stocks', ascending=False)
          .reset_index(drop=True)
    )
    return agg


def top_n(df_agg, n=10):
    """Returns the top N rows of an aggregated DataFrame sorted by total share value."""
    return df_agg.head(n).reset_index(drop=True)


def print_top_companies(df_top):
    """Prints a ranked table of companies and their total share values to stdout."""
    print('=' * 55)
    print(f'  TOP {len(df_top)} COMPANIES BY TOTAL SHARE VALUE')
    print('=' * 55)
    print(f'  {"Rank":<6} {"Company NIF":<20} {"Total Value (€)":>15}')
    print(f'  {"-"*6} {"-"*20} {"-"*15}')
    for i, row in df_top.iterrows():
        print(f'  {i+1:<6} {str(row["company_NIF"]):<20} {row["total_value_in_stocks"]:>15,.2f}')
    print()


def plot_top_companies(df_top, output_path):
    """
    Saves a horizontal bar chart of the top companies as SVG.
    Bars are ordered with the highest value at the top. Each bar is annotated
    with its total value in euros.
    """
    labels = [str(nif) for nif in df_top['company_NIF']]
    values = df_top['total_value_in_stocks'].tolist()

    labels = labels[::-1]
    values = values[::-1]

    fig, ax = plt.subplots(figsize=(12, 6))
    bars = ax.barh(labels, values, color='steelblue', edgecolor='white', alpha=0.85)

    for bar, val in zip(bars, values):
        ax.text(bar.get_width() * 1.005, bar.get_y() + bar.get_height() / 2,
                f'{val:,.0f} €', va='center', fontsize=9)

    ax.set_title('US17 — Top 10 Companies by Total Share Value\n'
                 '(most recent declaration per agent)', fontsize=13)
    ax.set_xlabel('Total Value in Stocks (€)', fontsize=10)
    ax.set_ylabel('Company NIF', fontsize=10)
    ax.grid(axis='x', linestyle='--', alpha=0.4)
    plt.tight_layout()

    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    plt.savefig(output_path, format='svg', bbox_inches='tight')
    plt.show()


def main():
    csv_path = sys.argv[1] if len(sys.argv) > 1 else 'dataset2_holdings.csv'
    docs_dir = os.path.join('docs', 'system-documentation', 'US17')

    df = load_data(csv_path)
    print(f'Holdings records (most recent per agent/company): {len(df)}')

    df_agg = aggregate_by_company(df)
    df_top = top_n(df_agg, n=10)

    print_top_companies(df_top)
    plot_top_companies(df_top, os.path.join(docs_dir, 'US17_top_companies.svg'))


if __name__ == '__main__':
    main()
