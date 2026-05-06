import sys
import os
import unittest
import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us17'))

from top_companies import aggregate_by_company, top_n, load_data


class TestAggregateByCompany(unittest.TestCase):

    def test_single_company_sums_correctly(self):
        df = pd.DataFrame({
            'company_NIF': ['C001', 'C001'],
            'total_value_in_stocks': [100000.0, 50000.0],
        })
        result = aggregate_by_company(df)
        self.assertEqual(len(result), 1)
        self.assertAlmostEqual(result.iloc[0]['total_value_in_stocks'], 150000.0)

    def test_multiple_companies_aggregated(self):
        df = pd.DataFrame({
            'company_NIF': ['C001', 'C002', 'C001'],
            'total_value_in_stocks': [100000.0, 200000.0, 50000.0],
        })
        result = aggregate_by_company(df)
        self.assertEqual(len(result), 2)
        self.assertAlmostEqual(result.iloc[0]['total_value_in_stocks'], 200000.0)
        self.assertAlmostEqual(result.iloc[1]['total_value_in_stocks'], 150000.0)

    def test_sorted_descending(self):
        df = pd.DataFrame({
            'company_NIF': ['C001', 'C002', 'C003'],
            'total_value_in_stocks': [10000.0, 50000.0, 30000.0],
        })
        result = aggregate_by_company(df)
        values = result['total_value_in_stocks'].tolist()
        self.assertEqual(values, sorted(values, reverse=True))


class TestTopN(unittest.TestCase):

    def setUp(self):
        self.df_agg = pd.DataFrame({
            'company_NIF': [f'C{i:03d}' for i in range(1, 16)],
            'total_value_in_stocks': list(range(15, 0, -1)),
        })

    def test_returns_n_rows(self):
        result = top_n(self.df_agg, n=10)
        self.assertEqual(len(result), 10)

    def test_returns_highest_values(self):
        result = top_n(self.df_agg, n=3)
        self.assertEqual(result.iloc[0]['total_value_in_stocks'], 15)
        self.assertEqual(result.iloc[1]['total_value_in_stocks'], 14)
        self.assertEqual(result.iloc[2]['total_value_in_stocks'], 13)


class TestLoadData(unittest.TestCase):

    SAMPLE_PATH = os.path.join(os.path.dirname(__file__),
                               '..', '..', 'resources', 'us17', 'sample_holdings.csv')

    def test_deduplication_keeps_most_recent(self):
        # A003/C001 appears in 2023 and 2024 — only 2024 (80000) should be kept
        df = load_data(self.SAMPLE_PATH)
        a003_c001 = df[(df['agent_id'] == 'A003') & (df['company_NIF'] == 'C001')]
        self.assertEqual(len(a003_c001), 1)
        self.assertAlmostEqual(a003_c001.iloc[0]['total_value_in_stocks'], 80000.0)

    def test_all_unique_pairs_kept(self):
        df = load_data(self.SAMPLE_PATH)
        # 7 unique (agent, company) pairs after deduplication
        self.assertEqual(len(df), 7)


if __name__ == '__main__':
    unittest.main(verbosity=2)
