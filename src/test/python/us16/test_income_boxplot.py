import sys
import os
import tempfile
import unittest

import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                "..", "..", "..", "main", "python", "us16"))

from income_boxplot import load_data, summarise, quartis


class TestLoadData(unittest.TestCase):

    def setUp(self):
        self.tmp = tempfile.mkdtemp()
        self.path = os.path.join(self.tmp, "data.csv")

    def _write(self, content):
        f = open(self.path, "w")
        f.write(content)
        f.close()

    def test_returns_dataframe(self):
        self._write("name,role,total_income\nA,deputy,71000\nB,mayor,55000\n")
        df = load_data(self.path)
        self.assertIsInstance(df, pd.DataFrame)
        self.assertEqual(len(df), 2)

    def test_keeps_expected_columns(self):
        self._write("name,role,total_income\nA,deputy,71000\n")
        df = load_data(self.path)
        self.assertIn("name", df.columns)
        self.assertIn("role", df.columns)
        self.assertIn("total_income", df.columns)


class TestSummarise(unittest.TestCase):

    def _df(self, rows):
        return pd.DataFrame(rows, columns=["name", "role", "total_income"])

    def test_returns_describe_columns(self):
        df = self._df([("A", "deputy", 70000.0), ("B", "deputy", 80000.0)])
        stats = summarise(df)
        for col in ["count", "mean", "std", "min", "25%", "50%", "75%", "max"]:
            self.assertIn(col, stats.columns)

    def test_groups_by_role(self):
        df = self._df([
            ("A", "deputy", 70000.0),
            ("B", "mayor", 55000.0),
            ("C", "deputy", 80000.0),
        ])
        stats = summarise(df)
        self.assertIn("deputy", stats.index)
        self.assertIn("mayor", stats.index)
        self.assertEqual(stats.loc["deputy", "count"], 2)
        self.assertEqual(stats.loc["mayor", "count"], 1)

    def test_mean_per_role(self):
        df = self._df([("A", "deputy", 70000.0), ("B", "deputy", 80000.0)])
        stats = summarise(df)
        self.assertEqual(stats.loc["deputy", "mean"], 75000.0)

    def test_median_per_role(self):
        df = self._df([
            ("A", "deputy", 70000.0),
            ("B", "deputy", 80000.0),
            ("C", "deputy", 90000.0),
        ])
        stats = summarise(df)
        self.assertEqual(stats.loc["deputy", "50%"], 80000.0)

    def test_min_max_per_role(self):
        df = self._df([
            ("A", "mayor", 50000.0),
            ("B", "mayor", 60000.0),
            ("C", "mayor", 70000.0),
        ])
        stats = summarise(df)
        self.assertEqual(stats.loc["mayor", "min"], 50000.0)
        self.assertEqual(stats.loc["mayor", "max"], 70000.0)


class TestQuartis(unittest.TestCase):

    def test_returns_three_values(self):
        q = quartis([1, 2, 3, 4, 5, 6, 7, 8])
        self.assertEqual(len(q), 3)

    def test_median_is_second_quartile(self):
        q = quartis([1, 2, 3, 4, 5, 6, 7, 8])
        self.assertAlmostEqual(q[1], 4.5)


if __name__ == "__main__":
    unittest.main()
