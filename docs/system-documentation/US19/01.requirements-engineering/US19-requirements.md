# US19 - Extract Entities from CSV to Graph

## 1. Requirements Engineering

### 1.1. User Story Description

As a Product Owner, I want to extract the entities with the information from an imported .csv file.

---

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> A heterogeneous network is a graph that combines different types of vertices (entities, e.g., person, organization, position/job, asset).

> Global Acceptance Criteria: adjacency matrices will be used to store the information of the graph to be visualised, as well as to perform calculations/analyses; .csv files with information about entities and relationships will populate these matrices.

> Entities have different types, where type is in {person (politician, family member, businessman, advisor, ...), organization (company, public institution, political party, NGO, foundation, ...), position (public, business, political party, ...), asset (company, property, fund, ...)}.

**From the client clarifications:**

* User permission restrictions will not be considered for this US.
* The model is based on discrete time snapshots, where each snapshot represents the state of a dynamic graph.
* Entities of the same type must have the same symbol in the graph, distinct from other entity types.

---

### 1.3. Acceptance Criteria

* **AC1:** In the graph drawing, entities of the same type must have the same symbol, which must be different from the symbols of the other entity types.
* **AC2:** Entities have different types, where *type* is in {person (politician, family member, businessman, advisor, ...), organization (company, public institution, political party, NGO, foundation, ...), position (public, business, political party, ...), asset (company, property, fund, ...)}.
* **AC3:** The entity format in the CSV must follow the structure defined per type:
  * person: [id, type, startDate, endDate, name, birthDate, nationality]
  * organization: [id, type, startDate, endDate, name, organizationType, country]
  * position: [id, type, startDate, endDate, positionTitle, positionType, organizationId]
  * asset: [id, type, startDate, endDate, assetType, country, estimatedValue]

---

### 1.4. Found out Dependencies

* **US20** - Drawing relationships between entities also loaded from CSV; depends on US19 entities being available.
* **US21** - Adjacency matrix construction requires the entities extracted in this US.
* **US25/US26** - Integration aspects that use the graph built from US19 entities.

---

### 1.5. Input and Output Data

**Input Data:**

* Selected data:
  * path to the .csv file containing entity data

**Output Data:**

* Collection of extracted Entity objects (Person, Organization, Position, Asset)
* (In)Success of the import operation

---

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US19-SSD.svg)

---

### 1.7. Other Relevant Remarks

* Blank lines and comment lines (starting with `#`) in the CSV are ignored.
* Lines with an unrecognised entity category are silently skipped.
* Each entity is identified uniquely by its `id`; equality between entities is determined solely by `id`.
