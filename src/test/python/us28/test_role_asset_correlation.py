import sys
import os
import math
import unittest

import pandas as pd

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                '..', '..', '..', 'main', 'python', 'us28'))

from role_asset_correlation import (
    filter_by_role,
    compute_total_remuneration,
    compute_total_assets,
    first_declarations,
    last_declarations,
    pearson_coefficient,
    pearson_pvalue,
    interpret_correlation,
    pearson_correlations,
    overall_correlation,
    strongest_correlation,
    compare_correlations,
)


class TestFilterByRole(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id': ['A', 'B', 'C'],
            'role':     ['MP', 'Judge', 'MP'],
        })

    def test_keeps_only_matching_role(self):
        result = filter_by_role(self.df, 'MP')
        self.assertEqual(len(result), 2)
        self.assertTrue((result['role'] == 'MP').all())

    def test_no_match_returns_empty(self):
        result = filter_by_role(self.df, 'Minister')
        self.assertEqual(len(result), 0)


class TestComputeTotalRemuneration(unittest.TestCase):

    def test_sums_salary_and_side_incomes(self):
        df = pd.DataFrame({
            'gross_salary':                  [50000.0],
            'side_income_consulting':        [3000.0],
            'side_income_board_memberships': [2000.0],
        })
        result = compute_total_remuneration(df)
        self.assertAlmostEqual(result.iloc[0]['total_remuneration'], 55000.0)

    def test_does_not_mutate_input(self):
        df = pd.DataFrame({
            'gross_salary':                  [10.0],
            'side_income_consulting':        [1.0],
            'side_income_board_memberships': [1.0],
        })
        compute_total_remuneration(df)
        self.assertNotIn('total_remuneration', df.columns)


class TestComputeTotalAssets(unittest.TestCase):

    def test_sums_all_asset_types(self):
        df = pd.DataFrame({
            'assets_in_real_estate': [100000.0],
            'assets_in_vehicles':    [20000.0],
            'assets_in_stocks':      [30000.0],
        })
        result = compute_total_assets(df)
        self.assertAlmostEqual(result.iloc[0]['total_assets'], 150000.0)

    def test_does_not_mutate_input(self):
        df = pd.DataFrame({
            'assets_in_real_estate': [1.0],
            'assets_in_vehicles':    [1.0],
            'assets_in_stocks':      [1.0],
        })
        compute_total_assets(df)
        self.assertNotIn('total_assets', df.columns)


class TestOverallCorrelation(unittest.TestCase):

    def test_perfect_alignment(self):
        # total_assets perfectly proportional to total_remuneration -> r = 1
        df = pd.DataFrame({
            'total_remuneration': [10.0, 20.0, 30.0, 40.0],
            'total_assets':       [100.0, 200.0, 300.0, 400.0],
        })
        r, p = overall_correlation(df)
        self.assertAlmostEqual(r, 1.0)
        self.assertLess(p, 0.05)

    def test_constant_assets_returns_nan(self):
        df = pd.DataFrame({
            'total_remuneration': [10.0, 20.0, 30.0],
            'total_assets':       [5.0, 5.0, 5.0],
        })
        r, p = overall_correlation(df)
        self.assertTrue(math.isnan(r))


class TestFirstLastDeclarations(unittest.TestCase):

    def setUp(self):
        self.df = pd.DataFrame({
            'agent_id':         ['A', 'A', 'A', 'B', 'B'],
            'declaration_date': pd.to_datetime(
                ['2021-01-01', '2019-01-01', '2023-01-01', '2020-01-01', '2022-01-01']),
            'marker':           ['a-mid', 'a-first', 'a-last', 'b-first', 'b-last'],
        })

    def test_first_picks_earliest_per_agent(self):
        result = first_declarations(self.df)
        markers = set(result['marker'])
        self.assertEqual(markers, {'a-first', 'b-first'})

    def test_last_picks_latest_per_agent(self):
        result = last_declarations(self.df)
        markers = set(result['marker'])
        self.assertEqual(markers, {'a-last', 'b-last'})

    def test_one_row_per_agent(self):
        self.assertEqual(len(first_declarations(self.df)), 2)
        self.assertEqual(len(last_declarations(self.df)), 2)


class TestPearsonCoefficient(unittest.TestCase):

    def test_perfect_positive_correlation(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0])
        y = pd.Series([2.0, 4.0, 6.0, 8.0])
        self.assertAlmostEqual(pearson_coefficient(x, y), 1.0)

    def test_perfect_negative_correlation(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0])
        y = pd.Series([8.0, 6.0, 4.0, 2.0])
        self.assertAlmostEqual(pearson_coefficient(x, y), -1.0)

    def test_constant_series_returns_nan(self):
        x = pd.Series([5.0, 5.0, 5.0])
        y = pd.Series([1.0, 2.0, 3.0])
        self.assertTrue(math.isnan(pearson_coefficient(x, y)))

    def test_single_point_returns_nan(self):
        x = pd.Series([1.0])
        y = pd.Series([2.0])
        self.assertTrue(math.isnan(pearson_coefficient(x, y)))


class TestPearsonPValue(unittest.TestCase):

    def test_perfect_correlation_is_significant(self):
        x = pd.Series([1.0, 2.0, 3.0, 4.0, 5.0])
        y = pd.Series([2.0, 4.0, 6.0, 8.0, 10.0])
        self.assertLess(pearson_pvalue(x, y), 0.05)

    def test_constant_series_returns_nan(self):
        x = pd.Series([5.0, 5.0, 5.0])
        y = pd.Series([1.0, 2.0, 3.0])
        self.assertTrue(math.isnan(pearson_pvalue(x, y)))


class TestInterpretCorrelation(unittest.TestCase):

    def test_perfect_positive(self):
        self.assertEqual(interpret_correlation(1.0), 'perfeita positiva')

    def test_strong_positive(self):
        self.assertEqual(interpret_correlation(0.85), 'forte positiva')

    def test_moderate_positive(self):
        self.assertEqual(interpret_correlation(0.6), 'moderada positiva')

    def test_weak_positive(self):
        self.assertEqual(interpret_correlation(0.3), 'fraca positiva')

    def test_tiny_positive(self):
        self.assertEqual(interpret_correlation(0.05), 'ínfima positiva')

    def test_null(self):
        self.assertEqual(interpret_correlation(0.0), 'nula')

    def test_weak_negative(self):
        self.assertEqual(interpret_correlation(-0.3), 'fraca negativa')

    def test_perfect_negative(self):
        self.assertEqual(interpret_correlation(-1.0), 'perfeita negativa')

    def test_boundary_point_one_is_weak(self):
        # 0.1 <= r < 0.5 -> fraca positiva (slide 11 boundary)
        self.assertEqual(interpret_correlation(0.1), 'fraca positiva')

    def test_nan_is_indeterminate(self):
        self.assertEqual(interpret_correlation(float('nan')), 'indeterminada')


class TestPearsonCorrelationsAndStrongest(unittest.TestCase):

    def setUp(self):
        # total_remuneration perfectly tracks stocks; weaker on the others.
        self.df = pd.DataFrame({
            'total_remuneration':    [10.0, 20.0, 30.0, 40.0],
            'assets_in_real_estate': [5.0, 5.0, 6.0, 5.0],
            'assets_in_vehicles':    [1.0, 3.0, 2.0, 4.0],
            'assets_in_stocks':      [100.0, 200.0, 300.0, 400.0],
        })

    def test_returns_all_asset_labels(self):
        correlations = pearson_correlations(self.df)
        self.assertEqual(set(correlations.keys()),
                         {'real estate', 'vehicles', 'stocks'})

    def test_stocks_perfectly_correlated(self):
        correlations = pearson_correlations(self.df)
        self.assertAlmostEqual(correlations['stocks'], 1.0)

    def test_strongest_is_stocks(self):
        correlations = pearson_correlations(self.df)
        label, r = strongest_correlation(correlations)
        self.assertEqual(label, 'stocks')
        self.assertAlmostEqual(r, 1.0)

    def test_strongest_uses_absolute_value(self):
        # vehicles is perfectly negatively correlated (r = -1).
        correlations = {'real estate': 0.5, 'vehicles': -1.0, 'stocks': 0.9}
        label, r = strongest_correlation(correlations)
        self.assertEqual(label, 'vehicles')
        self.assertAlmostEqual(r, -1.0)

    def test_strongest_ignores_nan(self):
        correlations = {'real estate': float('nan'), 'vehicles': 0.3, 'stocks': float('nan')}
        label, r = strongest_correlation(correlations)
        self.assertEqual(label, 'vehicles')

    def test_strongest_all_nan_returns_none(self):
        correlations = {'real estate': float('nan'), 'vehicles': float('nan'), 'stocks': float('nan')}
        label, r = strongest_correlation(correlations)
        self.assertIsNone(label)
        self.assertTrue(math.isnan(r))


class TestCompareCorrelations(unittest.TestCase):

    def test_delta_is_last_minus_first(self):
        corr_first = {'real estate': 0.2, 'vehicles': 0.5, 'stocks': 0.1}
        corr_last = {'real estate': 0.6, 'vehicles': 0.5, 'stocks': -0.4}
        comparison = compare_correlations(corr_first, corr_last)
        self.assertAlmostEqual(comparison['real estate'][2], 0.4)
        self.assertAlmostEqual(comparison['vehicles'][2], 0.0)
        self.assertAlmostEqual(comparison['stocks'][2], -0.5)

    def test_tuple_holds_first_and_last(self):
        corr_first = {'real estate': 0.2, 'vehicles': 0.5, 'stocks': 0.1}
        corr_last = {'real estate': 0.6, 'vehicles': 0.5, 'stocks': -0.4}
        comparison = compare_correlations(corr_first, corr_last)
        r_first, r_last, _ = comparison['stocks']
        self.assertAlmostEqual(r_first, 0.1)
        self.assertAlmostEqual(r_last, -0.4)


if __name__ == '__main__':
    unittest.main(verbosity=2)
