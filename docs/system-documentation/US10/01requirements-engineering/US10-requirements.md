# US10 - Analyse the Evolution of a Political Agent's Income Over a Period

## 1. Requirements Engineering

### 1.1. User Story Description

As a journalist, I want to analyse the evolution of a Political Agent's income over a given period of time (between two dates).

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**
* The system should allow a journalist to select a political agent and a time period (start date and end date) and view how their total income changed across declarations submitted in that period. Only validated declarations should be considered.

**From the client clarifications:**
* None yet.

### 1.3. Acceptance Criteria

* **AC 1:** The journalist must select a valid registered political agent.
* **AC 2:** The start date must be before or equal to the end date.
* **AC 3:** Only validated declarations (status = VALIDATED) within the period are shown.
* **AC 4:** Results are displayed chronologically ordered by declaration submission date.
* **AC 5:** If no validated declarations exist in the period, an appropriate message is shown.

### 1.4. Found out Dependencies

* **US06** - declarations must exist to be analysed.
* **US08** - only validated declarations are included in the analysis.
* **US01/US02** - the journalist must be registered and approved to access this feature.

### 1.5 Input and Output Data

**Input Data:**
* Selectable data:
    * Political Agent name
* Typed/Selectable data:
    * Start date
    * End date

**Output Data:**
* Chronologically ordered list of validated declarations with income evolution (amount, source, institution, date) for each declaration in the period
* (In)Success of the operation
