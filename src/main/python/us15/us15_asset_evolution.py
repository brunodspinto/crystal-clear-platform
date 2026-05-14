"""
US15 - Examine the evolution of a political agent's total assets and net worth over time.

Reads the declarations CSV (exported by US24) filtered by a given agent_id,
and produces three separate line charts saved as SVG:
    1. Gross salary evolution over time.
    2. Side income evolution over time, split by type
       (consulting vs. board memberships).
    3. Asset evolution over time, split by category
       (real estate, vehicles, stocks).

All declaration types (initial, regular, exceptional) and all statuses
are included, as per the US15 specification.

Usage:
    python us15_asset_evolution.py <agent_id> [<path_to_declarations_csv>]

If no CSV path is provided, the default sample CSV is used.
"""

import csv
import os
import sys

DEFAULT_CSV = os.path.join(os.path.dirname(__file__),
                           "..", "..", "..", "test", "resources", "us15",
                           "sample_declarations.csv")

OUTPUT_DIR = os.path.join(os.path.dirname(__file__),
                          "..", "..", "..", "..", "docs",
                          "system-documentation", "US15")


# ---------------------------------------------------------------------------
# Data loading
# ---------------------------------------------------------------------------

def load_agent_declarations(path, agent_id):
    """
    Reads the declarations CSV and returns a list of dicts for the given
    agent_id, sorted chronologically by declaration_date.

    Each dict contains:
        agent_id, role, declaration_type, declaration_date, declaration_id,
        institution, gross_salary, side_income_consulting,
        side_income_board_memberships, assets_in_real_estate,
        assets_in_vehicles, assets_in_stocks.
    """
    numeric_fields = [
        "gross_salary", "side_income_consulting",
        "side_income_board_memberships", "assets_in_real_estate",
        "assets_in_vehicles", "assets_in_stocks"
    ]

    declarations = []
    with open(path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            if row["agent_id"].strip() != agent_id:
                continue
            entry = {}
            entry["agent_id"] = row["agent_id"].strip()
            entry["role"] = row["role"].strip()
            entry["declaration_type"] = row["declaration_type"].strip()
            entry["declaration_date"] = row["declaration_date"].strip()
            entry["declaration_id"] = row["declaration_id"].strip()
            entry["institution"] = row["institution"].strip()
            for field in numeric_fields:
                try:
                    entry[field] = float(row[field])
                except (ValueError, KeyError):
                    entry[field] = 0.0
            declarations.append(entry)

    declarations.sort(key=lambda d: d["declaration_date"])
    return declarations


# ---------------------------------------------------------------------------
# Derived fields
# ---------------------------------------------------------------------------

def total_side_income(decl):
    """Returns total side income (consulting + board memberships)."""
    return decl["side_income_consulting"] + decl["side_income_board_memberships"]


def total_assets(decl):
    """Returns total assets (real estate + vehicles + stocks)."""
    return (decl["assets_in_real_estate"]
            + decl["assets_in_vehicles"]
            + decl["assets_in_stocks"])


def net_worth(decl):
    """Returns net worth approximation (gross salary + side income + total assets)."""
    return decl["gross_salary"] + total_side_income(decl) + total_assets(decl)


# ---------------------------------------------------------------------------
# Console summary
# ---------------------------------------------------------------------------

def print_summary(declarations, agent_id):
    """Prints a text summary of the declarations to the console."""
    print("Agent: " + agent_id)
    print("Total declarations: " + str(len(declarations)))
    print()
    header = "{:<12}  {:<12}  {:>14}  {:>16}  {:>18}  {:>14}  {:>14}  {:>14}".format(
        "Date", "Type", "Gross Salary", "Side (Consult.)", "Side (Board)", "Real Estate", "Vehicles", "Stocks"
    )
    print(header)
    print("-" * len(header))
    for d in declarations:
        print("{:<12}  {:<12}  {:>14.2f}  {:>16.2f}  {:>18.2f}  {:>14.2f}  {:>14.2f}  {:>14.2f}".format(
            d["declaration_date"], d["declaration_type"],
            d["gross_salary"], d["side_income_consulting"],
            d["side_income_board_memberships"],
            d["assets_in_real_estate"], d["assets_in_vehicles"], d["assets_in_stocks"]
        ))
    print()


# ---------------------------------------------------------------------------
# Chart 1 — Gross Salary Evolution
# ---------------------------------------------------------------------------

def plot_gross_salary(declarations, agent_id, output_path):
    """
    Saves a line chart of gross salary evolution over time as SVG.
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    dates = [d["declaration_date"] for d in declarations]
    salaries = [d["gross_salary"] for d in declarations]

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.plot(dates, salaries, marker="o", color="steelblue", linewidth=2)
    ax.set_title("Gross Salary Evolution — Agent: " + agent_id)
    ax.set_xlabel("Declaration Date")
    ax.set_ylabel("Gross Salary")
    ax.tick_params(axis="x", rotation=30)
    ax.grid(True, linestyle="--", alpha=0.5)
    fig.tight_layout()
    file_path = os.path.join(
        output_path,
        f"gross_salary_evolution_{agent_id}.svg"
    )

    fig.savefig(file_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


# ---------------------------------------------------------------------------
# Chart 2 — Side Income Evolution (consulting vs board memberships)
# ---------------------------------------------------------------------------

def plot_side_income(declarations, agent_id, output_path):
    """
    Saves a line chart of side income evolution over time, split by type, as SVG.
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    dates = [d["declaration_date"] for d in declarations]
    consulting = [d["side_income_consulting"] for d in declarations]
    board = [d["side_income_board_memberships"] for d in declarations]
    total = [total_side_income(d) for d in declarations]

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.plot(dates, consulting, marker="o", color="steelblue",
            linewidth=2, label="Consulting")
    ax.plot(dates, board, marker="s", color="darkorange",
            linewidth=2, label="Board Memberships")
    ax.plot(dates, total, marker="^", color="green",
            linewidth=2, linestyle="--", label="Total Side Income")
    ax.set_title("Side Income Evolution — Agent: " + agent_id)
    ax.set_xlabel("Declaration Date")
    ax.set_ylabel("Side Income")
    ax.tick_params(axis="x", rotation=30)
    ax.legend()
    ax.grid(True, linestyle="--", alpha=0.5)
    fig.tight_layout()
    file_path = os.path.join(
        output_path,
        f"side_income_evolution_{agent_id}.svg"
    )

    fig.savefig(file_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


# ---------------------------------------------------------------------------
# Chart 3 — Asset Evolution (real estate, vehicles, stocks)
# ---------------------------------------------------------------------------

def plot_assets(declarations, agent_id, output_path):
    """
    Saves a line chart of asset evolution over time, split by category, as SVG.
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    dates = [d["declaration_date"] for d in declarations]
    real_estate = [d["assets_in_real_estate"] for d in declarations]
    vehicles = [d["assets_in_vehicles"] for d in declarations]
    stocks = [d["assets_in_stocks"] for d in declarations]
    totals = [total_assets(d) for d in declarations]

    fig, ax = plt.subplots(figsize=(10, 5))
    ax.plot(dates, real_estate, marker="o", color="steelblue",
            linewidth=2, label="Real Estate")
    ax.plot(dates, vehicles, marker="s", color="darkorange",
            linewidth=2, label="Vehicles")
    ax.plot(dates, stocks, marker="^", color="green",
            linewidth=2, label="Stocks")
    ax.plot(dates, totals, marker="D", color="purple",
            linewidth=2, linestyle="--", label="Total Assets")
    ax.set_title("Asset Evolution — Agent: " + agent_id)
    ax.set_xlabel("Declaration Date")
    ax.set_ylabel("Asset Value")
    ax.tick_params(axis="x", rotation=30)
    ax.legend()
    ax.grid(True, linestyle="--", alpha=0.5)
    fig.tight_layout()
    file_path = os.path.join(
        output_path,
        f"assets_evolution_{agent_id}.svg"
    )

    fig.savefig(file_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python us15_asset_evolution.py <agent_id> [<csv_path>]")
        sys.exit(1)

    agent_id = sys.argv[1]
    csv_path = sys.argv[2] if len(sys.argv) >= 3 else DEFAULT_CSV

    data = load_agent_declarations(csv_path, agent_id)

    if len(data) == 0:
        print("No declarations found for agent: " + agent_id)
        sys.exit(0)

    print_summary(data, agent_id)

    out_dir = os.path.normpath(OUTPUT_DIR)
    os.makedirs(out_dir, exist_ok=True)

    plot_gross_salary(data, agent_id, out_dir)
    plot_side_income(data, agent_id, out_dir)
    plot_assets(data, agent_id, out_dir)
