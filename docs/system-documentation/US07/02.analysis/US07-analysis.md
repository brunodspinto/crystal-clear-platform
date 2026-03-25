# US07 - Consult a previously submitted Declaration of Interests

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

To fulfill this requirement, the core concepts involved from the domain model are the `PoliticalAgent` and the `DeclarationOfInterest`.

The `PoliticalAgent` is the actor who interacts with the system to read data.
The `DeclarationOfInterest` is a complex aggregate root that contains multiple entities (such as Assets, Incomes, Positions, and Holdings).

For this specific User Story, the system's responsibility is purely retrieval and presentation. It must fetch the existing `DeclarationOfInterest` objects associated with the logged-in `PoliticalAgent` and display their state. No data mutation or creation occurs in this use case.

### 2.2. Other Remarks

* This US heavily relies on the data structure defined in **US06**.
* The system must implement appropriate authorization checks to ensure that a Political Agent cannot access declarations belonging to other agents.