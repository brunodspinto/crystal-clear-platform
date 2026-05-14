import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns


def load_data(path):
    df = pd.read_csv(path)
    return df


def compute_increases(df):
    rows = []
    for (agent_id, company_nif), group in df.groupby(['agent_id', 'company_NIF']):
        if len(group) < 2:
            continue
        sorted_group = group.sort_values('declaration_date')
        initial = sorted_group['total_value_in_stocks'].values[0]
        latest = sorted_group['total_value_in_stocks'].values[-1]
        rows.append({
            'agent_id':      agent_id,
            'company_NIF':   company_nif,
            'initial_value': initial,
            'latest_value':  latest,
            'increase':      latest - initial,
        })
    result = pd.DataFrame(rows)
    result = result.sort_values('increase', ascending=False)
    return result


def top_n_increases(df_inc, n=10):
    return df_inc[:n]


def get_evolution(df, df_top):
    pairs = set(zip(df_top['agent_id'], df_top['company_NIF']))
    mask = [(a, c) in pairs for a, c in df[['agent_id', 'company_NIF']].values]
    return df[mask]


def print_top_increases(df_top):
    print(f'TOP {len(df_top)} STOCK VALUE INCREASES')
    cols = df_top[['agent_id', 'company_NIF', 'initial_value', 'latest_value', 'increase']].values
    for row in cols:
        print(f'  {str(row[0]):<10} {str(row[1]):<15} {row[2]:>14,.2f} {row[3]:>14,.2f} {row[4]:>14,.2f}')
    print()


def plot_top_increases(df_top, output_path):
    labels = [f'{row[0]}/{row[1]}' for row in df_top[['agent_id', 'company_NIF']].values]
    values = df_top['increase'].tolist()
    sns.barplot(x=values, y=labels, color='red')
    plt.title('US18 — Top 10 Stock Value Increases')
    plt.xlabel('Increase in Stock Value (€)')
    plt.ylabel('Agent / Company')
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


def plot_evolution(df_top, output_path):
    labels = [f'{row[0]}/{row[1]}' for row in df_top[['agent_id', 'company_NIF']].values]
    fig, axes = plt.subplots(1, 2)
    sns.barplot(x=df_top['initial_value'].tolist(), y=labels, color='steelblue', ax=axes[0])
    axes[0].set_title('Initial Value')
    axes[0].set_xlabel('Total Value in Stocks (€)')
    axes[0].set_ylabel('Agent / Company')
    sns.barplot(x=df_top['latest_value'].tolist(), y=labels, color='red', ax=axes[1])
    axes[1].set_title('Latest Value')
    axes[1].set_xlabel('Total Value in Stocks (€)')
    axes[1].set_ylabel('Agent / Company')
    plt.suptitle('US18 — Stock Value Evolution: Initial vs Latest')
    plt.tight_layout()
    plt.savefig(output_path)
    plt.show()


if __name__ == '__main__':
    df = load_data('dataset2_holdings.csv')
    df_inc = compute_increases(df)
    df_top = top_n_increases(df_inc, n=10)
    print_top_increases(df_top)
    plot_top_increases(df_top, 'docs/system-documentation/US18/US18_top_increases.svg')
    plot_evolution(df_top, 'docs/system-documentation/US18/US18_evolution.svg')
