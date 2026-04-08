# US03 - List Institutions

## 1. Requirements Engineering

### 1.1. User Story Description

As a Political Agent, I want to list Institutions (Companies, Political Parties, Foundations, Institutes, Associations).

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> Institutions represent organizations that may be associated with a political agent's positions, business participations, or asset declarations. Institution types include: Company, Political Party, Foundation, Institute, and Association.

> The institutions should be grouped by type and then listed alphabetically by name.

**From the client clarifications:**

* The list must include all institutions registered in the system, regardless of any association with the logged-in Political Agent.
* Only institutions that have been registered in the system should be displayed.
* The type of an institution refers to the activity sector or nature of the organization (e.g., civil construction, healthcare, finance), not the legal form such as political party or foundation.

### 1.3. Acceptance Criteria

* **AC1:** The institutions must be grouped by type (activity sector/nature of the organization) and then listed alphabetically by name within each group.
* **AC2:** Within each type group, institutions must be listed alphabetically by name.
* **AC3:** If no institutions are registered for a given type, that type group may be omitted or shown as empty.
* **AC4:** Only authenticated Political Agents may access this listing.

### 1.4. Found out Dependencies

* There is a dependency on **US04 - Register an Institution**, as institutions must first be registered before they can be listed.

### 1.5. Input and Output Data

**Input Data:**

* None (the listing is triggered by user action with no additional input required).

**Output Data:**

* List of all registered institutions, grouped by type and ordered alphabetically by name within each group.

### 1.6. System Sequence Diagram (SSD)

![US003-SSD](svg/US03-SSD.svg)

**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks

* This US is read-only. No data is created, modified, or deleted.
* The institution type (used for grouping) refers to the activity sector of the organization, not its legal form. This is consistent with US04, where the type is selected from a predefined list of activity sectors.
* Institutions are referenced in Declarations of Interests submitted by Political Agents (professional positions, business participations), which justifies the need for this listing.
