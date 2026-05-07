import sys
import os
import unittest
import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us18'))

from stock_increases import compute_increases, top_n_increases, get_evolution


class TestComputeIncreases(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id':              ['A001', 'A001', 'A002', 'A002', 'A003'],
            'company_NIF':           ['C001', 'C001', 'C001', 'C001', 'C002'],
            'total_value_in_stocks': [100000.0, 150000.0, 80000.0, 200000.0, 50000.0],
            'declaration_date': pd.to_datetime(
                ['2020-01-01', '2022-01-01', '2020-01-01', '2022-01-01', '2020-01-01']),
        })

    def test_increase_calculated_correctly(self):
        result = compute_increases(self.df)
        row = result[(result['agent_id'] == 'A002') & (result['company_NIF'] == 'C001')]
        self.assertAlmostEqual(row.iloc[0]['increase'], 120000.0)

    def test_sorted_descending(self):
        result = compute_increases(self.df)
        increases = result['increase'].tolist()
        self.assertEqual(increases, sorted(increases, reverse=True))

    def test_excludes_single_declaration(self):
        # A003/C002 has only one declaration — should be excluded
        result = compute_increases(self.df)
        excluded = result[(result['agent_id'] == 'A003') & (result['company_NIF'] == 'C002')]
        self.assertEqual(len(excluded), 0)

    def test_includes_negative_increase(self):
        df = pd.DataFrame({
            'agent_id':              ['A001', 'A001'],
            'company_NIF':           ['C003', 'C003'],
            'total_value_in_stocks': [200000.0, 180000.0],
            'declaration_date': pd.to_datetime(['2020-01-01', '2022-01-01']),
        })
        result = compute_increases(df)
        self.assertAlmostEqual(result.iloc[0]['increase'], -20000.0)


class TestTopNIncreases(unittest.TestCase):

    def setUp(self):
        self.df_inc = pd.DataFrame({
            'agent_id':      [f'A{i:03d}' for i in range(1, 16)],
            'company_NIF':   [f'C{i:03d}' for i in range(1, 16)],
            'initial_value': [0.0] * 15,
            'latest_value':  list(range(15, 0, -1)),
            'increase':      list(range(15, 0, -1)),
        })

    def test_returns_n_rows(self):
        result = top_n_increases(self.df_inc, n=10)
        self.assertEqual(len(result), 10)

    def test_returns_highest_increases(self):
        result = top_n_increases(self.df_inc, n=3)
        self.assertEqual(result.iloc[0]['increase'], 15)
        self.assertEqual(result.iloc[1]['increase'], 14)
        self.assertEqual(result.iloc[2]['increase'], 13)


class TestGetEvolution(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id':              ['A001', 'A001', 'A001', 'A002', 'A002'],
            'company_NIF':           ['C001', 'C001', 'C001', 'C002', 'C002'],
            'total_value_in_stocks': [100000.0, 120000.0, 150000.0, 80000.0, 200000.0],
            'declaration_date': pd.to_datetime(
                ['2020-01-01', '2021-01-01', '2022-01-01', '2020-01-01', '2022-01-01']),
            'company_percentage': [10.0, 10.0, 10.0, 8.0, 8.0],
        })
        self.df_top = pd.DataFrame({
            'agent_id':    ['A001'],
            'company_NIF': ['C001'],
            'increase':    [50000.0],
        })

    def test_contains_all_dates_for_pair(self):
        result = get_evolution(self.df, self.df_top)
        pair = result[(result['agent_id'] == 'A001') & (result['company_NIF'] == 'C001')]
        self.assertEqual(len(pair), 3)

    def test_excludes_other_pairs(self):
        result = get_evolution(self.df, self.df_top)
        other = result[result['agent_id'] == 'A002']
        self.assertEqual(len(other), 0)


if __name__ == '__main__':
    unittest.main(verbosity=2)
