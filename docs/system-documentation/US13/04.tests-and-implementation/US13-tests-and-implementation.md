# US13 - Exploratory Analysis of Declarations by Type, Role, and Institution

## 4. Tests

**Test 1:** Check that `count_by_field` groups declarations correctly by type.

    def test_count_by_type(self):
        result = count_by_field(self.declarations, "declaration_type")
        self.assertEqual(result["Initial"], 2)
        self.assertEqual(result["Regular"], 2)
        self.assertEqual(result["Exceptional"], 1)

**Test 2:** Check that `count_by_field` returns an empty dict for an empty list.

    def test_empty_list(self):
        result = count_by_field([], "declaration_type")
        self.assertEqual(result, {})

**Test 3:** Check that `load_data` returns the correct number of rows.

    def test_load_data_returns_correct_count(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,50000,0,0,0,0",
            "D-002,A-002,Minister,Government,Regular,2023-06-01,80000,5000,0,0,0",
        ])
        data = load_data(path)
        self.assertEqual(len(data), 2)

**Test 4:** Check that `load_data` parses string fields and strips whitespace.

    def test_load_data_parses_string_fields(self):
        path = self._write_csv([
            "D-001, A-001 , MP , Parliament ,Initial,2023-01-01,0,0,0,0,0",
        ])
        data = load_data(path)
        self.assertEqual(data[0]["role"], "MP")
        self.assertEqual(data[0]["institution"], "Parliament")

**Test 5:** Check that `load_data` parses numeric fields correctly.

    def test_load_data_parses_numeric_fields(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,55000.5,3000,100000,20000,50000",
        ])
        data = load_data(path)
        self.assertAlmostEqual(data[0]["gross_salary"], 55000.5)
        self.assertAlmostEqual(data[0]["assets_in_stocks"], 50000.0)

**Test 6:** Check that invalid numeric values default to 0.0.

    def test_load_data_invalid_numeric_defaults_to_zero(self):
        path = self._write_csv([
            "D-001,A-001,MP,Parliament,Initial,2023-01-01,N/A,bad,,0,0",
        ])
        data = load_data(path)
        self.assertEqual(data[0]["gross_salary"], 0.0)
        self.assertEqual(data[0]["side_income"], 0.0)

Run tests with:

    python3 src/test/python/us13/test_declaration_analysis.py -v


## 5. Construction (Implementation)

### Function `load_data`

```python
def load_data(path):
    df = pd.read_csv(path, dtype=str, keep_default_na=False)

    for field in STRING_FIELDS:
        if field in df.columns:
            df[field] = df[field].astype(str).str.strip()

    for field in NUMERIC_FIELDS:
        if field in df.columns:
            df[field] = pd.to_numeric(df[field], errors="coerce").fillna(0.0)
        else:
            df[field] = 0.0

    return df.to_dict(orient="records")
```

### Function `count_by_field`

```python
def count_by_field(declarations, field):
    if not declarations:
        return {}
    series = pd.Series([decl[field] for decl in declarations])
    return series.value_counts().to_dict()
```

### Function `plot_type_distribution`

Produces a pie chart of declaration type proportions (Initial, Regular, Exceptional) and saves it as SVG using matplotlib.

```python
def plot_type_distribution(declarations, output_path):
    df = pd.DataFrame(declarations)
    counts = df["declaration_type"].value_counts().sort_index()
    fig, ax = plt.subplots(figsize=(6, 6))
    ax.pie(counts.values, labels=counts.index, autopct="%1.1f%%",
           startangle=90, shadow=True, colors=sns.color_palette("muted"))
    ax.set_title("Distribution of Declarations by Type")
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
```

### Function `plot_by_role_and_institution`

Produces two side-by-side bar charts: total declarations by role, and total declarations by institution. Saved as a single SVG using seaborn and matplotlib.

```python
def plot_by_role_and_institution(declarations, output_path):
    df = pd.DataFrame(declarations)
    role_counts = df["role"].value_counts().sort_index()
    inst_counts = df["institution"].value_counts().sort_index()
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))
    sns.barplot(x=role_counts.index, y=role_counts.values, color="steelblue", ax=ax1)
    sns.barplot(x=inst_counts.index, y=inst_counts.values, color="darkorange", ax=ax2)
    fig.tight_layout()
    fig.savefig(output_path, format="svg", bbox_inches="tight")
    plt.close(fig)
```


## 6. Integration and Demo

* The script is run directly: `python3 src/main/python/us13/declaration_analysis.py`
* It reads `src/test/resources/us13/sample_declarations.csv` by default and prints frequency counts to stdout.
* Two SVG charts are saved to `docs/system-documentation/US13/`:
  * `us13_chart1_type_distribution.svg`: pie chart by declaration type
  * `us13_chart2_role_institution.svg`: bar charts by role and institution


## 7. Observations

n/a
