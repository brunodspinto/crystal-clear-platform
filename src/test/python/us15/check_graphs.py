import os
import sys

CURRENT_DIR = os.path.dirname(__file__)

MAIN_PYTHON_PATH = os.path.abspath(
    os.path.join(CURRENT_DIR, "../../../main/python/us15")
)

sys.path.append(MAIN_PYTHON_PATH)

from us15_asset_evolution import (
    load_agent_declarations,
    plot_gross_salary,
    plot_side_income,
    plot_assets,
    OUTPUT_DIR,
    DEFAULT_CSV
)


def test_graph_generation():
    agent_id = "123456789"  # sample agent from US15 dataset

    declarations = load_agent_declarations(DEFAULT_CSV, agent_id)

    if not declarations:
        print("No declarations found for testing.")
        return

    print(f"Loaded {len(declarations)} declarations")

    # gerar gráficos
    plot_gross_salary(declarations, agent_id, OUTPUT_DIR)
    plot_side_income(declarations, agent_id, OUTPUT_DIR)
    plot_assets(declarations, agent_id, OUTPUT_DIR)

    expected_files = [
        f"gross_salary_evolution_{agent_id}.svg",
        f"side_income_evolution_{agent_id}.svg",
        f"assets_evolution_{agent_id}.svg"
    ]

    print("\nChecking generated files:\n")

    success = True

    for file_name in expected_files:
        full_path = os.path.join(OUTPUT_DIR, file_name)

        if os.path.exists(full_path):
            size = os.path.getsize(full_path)
            print(f"[OK] {file_name} ({size} bytes)")

            if size == 0:
                print(f"     WARNING: File is empty")
                success = False

        else:
            print(f"[FAIL] {file_name} was not generated")
            success = False

    print()

    if success:
        print("All graphs generated successfully.")
    else:
        print("Some graphs failed.")


if __name__ == "__main__":
    test_graph_generation()