# US04 - Register an Organization

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

To fulfill this requirement, the core concept added to the domain model is `Organization`. The Administrator is the actor responsible for creating these organizations within the platform.

An `Organization` represents a broad set of entity types (company, political party, foundation, institute, or association) that may be associated with political agents' positions, business participations, or subsidy entries. It captures two fundamental attributes:

* **name:** A unique identifier for the organization within its type.
* **type:** The legal form of the organization, represented as the `OrganizationType` enum with values `company`, `politicalParty`, `foundation`, `institute`, and `association`. The type is selected from a predefined list (AC2), ensuring only valid values are stored.

Using an enum for `OrganizationType` rather than a free-text string prevents invalid entries, ensures consistent grouping in US03, and aligns with the predefined list defined in the specifications.

The system relies on object serialization to persist the registered organizations, allowing them to be referenced later by Political Agents when submitting their declarations.

![US04-DM](svg/US04-DM.svg)

### 2.2. Other Remarks

* The `OrganizationType` enum is shared across US03 (listing), US04 (registration), and US06 (declaration submission), ensuring consistency throughout the domain model.
