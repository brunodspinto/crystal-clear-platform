# US04 - Register an Organization

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

To fulfill this requirement, the core concept added to the domain model is `Organization`. The Administrator is the actor responsible for creating these organizations within the platform.

An `Organization` represents a broad set of entity types (company, political party, foundation, institute, or association) that may be associated with political agents' positions, business participations, or subsidy entries. It captures three fundamental attributes:

* **name:** A unique identifier for the organization within its type.
* **nature:** The legal nature of the organization, selected from the predefined `OrganizationNature` enum with values `PUBLIC`, `PRIVATE`, and `SOCIAL` (AC4). It complements the formal type with information about the organization's legal standing.
* **type:** The legal form of the organization, represented as the `OrganizationType` enum with values `COMPANY`, `POLITICAL_PARTY`, `FOUNDATION`, `INSTITUTE`, and `ASSOCIATION`. The type is selected from a predefined list (AC2), ensuring only valid values are stored.

Using enums for `OrganizationType` and `OrganizationNature` rather than free-text strings prevents invalid entries, ensures consistent grouping in US03, and aligns with the predefined lists defined in the specifications.

![US04-DM](svg/US04-DM.svg)

### 2.2. Other Remarks

* The `OrganizationType` enum is shared across US03 (listing), US04 (registration), and US06 (declaration submission), ensuring consistency throughout the domain model.
* The `OrganizationNature` enum (`PUBLIC`, `PRIVATE`, `SOCIAL`) is selected by the Administrator at registration time and the enum value itself is stored in the `Organization` (the `nature` attribute is of type `OrganizationNature`, not a String). This mirrors the `PositionNature` enum already used for declaration positions.
