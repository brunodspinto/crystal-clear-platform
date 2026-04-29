# US19 - Tests and Implementation

## Implementation

### Domain Classes

- `src/main/java/pt/ipp/isep/dei/domain/graph/Entity.java` — abstract base class; fields: id, type, startDate, endDate; equals/hashCode by id
- `src/main/java/pt/ipp/isep/dei/domain/graph/Person.java` — extends Entity; fields: name, birthDate, nationality
- `src/main/java/pt/ipp/isep/dei/domain/graph/Organization.java` — extends Entity; fields: name, organizationType, country
- `src/main/java/pt/ipp/isep/dei/domain/graph/Position.java` — extends Entity; fields: positionTitle, positionType, organizationId

### Tests

- `src/test/java/pt/ipp/isep/dei/domain/graph/EntityTest.java`

| Test | Description |
|------|-------------|
| `ensureFieldsAreKept` | Constructor stores id, type, startDate, endDate correctly |
| `ensureBlankIdsAreRejected` | Blank or null id throws `IllegalArgumentException` |
| `ensureEqualityIsByIdOnly` | Two entities with the same id are equal regardless of other fields |

## Checklist

- [x] `Entity` abstract class
- [x] `Person`, `Organization`, `Position` — skeleton subclasses
- [x] `EntityTest` — 3 unit tests (JUnit 5)
- [ ] `Asset` — skeleton subclass
- [ ] `CsvEntityExtractor` — reads declarations CSV and produces `Entity` instances
- [ ] `EntityCollection` — stores entities, populates `AdjacencyMatrix`
- [ ] Coverage ≥ 90% on `domain/graph`
