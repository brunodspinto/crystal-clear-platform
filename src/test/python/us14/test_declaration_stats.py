import sys
import os
import unittest
import math
import statistics as st

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us14'))

import pandas as pd
from declaration_stats import central_tendency, compute_quantiles, variability, shape_stats


class TestCentralTendency(unittest.TestCase):

    def setUp(self):
        self.data = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])

    def test_mean(self):
        result = central_tendency(self.data)
        self.assertAlmostEqual(result['mean'], 30.0, places=4)

    def test_median(self):
        result = central_tendency(self.data)
        self.assertAlmostEqual(result['median'], 30.0, places=4)

    def test_mode(self):
        data = pd.Series([10.0, 10.0, 20.0, 30.0, 40.0])
        result = central_tendency(data)
        self.assertAlmostEqual(result['mode'], 10.0, places=4)


class TestVariability(unittest.TestCase):

    def setUp(self):
        self.data = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])

    def test_total_range(self):
        result = variability(self.data)
        self.assertAlmostEqual(result['range'], 40.0, places=4)

    def test_sample_variance(self):
        result = variability(self.data)
        self.assertAlmostEqual(result['variance'], 250.0, places=4)


class TestComputeQuantiles(unittest.TestCase):

    def setUp(self):
        self.data = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])

    def test_quartile_q1(self):
        expected_q1 = st.quantiles([10.0, 20.0, 30.0, 40.0, 50.0], n=4)[0]
        result = compute_quantiles(self.data)
        self.assertAlmostEqual(result['q1'], expected_q1, places=4)


class TestShapeStats(unittest.TestCase):

    def test_skewness_symmetric(self):
        data = pd.Series([10.0, 20.0, 30.0, 40.0, 50.0])
        result = shape_stats(data)
        self.assertAlmostEqual(result['skewness'], 0.0, places=4)

    def test_skewness_right_skewed(self):
        data = pd.Series([1.0, 2.0, 3.0, 4.0, 100.0])
        result = shape_stats(data)
        self.assertGreater(result['skewness'], 0)

    def test_skewness_left_skewed(self):
        data = pd.Series([1.0, 97.0, 98.0, 99.0, 100.0])
        result = shape_stats(data)
        self.assertLess(result['skewness'], 0)


if __name__ == '__main__':
    unittest.main(verbosity=2)
