import sys
import os
import math
import unittest

import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us31'))

from shareholding_nonlinearity import (
    keep_most_recent,
    fit_linear,
    r_squared,
    compute_residuals,
    segment_statistics,
    acceleration_verdict,
)


class TestKeepMostRecent(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id':         ['A', 'A', 'A', 'B'],
            'company_NIF':      ['C1', 'C1', 'C2', 'C1'],
            'declaration_date': pd.to_datetime(
                ['2020-01-01', '2023-01-01', '2021-01-01', '2022-01-01']),
            'marker':           ['a-c1-old', 'a-c1-new', 'a-c2', 'b-c1'],
        })

    def test_one_row_per_agent_company(self):
        result = keep_most_recent(self.df)
        self.assertEqual(len(result), 3)

    def test_keeps_latest_for_pair(self):
        result = keep_most_recent(self.df)
        markers = set(result['marker'])
        self.assertEqual(markers, {'a-c1-new', 'a-c2', 'b-c1'})


class TestFitLinear(unittest.TestCase):

    def test_perfect_line(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0, 5.0])
        y = 3.0 * x + 2.0
        slope, intercept, r = fit_linear(x, y)
        self.assertAlmostEqual(slope, 3.0)
        self.assertAlmostEqual(intercept, 2.0)
        self.assertAlmostEqual(r, 1.0)


class TestRSquared(unittest.TestCase):

    def test_perfect(self):
        self.assertAlmostEqual(r_squared(1.0), 1.0)

    def test_half(self):
        self.assertAlmostEqual(r_squared(0.5), 0.25)

    def test_negative_r_gives_positive_r2(self):
        self.assertAlmostEqual(r_squared(-0.8), 0.64)


class TestComputeResiduals(unittest.TestCase):

    def test_residual_is_observed_minus_predicted(self):
        x = pd.Series([0.0, 1.0, 2.0])
        y = pd.Series([1.0, 5.0, 7.0])  # predicted (slope 2, intercept 1): 1,3,5
        residuals = compute_residuals(x, y, slope=2.0, intercept=1.0)
        self.assertAlmostEqual(residuals.iloc[0], 0.0)
        self.assertAlmostEqual(residuals.iloc[1], 2.0)
        self.assertAlmostEqual(residuals.iloc[2], 2.0)


class TestSegmentStatistics(unittest.TestCase):

    def setUp(self):
        # 10 points, percentages 1..10, value = 1000 * percentage.
        self.df = pd.DataFrame({
            'company_percentage':    [1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0],
            'total_value_in_stocks': [1000.0, 2000.0, 3000.0, 4000.0, 5000.0,
                                      6000.0, 7000.0, 8000.0, 9000.0, 10000.0],
        })

    def test_two_segments_split_in_half(self):
        stats = segment_statistics(self.df, n_segments=2)
        self.assertEqual(len(stats), 2)
        self.assertEqual(stats[0]['n'], 5)
        self.assertEqual(stats[1]['n'], 5)

    def test_segment_mean_values(self):
        stats = segment_statistics(self.df, n_segments=2)
        # segment 0: pct 1..5 -> mean value 3000; segment 1: pct 6..10 -> 8000
        self.assertAlmostEqual(stats[0]['mean_value'], 3000.0)
        self.assertAlmostEqual(stats[1]['mean_value'], 8000.0)

    def test_segment_slope_matches_perfect_line(self):
        stats = segment_statistics(self.df, n_segments=2)
        self.assertAlmostEqual(stats[0]['slope'], 1000.0)
        self.assertAlmostEqual(stats[1]['slope'], 1000.0)


class TestAccelerationVerdict(unittest.TestCase):

    def test_increasing(self):
        self.assertEqual(acceleration_verdict([1.0, 2.0, 3.0, 4.0]), 'increasing')

    def test_decreasing(self):
        self.assertEqual(acceleration_verdict([4.0, 3.0, 2.0, 1.0]), 'decreasing')

    def test_no_monotonic_trend(self):
        self.assertEqual(acceleration_verdict([1.0, 3.0, 2.0]), 'no monotonic trend')

    def test_ignores_nan(self):
        self.assertEqual(acceleration_verdict([1.0, float('nan'), 2.0, 3.0]), 'increasing')

    def test_indeterminate_with_one_value(self):
        self.assertEqual(acceleration_verdict([float('nan'), 5.0]), 'indeterminate')


if __name__ == '__main__':
    unittest.main(verbosity=2)
