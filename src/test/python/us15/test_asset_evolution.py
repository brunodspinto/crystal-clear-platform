"""
Tests for US15 - Asset and Net Worth Evolution.
"""

import unittest
import sys
import os

CURRENT_DIR = os.path.dirname(__file__)

MAIN_PYTHON_PATH = os.path.abspath(
    os.path.join(CURRENT_DIR, "../../../main/python/us15")
)

sys.path.append(MAIN_PYTHON_PATH)
from us15_asset_evolution import (
    load_agent_declarations,
    total_side_income,
    total_assets,
    net_worth,
)

SAMPLE_CSV = os.path.join(os.path.dirname(__file__),
                          "..", "..", "resources", "us15",
                          "sample_declarations.csv")


class TestLoadAgentDeclarations(unittest.TestCase):

    def test_loads_correct_agent_only(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        for d in data:
            self.assertEqual(d["agent_id"], "123456789")

    def test_filters_out_other_agents(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        for d in data:
            self.assertNotEqual(d["agent_id"], "987654321")

    def test_correct_number_of_declarations_for_agent(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        self.assertEqual(len(data), 4)

    def test_returns_empty_for_unknown_agent(self):
        data = load_agent_declarations(SAMPLE_CSV, "000000000")
        self.assertEqual(len(data), 0)

    def test_sorted_chronologically(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        dates = [d["declaration_date"] for d in data]
        self.assertEqual(dates, sorted(dates))

    def test_includes_all_declaration_types(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        types = set(d["declaration_type"] for d in data)
        self.assertIn("initial", types)
        self.assertIn("regular", types)
        self.assertIn("exceptional", types)

    def test_numeric_fields_are_floats(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        for d in data:
            self.assertIsInstance(d["gross_salary"], float)
            self.assertIsInstance(d["side_income_consulting"], float)
            self.assertIsInstance(d["side_income_board_memberships"], float)
            self.assertIsInstance(d["assets_in_real_estate"], float)
            self.assertIsInstance(d["assets_in_vehicles"], float)
            self.assertIsInstance(d["assets_in_stocks"], float)


class TestDerivedFields(unittest.TestCase):

    def _make_decl(self, consulting, board, real_estate, vehicles, stocks, gross):
        return {
            "gross_salary": gross,
            "side_income_consulting": consulting,
            "side_income_board_memberships": board,
            "assets_in_real_estate": real_estate,
            "assets_in_vehicles": vehicles,
            "assets_in_stocks": stocks,
        }

    def test_total_side_income(self):
        d = self._make_decl(2000.0, 500.0, 0, 0, 0, 0)
        self.assertAlmostEqual(total_side_income(d), 2500.0)

    def test_total_side_income_zero(self):
        d = self._make_decl(0, 0, 0, 0, 0, 0)
        self.assertAlmostEqual(total_side_income(d), 0.0)

    def test_total_assets(self):
        d = self._make_decl(0, 0, 200000.0, 15000.0, 5000.0, 0)
        self.assertAlmostEqual(total_assets(d), 220000.0)

    def test_total_assets_zero(self):
        d = self._make_decl(0, 0, 0, 0, 0, 0)
        self.assertAlmostEqual(total_assets(d), 0.0)

    def test_net_worth(self):
        d = self._make_decl(2000.0, 500.0, 200000.0, 15000.0, 5000.0, 55000.0)
        expected = 55000.0 + 2000.0 + 500.0 + 200000.0 + 15000.0 + 5000.0
        self.assertAlmostEqual(net_worth(d), expected)

    def test_net_worth_from_csv(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        first = data[0]
        expected = (first["gross_salary"]
                    + first["side_income_consulting"]
                    + first["side_income_board_memberships"]
                    + first["assets_in_real_estate"]
                    + first["assets_in_vehicles"]
                    + first["assets_in_stocks"])
        self.assertAlmostEqual(net_worth(first), expected)

    def test_total_assets_matches_sum_of_categories_from_csv(self):
        data = load_agent_declarations(SAMPLE_CSV, "123456789")
        for d in data:
            expected = (d["assets_in_real_estate"]
                        + d["assets_in_vehicles"]
                        + d["assets_in_stocks"])
            self.assertAlmostEqual(total_assets(d), expected)


if __name__ == "__main__":
    unittest.main()
