import sys
import os
import unittest

import numpy as np
import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us30'))

from residual_analysis import (
    keep_most_recent,
    compute_totals,
    fit_regression,
    compute_residuals,
    standardize,
    residuals_original,
    residuals_normalized,
    describe_residuals,
)


class TestKeepMostRecent(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id':         ['A', 'A', 'B'],
            'declaration_date': pd.to_datetime(['2019-01-01', '2023-01-01', '2020-01-01']),
            'marker':           ['a-old', 'a-recent', 'b-only'],
        })

    def test_one_row_per_agent(self):
        result = keep_most_recent(self.df)
        self.assertEqual(len(result), 2)

    def test_keeps_latest(self):
        result = keep_most_recent(self.df)
        markers = set(result['marker'])
        self.assertEqual(markers, {'a-recent', 'b-only'})


class TestComputeTotals(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'gross_salary':                  [50000.0],
            'side_income_consulting':        [3000.0],
            'side_income_board_memberships': [2000.0],
            'assets_in_real_estate':         [100000.0],
            'assets_in_vehicles':            [20000.0],
            'assets_in_stocks':              [30000.0],
        })

    def test_total_income(self):
        result = compute_totals(self.df)
        self.assertAlmostEqual(result.iloc[0]['total_income'], 55000.0)

    def test_total_assets(self):
        result = compute_totals(self.df)
        self.assertAlmostEqual(result.iloc[0]['total_assets'], 150000.0)

    def test_does_not_mutate_input(self):
        compute_totals(self.df)
        self.assertNotIn('total_income', self.df.columns)


class TestFitRegression(unittest.TestCase):

    def test_perfect_line_recovers_slope_and_intercept(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0, 5.0])
        y = 2.0 * x + 1.0
        slope, intercept = fit_regression(x, y)
        self.assertAlmostEqual(slope, 2.0)
        self.assertAlmostEqual(intercept, 1.0)


class TestComputeResiduals(unittest.TestCase):

    def test_residual_is_observed_minus_predicted(self):
        x = pd.Series([0.0, 1.0, 2.0])
        y = pd.Series([1.0, 5.0, 7.0])  # predicted with slope=2, intercept=1: 1,3,5
        residuals = compute_residuals(x, y, slope=2.0, intercept=1.0)
        self.assertAlmostEqual(residuals.iloc[0], 0.0)
        self.assertAlmostEqual(residuals.iloc[1], 2.0)
        self.assertAlmostEqual(residuals.iloc[2], 2.0)

    def test_perfect_fit_gives_zero_residuals(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0])
        y = 3.0 * x - 2.0
        slope, intercept = fit_regression(x, y)
        residuals = compute_residuals(x, y, slope, intercept)
        for value in residuals:
            self.assertAlmostEqual(value, 0.0)


class TestStandardize(unittest.TestCase):

    def test_mean_is_zero(self):
        s = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])
        z = standardize(s)
        self.assertAlmostEqual(z.mean(), 0.0)

    def test_std_is_one(self):
        s = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])
        z = standardize(s)
        self.assertAlmostEqual(z.std(), 1.0)  # sample std (ddof=1), as in US29

    def test_constant_series_does_not_divide_by_zero(self):
        s = pd.Series([7.0, 7.0, 7.0])
        z = standardize(s)
        for value in z:
            self.assertAlmostEqual(value, 0.0)


class TestResiduals(unittest.TestCase):

    def setUp(self):
        # total_assets is a noisy linear function of total_income.
        self.df = pd.DataFrame({
            'total_income': [10.0, 20.0, 30.0, 40.0, 50.0],
            'total_assets': [22.0, 39.0, 61.0, 78.0, 101.0],
        })

    def test_original_residuals_sum_to_zero(self):
        # An ordinary least-squares fit always yields residuals with mean 0.
        residuals = residuals_original(self.df)
        self.assertAlmostEqual(residuals.mean(), 0.0, places=6)

    def test_normalized_residuals_have_mean_zero(self):
        residuals = residuals_normalized(self.df)
        self.assertAlmostEqual(residuals.mean(), 0.0, places=6)

    def test_one_residual_per_row(self):
        self.assertEqual(len(residuals_original(self.df)), len(self.df))
        self.assertEqual(len(residuals_normalized(self.df)), len(self.df))


class TestDescribeResiduals(unittest.TestCase):

    def test_mean_and_std(self):
        residuals = pd.Series([-2.0, -1.0, 0.0, 1.0, 2.0])
        description = describe_residuals(residuals)
        self.assertAlmostEqual(description['mean'], 0.0)
        # sample std (ddof=1), matching pandas .std() / US29 convention
        self.assertAlmostEqual(description['std'], np.std([-2.0, -1.0, 0.0, 1.0, 2.0], ddof=1))

    def test_min_and_max(self):
        residuals = pd.Series([-3.0, 0.5, 4.0])
        description = describe_residuals(residuals)
        self.assertAlmostEqual(description['min'], -3.0)
        self.assertAlmostEqual(description['max'], 4.0)


if __name__ == '__main__':
    unittest.main(verbosity=2)
