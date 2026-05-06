# US24 - Export Declaration Dataset to CSV

## 4. Tests

**Test 1:** Check that an empty list of declarations produces a CSV with only the header row — AC8.

    @Test
    void ensureExportWithNoDeclarationsWritesHeaderOnly() throws IOException {
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(new ArrayList<>(), out.getAbsolutePath());
        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).startsWith("agent_id"));
    }

**Test 2:** Check that each validated declaration produces exactly one data row — AC2.

    @Test
    void ensureExportWritesOneRowPerDeclaration() throws IOException {
        List<Declaration> declarations = List.of(createTestDeclaration(), createTestDeclaration());
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(declarations, out.getAbsolutePath());
        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals(3, lines.size()); // header + 2 data rows
    }

**Test 3:** Check that the declaration date is formatted as yyyy-MM-dd — AC3.

    @Test
    void ensureDeclarationDateIsFormattedCorrectly() throws IOException {
        Declaration declaration = createTestDeclaration();
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(List.of(declaration), out.getAbsolutePath());
        String dataRow = Files.readAllLines(out.toPath()).get(1);
        assertTrue(dataRow.matches(".*,\\d{4}-\\d{2}-\\d{2},.*"));
    }

**Test 4:** Check that gross salary is the sum of all position entries — AC4.

    @Test
    void ensureGrossSalaryIsSummedAcrossPositionEntries() throws IOException {
        // declaration with two position entries: grossSalary 10000 + 5000
        Declaration declaration = createTestDeclarationWithTwoPositions(10000.0, 5000.0, 0.0, 0.0, 0.0, 0.0);
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(List.of(declaration), out.getAbsolutePath());
        String dataRow = Files.readAllLines(out.toPath()).get(1);
        assertTrue(dataRow.contains("15000.0"));
    }

**Test 4b:** Check that side income consulting and board memberships are summed separately — AC5/AC5b.

    @Test
    void ensureSideIncomeColumnsAreSummedSeparately() throws IOException {
        // two position entries: consulting 3000+2000=5000, board 1000+500=1500
        Declaration declaration = createTestDeclarationWithTwoPositions(0.0, 0.0, 3000.0, 2000.0, 1000.0, 500.0);
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(List.of(declaration), out.getAbsolutePath());
        String dataRow = Files.readAllLines(out.toPath()).get(1);
        assertTrue(dataRow.contains("5000.0") && dataRow.contains("1500.0"));
    }

**Test 5:** Check that asset columns contain the correct sums per category — AC6.

    @Test
    void ensureAssetColumnsAreSummedByCategory() throws IOException {
        Declaration declaration = createTestDeclarationWithAssets(50000.0, 20000.0, 10000.0);
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(List.of(declaration), out.getAbsolutePath());
        String dataRow = Files.readAllLines(out.toPath()).get(1);
        assertTrue(dataRow.contains("50000.0") && dataRow.contains("20000.0") && dataRow.contains("10000.0"));
    }

**Test 6:** Check that the header row contains all required column names in the correct order — AC8.

    @Test
    void ensureHeaderContainsAllRequiredColumns() throws IOException {
        File out = File.createTempFile("declarations", ".csv");
        DeclarationCsvExporter.export(new ArrayList<>(), out.getAbsolutePath());
        String header = Files.readAllLines(out.toPath()).get(0);
        assertEquals("agent_id,role,declaration_type,declaration_date,declaration_id," +
                "institution,gross_salary,side_income_consulting," +
                "side_income_board_memberships,assets_in_real_estate," +
                "assets_in_vehicles,assets_in_stocks", header);
    }


## 5. Construction (Implementation)

### Class DeclarationCsvExporter

```java
public class DeclarationCsvExporter {

    private static final String HEADER =
            "agent_id,role,declaration_type,declaration_date,declaration_id," +
            "institution,gross_salary,side_income_consulting," +
            "side_income_board_memberships,assets_in_real_estate," +
            "assets_in_vehicles,assets_in_stocks";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DeclarationCsvExporter() {}

    public static boolean export(List<Declaration> declarations, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(HEADER);
            for (Declaration d : declarations) {
                writer.println(toCsvRow(d));
            }
        }
        return true;
    }

    private static String toCsvRow(Declaration d) {
        String role = "";
        String institution = "";
        double grossSalary = 0.0;
        double sideIncomeConsulting = 0.0;
        double sideIncomeBoardMemberships = 0.0;
        List<PositionEntry> positions = d.getPositionEntries();
        if (!positions.isEmpty()) {
            role = positions.get(0).getFunctionDesignation();
            institution = positions.get(0).getOrganization().getName();
            for (PositionEntry p : positions) {
                grossSalary += p.getGrossSalary();
                sideIncomeConsulting += p.getSideIncomeConsulting();
                sideIncomeBoardMemberships += p.getSideIncomeBoardMemberships();
            }
        }
        double realEstate = 0.0, vehicles = 0.0, stocks = 0.0;
        for (AssetEntry a : d.getAssetEntries()) {
            switch (a.getAssetType()) {
                case REAL_ESTATE: realEstate += a.getAssetValue(); break;
                case VEHICLES:    vehicles   += a.getAssetValue(); break;
                case STOCKS:      stocks     += a.getAssetValue(); break;
            }
        }
        String date = d.getSubmissionDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
        return String.join(",",
                d.getAgent().getTaxIdentificationNumber(),
                role,
                d.getType().toString().toLowerCase(),
                date,
                d.getId().toString(),
                institution,
                String.valueOf(grossSalary),
                String.valueOf(sideIncomeConsulting),
                String.valueOf(sideIncomeBoardMemberships),
                String.valueOf(realEstate),
                String.valueOf(vehicles),
                String.valueOf(stocks));
    }
}
```

### Class ExportDeclarationCsvController

```java
public boolean exportToCsv(String filePath) {
    List<Declaration> validated = declarationRepository
            .getDeclarationsByStatus(DeclarationStatus.VALIDATED);
    try {
        return DeclarationCsvExporter.export(validated, filePath);
    } catch (IOException e) {
        return false;
    }
}
```

### Declaration (UUID id field)

A `UUID id` field is added to `Declaration`, generated at construction time:

```java
private final UUID id = UUID.randomUUID();

public UUID getId() { 
    return id; 
}
```


## 6. Integration and Demo

* A option **"Export Declaration (CSV)"** was added to the Product Owner menu.
* For demo purposes, at least one validated declaration is bootstrapped so the export can be demonstrated immediately.
* The exported file is saved to a path typed by the user (e.g. `declarations.csv`).


## 7. Observations

* File I/O operations are not unit-tested as per the project's non-functional requirements. The `DeclarationCsvExporter.export()` method is tested through integration-style tests that write to a temp file and read it back.
* The `role` and `institution` columns are taken from the first position entry. Declarations with no position entries will have empty strings in those columns.
