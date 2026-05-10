"""
US13 - Exploratory Analysis of Declarations by Type, Role, and Institution.

Reads the declarations CSV and produces:
1. Pie chart: distribution of declarations by type (SVG)
2. Bar chart: total declarations by role and by institution (SVG)
"""

import csv
import os

DEFAULT_CSV = os.path.join(os.path.dirname(__file__),
                           "..", "..", "..", "test", "resources", "us13", "sample_declarations.csv")

OUTPUT_DIR = os.path.join(os.path.dirname(__file__),
                          "..", "..", "..", "..", "docs", "system-documentation", "US13")


def load_data(path):
    """
    Reads the declarations CSV and returns a list of dicts, one per row.
    Each dict has keys: declaration_id, agent_id, role, institution,
    declaration_type, declaration_date, gross_salary, side_income,
    assets_in_real_estate, assets_in_vehicles, assets_in_stocks.
    Rows with missing/invalid numeric fields default to 0.
    """
    declarations = []
    numeric_fields = ["gross_salary", "side_income",
                      "assets_in_real_estate", "assets_in_vehicles", "assets_in_stocks"]

    with open(path, "r") as f:
        reader = csv.DictReader(f)
        for row in reader:
            entry = {}
            entry["declaration_id"] = row["declaration_id"].strip()
            entry["agent_id"] = row["agent_id"].strip()
            entry["role"] = row["role"].strip()
            entry["institution"] = row["institution"].strip()
            entry["declaration_type"] = row["declaration_type"].strip()
            entry["declaration_date"] = row["declaration_date"].strip()
            for field in numeric_fields:
                try:
                    entry[field] = float(row[field])
                except (ValueError, KeyError):
                    entry[field] = 0.0
            declarations.append(entry)
    return declarations


def count_by_field(declarations, field):
    """Returns a dict {value: count} for the given field across all declarations."""
    counts = {}
    for decl in declarations:
        value = decl[field]
        if value not in counts:
            counts[value] = 0
        counts[value] = counts[value] + 1
    return counts


def print_counts(label, counts):
    print(label)
    for key in sorted(counts.keys()):
        print("  " + key + ": " + str(counts[key]))


def plot_type_distribution(declarations, output_path):
    """
    Saves a pie chart of declaration type proportions as SVG.
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    counts = count_by_field(declarations, "declaration_type")
    labels = sorted(counts.keys())
    sizes = [counts[l] for l in labels]

    fig, ax = plt.subplots(figsize=(6, 6))
    ax.pie(sizes, labels=labels, autopct="%1.1f%%", startangle=90)
    ax.set_title("Distribution of Declarations by Type")

    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


def plot_by_role_and_institution(declarations, output_path):
    """
    Saves a bar chart with two subplots (by role, by institution) as SVG.
    """
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    role_counts = count_by_field(declarations, "role")
    inst_counts = count_by_field(declarations, "institution")

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))

    roles = sorted(role_counts.keys())
    ax1.bar(roles, [role_counts[r] for r in roles], color="steelblue")
    ax1.set_title("Total Declarations by Role")
    ax1.set_xlabel("Role")
    ax1.set_ylabel("Number of Declarations")
    ax1.tick_params(axis="x", rotation=30)

    institutions = sorted(inst_counts.keys())
    ax2.bar(institutions, [inst_counts[i] for i in institutions], color="darkorange")
    ax2.set_title("Total Declarations by Institution")
    ax2.set_xlabel("Institution")
    ax2.set_ylabel("Number of Declarations")
    ax2.tick_params(axis="x", rotation=30)

    fig.tight_layout()
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
    print("Saved: " + output_path)


if __name__ == "__main__":
    data = load_data(DEFAULT_CSV)
    print("Total declarations loaded: " + str(len(data)))
    print()
    print_counts("By declaration type:", count_by_field(data, "declaration_type"))
    print()
    print_counts("By role:", count_by_field(data, "role"))
    print()
    print_counts("By institution:", count_by_field(data, "institution"))
    print()

    out_dir = os.path.normpath(OUTPUT_DIR)
    plot_type_distribution(data, os.path.join(out_dir, "us13_chart1_type_distribution.svg"))
    plot_by_role_and_institution(data, os.path.join(out_dir, "us13_chart2_role_institution.svg"))
