# US19 - Extract Entities from CSV to Graph

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

To fulfil this requirement, the core concept is an `Entity` hierarchy representing the different node types in the heterogeneous graph.

`Entity` is an abstract class that captures the fields common to all entity types: `id`, `type`, `startDate`, and `endDate`. Equality between entities is determined solely by `id`, making it the natural key for deduplication within the graph.

Four concrete subclasses extend `Entity`, each adding type-specific attributes:

* **Person** — represents a human actor (politician, businessman, family member, advisor, ...):
  * `name`, `birthDate`, `nationality`

* **Organization** — represents a legal or institutional body (company, NGO, political party, ...):
  * `name`, `organizationType`, `country`

* **Position** — represents a role or job held within an organisation (public, business, political party, ...):
  * `positionTitle`, `positionType`, `organizationId`

* **Asset** — represents a declared asset (property, fund, company share, ...):
  * `assetType`, `country`, `estimatedValue`

The `EntityCsvParser` is a utility class responsible for reading a CSV file and producing a `List<Entity>`. Each line in the CSV begins with the entity category (`person`, `organization`, `position`, or `asset`), followed by the seven fields defined in the specification.

Business rules:
* The `id` field must not be blank; an `IllegalArgumentException` is thrown otherwise.
* The `type` field must not be blank; an `IllegalArgumentException` is thrown otherwise.
* All other String fields default to an empty string when `null` is supplied.
* Lines with unknown categories or insufficient fields are silently skipped.

![Domain Model Excerpt](svg/US19-DM.svg)

---

### 2.2. Other Remarks

* The entity category prefix in the CSV (e.g., `person`) is separate from the `type` field (e.g., `politician`), allowing the parser to instantiate the correct subclass without ambiguity.
* `EntityCsvParser` is a stateless utility; it exposes a single static `parse(filePath)` method.
* This model feeds directly into US20 (relationships) and US21 (adjacency matrices).
