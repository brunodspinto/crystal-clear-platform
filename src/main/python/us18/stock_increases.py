"""
US18 - Identify Top Stock Value Increases.

Reads the holdings CSV (one row per agent/company/date) and, for each
(agent_id, company_NIF) pair, computes the increase in stock value from
the initial declaration to the most recent declaration.

Identifies the ten pairs with the largest increase and plots their full
evolution over time.

Produces two SVG charts saved to docs/system-documentation/US18/:
  1. US18_top_increases.svg  -- horizontal bar chart of the top 10 increases
  2. US18_evolution.svg      -- line chart showing evolution over time for each pair
"""

import os
import sys
import pandas as pd
import matplotlib.pyplot as plt


def load_data(path):
    """
    Reads the holdings CSV and returns a DataFrame with declaration_date
    parsed as datetime. All rows are kept (no deduplication) since the full
    history is needed to compute increases and plot evolution.
    """
    df = pd.read_csv(path)
    df['declaration_date'] = pd.to_datetime(df['declaration_date'])
    return df


def compute_increases(df):
    """
    For each (agent_id, company_NIF) pair, computes the increase in
    total_value_in_stocks from the initial (earliest) declaration to the
    most recent (latest) declaration.

    Pairs with only one declaration are excluded (no increase can be computed).
    Returns a DataFrame sorted by increase descending, with columns:
      agent_id, company_NIF, initial_value, latest_value, increase.
    """
    rows = []
    for (agent_id, company_nif), group in df.groupby(['agent_id', 'company_NIF']):
        sorted_group = group.sort_values('declaration_date')
        if len(sorted_group) < 2:
            continue
        initial = sorted_group.iloc[0]['total_value_in_stocks']
        latest  = sorted_group.iloc[-1]['total_value_in_stocks']
        rows.append({
            'agent_id':      agent_id,
            'company_NIF':   company_nif,
            'initial_value': initial,
            'latest_value':  latest,
            'increase':      latest - initial,
        })
    return (pd.DataFrame(rows)
              .sort_values('increase', ascending=False)
              .reset_index(drop=True))


def top_n_increases(df_inc, n=10):
    """Returns the top N rows of the increases DataFrame (highest increases first)."""
    return df_inc.head(n).reset_index(drop=True)


def get_evolution(df, df_top):
    """
    Returns all historical data rows from df for the (agent_id, company_NIF)
    pairs present in df_top, sorted by pair and then by declaration_date.
    """
    keys = df_top[['agent_id', 'company_NIF']]
    evolution = df.merge(keys, on=['agent_id', 'company_NIF'], how='inner')
    return evolution.sort_values(['agent_id', 'company_NIF', 'declaration_date']).reset_index(drop=True)


def print_top_increases(df_top):
    """Prints a ranked table of the top pairs and their stock value increases to stdout."""
    print('=' * 70)
    print(f'  TOP {len(df_top)} STOCK VALUE INCREASES (initial → most recent)')
    print('=' * 70)
    print(f'  {"Rank":<6} {"Agent":<10} {"Company NIF":<15} {"Initial (€)":>14} {"Latest (€)":>14} {"Increase (€)":>14}')
    print(f'  {"-"*6} {"-"*10} {"-"*15} {"-"*14} {"-"*14} {"-"*14}')
    for i, row in df_top.iterrows():
        print(f'  {i+1:<6} {str(row["agent_id"]):<10} {str(row["company_NIF"]):<15} '
              f'{row["initial_value"]:>14,.2f} {row["latest_value"]:>14,.2f} {row["increase"]:>14,.2f}')
    print()


def plot_top_increases(df_top, output_path):
    """
    Saves a horizontal bar chart of the top 10 stock value increases as SVG.
    Bars are ordered with the highest increase at the top and annotated with
    their value in euros.
    """
    labels = [f'{row["agent_id"]} / {row["company_NIF"]}' for _, row in df_top.iterrows()]
    values = df_top['increase'].tolist()

    labels = labels[::-1]
    values = values[::-1]

    fig, ax = plt.subplots(figsize=(13, 6))
    bars = ax.barh(labels, values, color='steelblue', edgecolor='white', alpha=0.85)

    for bar, val in zip(bars, values):
        ax.text(bar.get_width() * 1.005, bar.get_y() + bar.get_height() / 2,
                f'{val:,.0f} €', va='center', fontsize=9)

    ax.set_title('US18 — Top 10 Stock Value Increases\n'
                 '(initial declaration → most recent declaration)', fontsize=13)
    ax.set_xlabel('Increase in Stock Value (€)', fontsize=10)
    ax.set_ylabel('Agent / Company', fontsize=10)
    ax.grid(axis='x', linestyle='--', alpha=0.4)
    plt.tight_layout()

    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    plt.savefig(output_path, format='svg', bbox_inches='tight')
    plt.show()


def plot_evolution(df_evolution, output_path):
    """
    Saves a line chart showing the evolution of total_value_in_stocks over time
    for each (agent_id, company_NIF) pair. Each pair is drawn as a separate line
    with markers at each data point.
    """
    fig, ax = plt.subplots(figsize=(14, 7))
    colors = plt.cm.tab10.colors

    for i, ((agent_id, company_nif), group) in enumerate(
            df_evolution.groupby(['agent_id', 'company_NIF'])):
        group_sorted = group.sort_values('declaration_date')
        label = f'{agent_id} / {company_nif}'
        ax.plot(group_sorted['declaration_date'],
                group_sorted['total_value_in_stocks'],
                marker='o', linewidth=1.5, label=label,
                color=colors[i % len(colors)])

    ax.set_title('US18 — Evolution of Top 10 Stock Value Increases Over Time', fontsize=13)
    ax.set_xlabel('Declaration Date', fontsize=10)
    ax.set_ylabel('Total Value in Stocks (€)', fontsize=10)
    ax.legend(fontsize=8, loc='upper left', bbox_to_anchor=(1, 1))
    ax.grid(linestyle='--', alpha=0.4)
    plt.tight_layout()

    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    plt.savefig(output_path, format='svg', bbox_inches='tight')
    plt.show()


def main():
    csv_path = sys.argv[1] if len(sys.argv) > 1 else 'dataset2_holdings.csv'
    docs_dir = os.path.join('docs', 'system-documentation', 'US18')

    df = load_data(csv_path)
    print(f'Holdings records loaded: {len(df)}')

    df_inc = compute_increases(df)
    df_top = top_n_increases(df_inc, n=10)

    print_top_increases(df_top)

    plot_top_increases(df_top, os.path.join(docs_dir, 'US18_top_increases.svg'))
    plot_evolution(get_evolution(df, df_top), os.path.join(docs_dir, 'US18_evolution.svg'))


if __name__ == '__main__':
    main()
