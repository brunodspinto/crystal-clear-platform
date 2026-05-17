# US19 - Load Entities from CSV File

## 4. Tests

**Test 1:** Check that loading a valid CSV file returns the correct number of entities.

```java
@Test
void ensureLoadEntitiesReturnsCorrectCount() throws IOException {
    int count = controller.loadEntities(resourcePath("graph/entities_sample.csv"));
    assertEquals(12, count);
}
```

**Test 2:** Check that entities are stored in the repository after loading.

```java
@Test
void ensureLoadEntitiesStoresEntitiesInRepository() throws IOException {
    controller.loadEntities(resourcePath("graph/entities_sample.csv"));
    assertEquals(12, repo.getAll().size());
}
```

**Test 3:** Check that an invalid file path throws IOException.

```java
@Test
void ensureLoadEntitiesOnInvalidPathThrowsIOException() {
    assertThrows(IOException.class, () ->
            controller.loadEntities("nonexistent/path/file.csv"));
}
```

**Test 4:** Check that a Person entity is parsed correctly from CSV.

```java
@Test
void ensurePersonsAreParsedCorrectly() throws IOException {
    List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
    Person person = (Person) entities.stream()
            .filter(e -> e.getId().equals("P-001"))
            .findFirst()
            .orElseThrow();
    assertEquals("politician", person.getType());
    assertEquals("António Silva", person.getName());
    assertEquals("Portuguese", person.getNationality());
}
```

**Test 5:** Check that blank lines and comments in the CSV file are ignored.

```java
@Test
void ensureCommentsAndBlankLinesAreIgnored() throws IOException {
    List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
    assertTrue(entities.stream().noneMatch(e -> e.getId().startsWith("#")));
}
```

**Test 6:** Check that it is not possible to create an Entity with a blank id.

```java
@Test
void ensureBlankIdIsNotAllowed() {
    assertThrows(IllegalArgumentException.class, () ->
            new Person("", "politician", "2020-01-01", "2024-01-01", "Name", "1990-01-01", "Portuguese"));
}
```


## 5. Construction (Implementation)

### Class Entity

```java
public abstract class Entity {

    private final String id;
    private final String type;
    private final String startDate;
    private final String endDate;

    protected Entity(String id, String type, String startDate, String endDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        this.id = id;
        this.type = type;
        this.startDate = startDate == null ? "" : startDate;
        this.endDate = endDate == null ? "" : endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    public abstract String getDetails();
}
```

### Class EntityCsvParser

```java
public static List<Entity> parse(String filePath) throws IOException {
    List<Entity> entities = new ArrayList<>();
    ArrayList<String> dataLines = readDataLines(filePath);
    if (dataLines.isEmpty()) return entities;

    char separator = detectSeparator(dataLines.get(0));
    int start = looksLikeHeader(dataLines.get(0), separator) ? 1 : 0;
    for (int i = start; i < dataLines.size(); i++) {
        Entity entity = parseLine(dataLines.get(i), separator);
        if (entity != null) {
            entities.add(entity);
        }
    }
    return entities;
}
```

### Class LoadEntitiesFromCsvController

```java
public int loadEntities(String filePath) throws IOException {
    List<Entity> entities = EntityCsvParser.parse(filePath);
    graphRepository.addAll(entities);
    return entities.size();
}
```


## 6. Integration and Demo

* A new option was added to the main menu to load entities from a CSV file.

* After loading, the user is offered the option to render the entity graph to an SVG file using Graphviz.

* For demo purposes, a sample CSV file is provided at `src/test/resources/graph/entities_sample.csv`.


## 7. Observations

n/a
