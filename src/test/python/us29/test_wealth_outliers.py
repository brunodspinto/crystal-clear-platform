import sys
import os
import unittest
import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us29'))

from wealth_outliers import (
    keep_most_recent,
    compute_totals,
    fit_regression,
    compute_residuals,
    top_n_deviations,
    detect_outliers,
)


class TestKeepMostRecent(unittest.TestCase):

    def test_keeps_latest_per_agent(self):
        df = pd.DataFrame({
            'agent_id':         ['A001', 'A001', 'A002'],
            'declaration_date': ['2020-01-01', '2022-01-01', '2021-01-01'],
        })
        result = keep_most_recent(df)
        self.assertEqual(len(result), 2)
        a001 = result[result['agent_id'] == 'A001']
        self.assertEqual(a001.iloc[0]['declaration_date'], '2022-01-01')

    def test_single_declaration_kept(self):
        df = pd.DataFrame({
            'agent_id':         ['A001'],
            'declaration_date': ['2020-01-01'],
        })
        result = keep_most_recent(df)
        self.assertEqual(len(result), 1)


class TestComputeTotals(unittest.TestCase):

    def test_income_and_assets_sum(self):
        df = pd.DataFrame({
            'gross_salary':                  [100.0],
            'side_income_consulting':        [50.0],
            'side_income_board_memberships': [30.0],
            'assets_in_real_estate':         [10.0],
            'assets_in_vehicles':            [20.0],
            'assets_in_stocks':              [30.0],
        })
        result = compute_totals(df)
        self.assertAlmostEqual(result.iloc[0]['total_income'], 180.0)
        self.assertAlmostEqual(result.iloc[0]['total_assets'], 60.0)


class TestFitRegression(unittest.TestCase):

    def test_perfect_linear(self):
        df = pd.DataFrame({
            'total_income': [1.0, 2.0, 3.0, 4.0],
            'total_assets': [2.0, 4.0, 6.0, 8.0],
        })
        slope, intercept, r = fit_regression(df)
        self.assertAlmostEqual(slope, 2.0)
        self.assertAlmostEqual(intercept, 0.0)
        self.assertAlmostEqual(r, 1.0)

    def test_inverse_relation(self):
        df = pd.DataFrame({
            'total_income': [1.0, 2.0, 3.0, 4.0],
            'total_assets': [8.0, 6.0, 4.0, 2.0],
        })
        slope, intercept, r = fit_regression(df)
        self.assertAlmostEqual(slope, -2.0)
        self.assertAlmostEqual(r, -1.0)


class TestComputeResiduals(unittest.TestCase):

    def test_residual_is_actual_minus_predicted(self):
        df = pd.DataFrame({
            'total_income': [10.0, 20.0],
            'total_assets': [25.0, 35.0],
        })
        result = compute_residuals(df, slope=2.0, intercept=0.0)
        self.assertAlmostEqual(result.iloc[0]['residual'], 5.0)
        self.assertAlmostEqual(result.iloc[1]['residual'], -5.0)


class TestTopNDeviations(unittest.TestCase):

    def test_returns_top_by_absolute(self):
        df = pd.DataFrame({
            'agent_id': ['A1', 'A2', 'A3', 'A4'],
            'residual': [10.0, -20.0, 5.0, -3.0],
        })
        result = top_n_deviations(df, n=2)
        self.assertEqual(len(result), 2)
        self.assertEqual(result['agent_id'].tolist(), ['A2', 'A1'])

    def test_n_larger_than_df(self):
        df = pd.DataFrame({
            'agent_id': ['A1', 'A2'],
            'residual': [10.0, -20.0],
        })
        result = top_n_deviations(df, n=10)
        self.assertEqual(len(result), 2)


class TestDetectOutliers(unittest.TestCase):

    def test_extreme_values_flagged(self):
        df = pd.DataFrame({
            'agent_id': ['A1', 'A2', 'A3', 'A4', 'A5'],
            'residual': [0.0, 1.0, -1.0, 100.0, -100.0],
        })
        result = detect_outliers(df, threshold=1.0)
        agents = result['agent_id'].tolist()
        self.assertIn('A4', agents)
        self.assertIn('A5', agents)

    def test_no_outliers_when_uniform(self):
        df = pd.DataFrame({
            'agent_id': ['A1', 'A2', 'A3'],
            'residual': [1.0, 1.0, 1.0],
        })
        result = detect_outliers(df, threshold=2.0)
        self.assertEqual(len(result), 0)


if __name__ == '__main__':
    unittest.main(verbosity=2)
