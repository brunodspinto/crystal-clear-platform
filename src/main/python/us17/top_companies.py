import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


def load_data(path):
    df = pd.read_csv(path)
    df = df.sort_values('declaration_date').drop_duplicates(subset=['agent_id', 'company_NIF'], keep='last')
    return df


def aggregate_by_company(df):
    agg = df.groupby('company_NIF', as_index=False)['total_value_in_stocks'].sum()
    agg = agg.sort_values('total_value_in_stocks', ascending=False)
    return agg


def top_n(df_agg, n=10):
    return df_agg[:n]


def print_top_companies(df_top):
    print(f'TOP {len(df_top)} COMPANIES BY TOTAL SHARE VALUE')
    for row in df_top.values:
        print(f'  {str(row[0]):<20} {row[1]:>15,.2f}')
    print()


def plot_top_companies(df_top, output_path):
    nifs = [str(nif) for nif in df_top['company_NIF']]
    values = df_top['total_value_in_stocks'].tolist()
    sns.barplot(x=values, y=nifs, color='red')
    plt.title('US17 — Top 10 Companies by Total Share Value')
    plt.xlabel('Total Value in Stocks (€)')
    plt.ylabel('Company NIF')
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    df = load_data('dataset2_holdings.csv')
    df_agg = aggregate_by_company(df)
    df_top = top_n(df_agg, n=10)
    print_top_companies(df_top)
    plot_top_companies(df_top, 'docs/system-documentation/US17/US17_top_companies.svg')
