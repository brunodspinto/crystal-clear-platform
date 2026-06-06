# US12 - Make a Complaint About a Political Agent

## 1. Requirements Engineering

### 1.1. User Story Description

As a citizen, I want to make a complaint about a political agent in a specific role and on a specific date.

A single complaint targets one political agent but may aggregate several grievances about that same agent. Each grievance describes a behaviour with its own description, date and the political function the agent held at that time. A complaint about a different political agent is a different complaint.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The platform may be used by Ordinary Citizens — persons who have an interest in scrutinising political agents and who can use the platform to report behaviour and processes that they consider to be lacking in transparency.

> The platform supports the analysis of Declarations of Interests submitted by political agents. Its main goal is to detect irregularities such as conflicts of interest and cases of illicit enrichment.
 
### 1.3. Acceptance Criteria

* **AC1:** The citizen must select a registered political agent from the system.
* **AC2:** For each grievance, the political function (role) held by the agent must be selected from the predefined list of political functions.
* **AC3:** Each grievance date must be a valid past or present date; future dates are not allowed.
* **AC4:** A non-empty description must be provided for each grievance.
* **AC5:** The complaint must be associated with the authenticated citizen and the submission date is recorded automatically by the system.
* **AC6:** A complaint may aggregate one or more grievances; after a grievance is confirmed, the citizen is asked whether to add another grievance to the same complaint.
* **AC7:** All grievances of a complaint refer to the same political agent. A complaint with at least one grievance is persisted as a single complaint; a complaint with no confirmed grievances is not submitted.

### 1.4. Found out Dependencies

* There is a dependency on **US01 - Request Registration** and **US02 - Accept/Reject Registration**, as the citizen must be registered and authenticated in the platform before submitting a complaint.

### 1.5. Input and Output Data

**Input Data:**

* Selected once per complaint:
    * political agent (from the list of registered political agents)

* Per grievance (repeated for each grievance added to the complaint):
    * Typed data:
        * grievance description
        * grievance date (date on which the reported behaviour occurred)
    * Selected data:
        * political function held by the agent at the time of the reported behaviour
    * Decision: whether to add another grievance about the same agent

**Output Data:**

* List of registered political agents
* List of predefined political functions
* Summary of each grievance
* List of grievances already added to the complaint
* (In)Success of the operation

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US12-SSD.svg)

**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks

* The identity of the citizen who submitted a complaint is recorded internally for audit purposes but is not publicly disclosed.
* The submission date is automatically recorded by the system at the moment the complaint is submitted.
* **[Design decision]** A complaint groups several grievances about the same political agent. This keeps related reports together while preserving, for each grievance, its own description, date and the function the agent held at the time. Reporting a different agent requires a separate complaint.
* **[Assumption — pending client confirmation]** A complaint is assumed to target a political agent independently of whether they have validated declarations on the platform. This needs to be confirmed with the client.
