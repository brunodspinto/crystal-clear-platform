# US09 - Consult the Integrated Situation of a Political Agent on a Given Date

## 1. Requirements Engineering

### 1.1. User Story Description

As a member of the Ethics Committee, I want to consult the Integrated Situation of a Political Agent on a given date.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**
* The system should allow a member of the Ethics Committee to select a political agent and a specific date, and view their integrated situation on that date, including all active positions, income sources, assets, and business participations declared up to that date.

**From the client clarifications:**
* None yet.

### 1.3. Acceptance Criteria

* **AC 1:** The Ethics Committee member must select a valid registered political agent.
* **AC 2:** Only validated declarations (status = VALIDATED) up to the specified date are considered.
* **AC 3:** If no validated declarations exist up to the specified date, an appropriate message is shown.

### 1.4. Found out Dependencies

* **US06** - declarations must exist to be consulted.
* **US08** - only validated declarations are included.
* **US01/US02** - the Ethics Committee member must be registered and approved to access this feature.

### 1.5 Input and Output Data

**Input Data:**
* Selectable data:
    * Political Agent name
* Typed/Selectable data:
    * Reference date

**Output Data:**
* Integrated situation of the political agent on that date (positions, income, assets, business participations)
* (In)Success of the operation
