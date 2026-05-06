import sys
import os
import unittest

sys.path.insert(0, os.path.join(os.path.dirname(__file__),
                                "..", "..", "..", "main", "python", "us13"))

from declaration_analysis import count_by_field


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


if __name__ == "__main__":
    unittest.main()
