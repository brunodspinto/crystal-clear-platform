# US25 - Export Holdings Dataset to CSV

## 4. Tests

**Test 1:** Check that an empty list of declarations produces a CSV with only the header row.

    @Test
    void ensureEmptyListProducesHeaderOnly() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        HoldingsCsvExporter.export(Collections.emptyList(), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertEquals("agent_id,company_NIF,total_value_in_stocks,company_percentage,declaration_date",
                lines.get(0));
    }

**Test 2:** Check that a declaration with no business participations produces no data rows.

    @Test
    void ensureDeclarationWithNoParticipationsProducesHeaderOnly() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("111111111");
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
    }

**Test 3:** Check that one business participation produces one data row with correct values.

    @Test
    void ensureOneParticipationProducesOneDataRow() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("222222222");
        d.addBusinessParticipation(createOrg("TechCorp"), 500000001L, 15000.0, 10.5);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).startsWith("222222222,500000001,"));
        assertTrue(lines.get(1).contains("15000.0"));
        assertTrue(lines.get(1).contains("10.5"));
    }

**Test 4:** Check that two participations in one declaration produce two data rows.

    @Test
    void ensureTwoParticipationsProduceTwoDataRows() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("333333333");
        d.addBusinessParticipation(createOrg("Alpha"), 100000001L, 5000.0, 5.0);
        d.addBusinessParticipation(createOrg("Beta"), 200000002L, 8000.0, 3.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(3, lines.size());
    }

**Test 5:** Check that multiple declarations with multiple participations produce the correct total row count.

    @Test
    void ensureMultipleDeclarationsProduceCorrectRowCount() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d1 = createValidatedDeclaration("444444444");
        d1.addBusinessParticipation(createOrg("Corp1"), 111111111L, 1000.0, 1.0);
        Declaration d2 = createValidatedDeclaration("555555555");
        d2.addBusinessParticipation(createOrg("Corp2"), 222222222L, 2000.0, 2.0);
        d2.addBusinessParticipation(createOrg("Corp3"), 333333333L, 3000.0, 3.0);
        HoldingsCsvExporter.export(Arrays.asList(d1, d2), out.toString());
        List<String> lines = Files.readAllLines(out);
        assertEquals(4, lines.size()); // header + 3 rows
    }

**Test 6:** Check that `export()` returns `true` on success.

    @Test
    void ensureExportReturnsTrueOnSuccess() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        boolean result = HoldingsCsvExporter.export(Collections.emptyList(), out.toString());
        assertTrue(result);
    }

**Test 7:** Check that the `declaration_date` column is formatted as `yyyy-MM-dd`.

    @Test
    void ensureDateColumnIsInYyyyMmDdFormat() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("777777777");
        d.addBusinessParticipation(createOrg("DateCorp"), 123456789L, 1000.0, 1.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        String row = Files.readAllLines(out).get(1);
        String[] cols = row.split(",");
        String date = cols[cols.length - 1];
        assertTrue(Pattern.matches("\\d{4}-\\d{2}-\\d{2}", date));
    }

**Test 8:** Check that `agent_id` and `company_NIF` are correctly placed in the row.

    @Test
    void ensureAgentIdAndNifAreCorrectInRow() throws IOException {
        Path out = tempDir.resolve("holdings.csv");
        Declaration d = createValidatedDeclaration("999999999");
        d.addBusinessParticipation(createOrg("MegaCorp"), 987654321L, 50000.0, 25.0);
        HoldingsCsvExporter.export(Collections.singletonList(d), out.toString());
        String row = Files.readAllLines(out).get(1);
        assertTrue(row.startsWith("999999999,987654321,"));
        assertTrue(row.contains("50000.0"));
        assertTrue(row.contains("25.0"));
    }


## 5. Construction (Implementation)

### Class HoldingsCsvExporter

```java
public class HoldingsCsvExporter {

    private static final String HEADER =
            "agent_id,company_NIF,total_value_in_stocks,company_percentage,declaration_date";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private HoldingsCsvExporter() {}

    public static boolean export(List<Declaration> declarations, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(HEADER);
            for (Declaration d : declarations) {
                for (BusinessParticipation bp : d.getBusinessParticipations()) {
                    writer.println(toCsvRow(d, bp));
                }
            }
        }
        return true;
    }

    private static String toCsvRow(Declaration d, BusinessParticipation bp) {
        String agentId = d.getAgent().getTaxIdentificationNumber();
        String date = d.getSubmissionDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FMT);
        return String.join(",",
                agentId,
                String.valueOf(bp.getCompanyNIF()),
                String.valueOf(bp.getTotalValueInStocks()),
                String.valueOf(bp.getCompanyPercentage()),
                date);
    }
}
```

### Class ExportHoldingsCsvController

```java
public boolean exportToCsv(String filePath) {
    List<Declaration> validated = declarationRepository
            .getDeclarationsByStatus(DeclarationStatus.VALIDATED);
    try {
        return HoldingsCsvExporter.export(validated, filePath);
    } catch (IOException e) {
        return false;
    }
}
```


## 6. Integration and Demo

* An option **"Export Holdings (CSV)"** was added to the Product Owner menu.
* For demo purposes, at least one validated declaration with business participations is bootstrapped so the export can be demonstrated immediately.
* The exported file is saved to a path typed by the user (e.g. `holdings.csv`).


## 7. Observations

* One CSV row is produced per (declaration × business participation) pair; declarations with no business participations contribute zero rows.
* The `declaration_date` is derived from the declaration's submission date, formatted as `yyyy-MM-dd`.
