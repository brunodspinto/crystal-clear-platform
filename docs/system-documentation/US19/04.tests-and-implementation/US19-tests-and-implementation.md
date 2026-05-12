# US19 - Tests and Implementation

## Implementation

### Classes

| Class | Path |
|-------|------|
| `Entity` | `src/main/java/pt/ipp/isep/dei/domain/graph/Entity.java` |
| `Person` | `src/main/java/pt/ipp/isep/dei/domain/graph/Person.java` |
| `Organization` | `src/main/java/pt/ipp/isep/dei/domain/graph/Organization.java` |
| `Position` | `src/main/java/pt/ipp/isep/dei/domain/graph/Position.java` |
| `Asset` | `src/main/java/pt/ipp/isep/dei/domain/graph/Asset.java` |
| `Edge` | `src/main/java/pt/ipp/isep/dei/domain/graph/Edge.java` |
| `RelationGraph` | `src/main/java/pt/ipp/isep/dei/domain/graph/RelationGraph.java` |
| `GraphBuilder` | `src/main/java/pt/ipp/isep/dei/domain/graph/GraphBuilder.java` |
| `EntityCsvParser` | `src/main/java/pt/ipp/isep/dei/domain/graph/EntityCsvParser.java` |
| `RelationCsvParser` | `src/main/java/pt/ipp/isep/dei/domain/graph/RelationCsvParser.java` |
| `IndexRegistry` | `src/main/java/pt/ipp/isep/dei/domain/graph/IndexRegistry.java` |
| `AdjacencyMatrix` | `src/main/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrix.java` |

**`Entity`**: abstract base class with fields `id`, `type`, `startDate`, `endDate`. Equality uses `getClass()` so that a `Person` and an `Asset` with the same id are never considered equal.

**`Person`, `Organization`, `Position`, `Asset`**: concrete subclasses of `Entity`, each adding type-specific fields.

**`Edge`**: immutable relation between two entity ids, with `label` and `weight`. Validates that all string fields are non-blank on construction.

**`RelationGraph`**: directed graph backed by parallel `ArrayList`s (nodeIds + outgoing edges). Supports `addNode()`, `addEdge()`, `neighbors()`, and conversion to `AdjacencyMatrix`.

**`GraphBuilder`**: static factory that registers all entities as nodes and then adds all edges into a `RelationGraph`.

**`EntityCsvParser`**: reads a CSV file and produces a `List<Entity>`. Format per line: `category;id;type;startDate;endDate;...`. Skips blank lines, comment lines, and malformed lines.

**`RelationCsvParser`**: reads a CSV file and produces a `List<Edge>`. Format: `from_id;to_id;label;weight`. Skips malformed lines.

**`IndexRegistry`**: maps entity ids to integer indices for use in adjacency matrices.

**`AdjacencyMatrix`**: square matrix of `double` values representing edge weights.

## Tests

- `src/test/java/pt/ipp/isep/dei/domain/graph/EntityTest.java` — 3 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/PersonTest.java` — 4 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/OrganizationTest.java` — 4 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/PositionTest.java` — 4 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/AssetTest.java` — 4 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/EdgeTest.java` — 3 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/RelationGraphTest.java` — 11 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/GraphBuilderTest.java` — 7 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/EntityCsvParserTest.java` — 7 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/RelationCsvParserTest.java` — 9 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/IndexRegistryTest.java` — 8 tests
- `src/test/java/pt/ipp/isep/dei/domain/graph/AdjacencyMatrixTest.java` — 9 tests

Total: 73 tests (all passing)

## Checklist

- [x] `Entity` abstract class with id/type/startDate/endDate and getClass()-based equality
- [x] `Person`, `Organization`, `Position`, `Asset` concrete subclasses
- [x] `Edge` immutable value object with blank-field validation
- [x] `RelationGraph` with parallel ArrayList structure
- [x] `GraphBuilder` static factory
- [x] `EntityCsvParser` with category dispatch and malformed-line skipping
- [x] `RelationCsvParser` with weight parsing and malformed-line skipping
- [x] `IndexRegistry` for id-to-index mapping
- [x] `AdjacencyMatrix` square double matrix
- [x] 73 unit tests (all passing)
