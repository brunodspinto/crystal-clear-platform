import sys
import os
import tempfile
import unittest

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                "..", "..", "..", "main", "python", "us13"))

from declaration_analysis import count_by_field, load_data


class TestCountByField(unittest.TestCase):

    def setUp(self):
        self.declarations = [
            {"declaration_type": "Initial", "role": "MP", "institution": "Parliament"},
            {"declaration_type": "Regular", "role": "MP", "institution": "Parliament"},
            {"declaration_type": "Regular", "role": "Minister", "institution": "Government"},
            {"declaration_type": "Exceptional", "role": "Judge", "institution": "Courts"},
            {"declaration_type": "Initial", "role": "Judge", "institution": "Courts"},
        ]

    def test_count_by_type(self):
        result = count_by_field(self.declarations, "declaration_type")
        self.assertEqual(result["Initial"], 2)
        self.assertEqual(result["Regular"], 2)
        self.assertEqual(result["Exceptional"], 1)

    def test_count_by_role(self):
        result = count_by_field(self.declarations, "role")
        self.assertEqual(result["MP"], 2)
        self.assertEqual(result["Minister"], 1)
        self.assertEqual(result["Judge"], 2)

    def test_count_by_institution(self):
        result = count_by_field(self.declarations, "institution")
        self.assertEqual(result["Parliament"], 2)
        self.assertEqual(result["Government"], 1)
        self.assertEqual(result["Courts"], 2)

    def test_empty_list(self):
        result = count_by_field([], "declaration_type")
        self.assertEqual(result, {})

    def test_single_entry(self):
        result = count_by_field([{"role": "MP"}], "role")
        self.assertEqual(result, {"MP": 1})

    def test_all_same_value(self):
        data = [{"role": "MP"}, {"role": "MP"}, {"role": "MP"}]
        result = count_by_field(data, "role")
        self.assertEqual(result, {"MP": 3})


class TestLoadData(unittest.TestCase):

    CSV_HEADER = "declaration_id,agent_id,role,institution,declaration_type,declaration_date,gross_salary,side_income,assets_in_real_estate,assets_in_vehicles,assets_in_stocks\n"

    def _write_csv(self, rows):
        f = tempfile.NamedTemporaryFile(mode="w", suffix=".csv", delete=False)
        f.write(self.CSV_HEADER)
        for row in rows:
            f.write(row + "\n")
        f.close()
        return f.name

    def test_load_data_returns_correct_count(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,50000,0,0,0,0",
            "D-002,A-002,Minister,Government,Regular,2023-06-01,80000,5000,0,0,0",
        ])
        data = load_data(path)
        self.assertEqual(len(data), 2)

    def test_load_data_parses_string_fields(self):
        path = self._write_csv([
            "D-001, A-001 , MP , Parliament ,Initial,2023-01-01,0,0,0,0,0",
        ])
        data = load_data(path)
        self.assertEqual(data[0]["declaration_id"], "D-001")
        self.assertEqual(data[0]["role"], "MP")
        self.assertEqual(data[0]["institution"], "Parliament")

    def test_load_data_parses_numeric_fields(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,55000.5,3000,100000,20000,50000",
        ])
        data = load_data(path)
        self.assertAlmostEqual(data[0]["gross_salary"], 55000.5)
        self.assertAlmostEqual(data[0]["side_income"], 3000.0)
        self.assertAlmostEqual(data[0]["assets_in_stocks"], 50000.0)

    def test_load_data_invalid_numeric_defaults_to_zero(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,N/A,bad,,0,0",
        ])
        data = load_data(path)
        self.assertEqual(data[0]["gross_salary"], 0.0)
        self.assertEqual(data[0]["side_income"], 0.0)

    def test_load_data_empty_file_returns_empty_list(self):
        path = self._write_csv([])
        data = load_data(path)
        self.assertEqual(data, [])


if __name__ == "__main__":
    unittest.main()
