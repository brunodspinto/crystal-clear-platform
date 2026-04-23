# US04 - Register an Institution

## 1. Requirements Engineering

### 1.1. User Story Description

As an Administrator, I want to register an Institution of a given type.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**
* "As an Administrator, I want to register an Institution of a given type."
* Institutions represent organizations (companies, political parties, foundations, institutes, or associations) that may be associated with a political agent's positions or business participation.
* The system must ensure data persistence through object serialization.

**From the client clarifications:**
* The type of an institution refers to its legal form (e.g., company, political party, foundation, institute, or association).
* The nature of an institution refers to whether it is public, private, or social.
* The institution type must be selected from a predefined list of available types.

### 1.3. Acceptance Criteria

* **AC 1:** An institution must have a valid designation/name (cannot be null or empty).
* **AC 2:** The system must not allow the registration of duplicate institutions.
* **AC 3:** The type of the institution (e.g., Company, Political Party, Foundation) must be specified during registration.
* **AC 4:** The nature of the institution (PUBLIC, PRIVATE, or SOCIAL) must be specified during registration.

### 1.4. Found out Dependencies

* There are no strict functional dependencies for this US. It is a foundational setup task for the domain model.

### 1.5 Input and Output Data

**Input Data:**
* Typed data:
    * Name
    * Type of Institution
* Selected data:
    * Nature of Institution (PUBLIC, PRIVATE, SOCIAL)

**Output Data:**
* (In)Success of the operation

### 1.6. System Sequence Diagram (SSD)

![US04-SSD](svg/US04-SSD.svg)

### 1.7. Other Relevant Remarks

* The institution type refers to its legal form (e.g., company, political party, foundation, institute, or association).
* The institution nature refers to whether it is public, private, or social.
* Institutions registered through this US will be referenced by Political Agents when submitting Declarations of Interests (see US06), specifically in position entries, subsidy entries, and business participations.
* This US is closely related to **US03**, which allows Political Agents to list all registered institutions grouped by type.