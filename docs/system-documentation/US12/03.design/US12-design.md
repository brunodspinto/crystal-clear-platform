# US12 - Make a Complaint About a Political Agent

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...            | Answer                    | Justification (with patterns)                                                                                                                       |
|:---------------|:-------------------------------------------------------|:--------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------|
| Step 1         | ...interacting with the actor?                         | SubmitComplaintUI         | **Pure Fabrication**: there is no reason to assign this responsibility to any existing class in the Domain Model.                                   |
|                | ...coordinating the US?                                | SubmitComplaintController | **Controller**                                                                                                                                      |
|                | ...providing a single, globally accessible instance of the repositories to the controller? | Repositories | **Singleton** (GoF): `Repositories` has a private constructor and exposes `getInstance()`, guaranteeing one shared instance of each repository across the application. |
|                | ...knowing the user using the system?                  | UserSession               | **IE**: cf. A&A component documentation.                                                                                                            |
|                |                                                        | CitizenRepository         | **IE**: knows/has its own Citizens.                                                                                                                 |
|                |                                                        | Citizen                   | **IE**: knows its own data (e.g. email).                                                                                                            |
| Step 2         | ...knowing all registered political agents to show?    | Repositories              | **IE**: Repositories maintains Political Agents.                                                                                                    |
|                |                                                        | PoliticalAgentRepository  | By applying **High Cohesion (HC) + Low Coupling (LC)** on class Repositories, it delegates the responsibility on PoliticalAgentRepository.          |
| Step 3         | ...creating the (empty) complaint for the selected agent and the authenticated citizen? | SubmitComplaintController | **Controller** + **Creator**: it knows the session/citizen and starts the complaint via `createComplaint(agent)`.                                  |
|                | ...knowing the citizen and the submission date?        | Complaint                 | **IE**: owns its `citizen`, `politicalAgent` and `submissionDate` (set to `new Date()` on instantiation).                                          |
| Step 4         | ...knowing all predefined political functions to show? | PoliticalFunction         | **IE**: the enum owns its own set of values.                                                                                                        |
| Step 5         | ...requesting and keeping the grievance data (function, description, date) until confirmation? | SubmitComplaintUI | **IE**: is responsible for user interactions and for the current selections.                                                                       |
| Step 6         | ...showing the grievance summary and requesting confirmation? | SubmitComplaintUI  | **IE**: is responsible for user interactions.                                                                                                       |
| Step 7         | ...adding a confirmed grievance to the complaint?      | SubmitComplaintController | **Controller**: delegates to the complaint via `addGrievance(complaint, ...)`.                                                                      |
|                | ...creating each grievance and validating its data?    | Complaint / ComplaintItem | **Creator** + **IE**: `Complaint.addItem(...)` instantiates a `ComplaintItem`, which validates its own description, date and function.             |
| Step 8         | ...asking whether to add another grievance about the same agent (loop)? | SubmitComplaintUI | **IE**: drives the dialogue with the actor.                                                                                                         |
| Step 9         | ...persisting the complaint with all its grievances?   | SubmitComplaintController | **Controller**: `saveComplaint(complaint)` checks there is at least one grievance and delegates persistence.                                       |
|                | ...storing and retrieving all Complaint instances?     | ComplaintRepository       | **Pure Fabrication** + **IE**: the repository is responsible for storing and retrieving all Complaint instances.                                    |
| Step 10        | ...informing operation success?                        | SubmitComplaintUI         | **IE**: is responsible for user interactions.                                                                                                       |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* Complaint
* ComplaintItem
* PoliticalAgent
* PoliticalFunction
* Citizen

Other software classes (i.e. Pure Fabrication) identified:

* SubmitComplaintUI
* SubmitComplaintController
* Repositories (**Singleton**)
* PoliticalAgentRepository
* CitizenRepository
* ComplaintRepository
* AuthenticationRepository
* ApplicationSession
* UserSession


## 3.2. Sequence Diagram (SD)

_This diagram shows the full sequence of interactions between the classes involved in the realization of this user story._

![Sequence Diagram](svg/US12-SD.svg)


## 3.3. Class Diagram (CD)

_This diagram shows the main related software classes involved in fulfilling the requirements, their relations, attributes and methods._

![Class Diagram](svg/US12-CD.svg)
