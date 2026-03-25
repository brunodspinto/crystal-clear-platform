# US07 - Consult a previously submitted Declaration of Interests

## 1. Requirements Engineering

### 1.1. User Story Description

As a Political Agent, I want to consult a previously submitted Declaration of Interests.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**
* "As a Political Agent, I want to consult a previously submitted Declaration of Interests."
* Declarations of interest include information regarding: professional positions, support and subsidies received, assets (real estate), and quotas, shares, and holdings in companies.

**From the client clarifications:**
* (Insert any specific clarifications given by the product owner/teachers in the forum here, if applicable. Otherwise, leave as "None yet.")

### 1.3. Acceptance Criteria

* **AC 1:** The system must only allow a Political Agent to consult their own declarations of interests.
* **AC 2:** The system must present all the information associated with the selected declaration (income, positions, assets, and business participations) in a read-only format.

### 1.4. Found out Dependencies

* **US06:** "As a Political Agent, I want to submit a declaration of interests...". There is a strict functional dependency here. A Political Agent can only consult a declaration if at least one has been previously submitted.

### 1.5 Input and Output Data

**Input Data:**
* Selected data:
    * The specific declaration of interests to consult (from a provided list).

**Output Data:**
* List of previously submitted declarations (for selection).
* The detailed information of the selected Declaration of Interests.