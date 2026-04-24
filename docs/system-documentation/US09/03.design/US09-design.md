# US09 - Consult the Integrated Situation of a Political Agent on a Given Date

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...                                                          | Answer                             | Justification (with patterns)                                                                                                                                      |
|:---------------|:-----------------------------------------------------------------------------------------------------|:-----------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Step 1         | ...interacting with the actor?                                                                       | ConsultIntegratedSituationUI       | **Pure Fabrication**: the UI has no business responsibilities; it only handles I/O with the Ethics Committee Member.                                               |
| Step 1         | ...coordinating the US?                                                                              | ConsultIntegratedSituationController | **Controller**: decouples the UI from the domain and orchestrates the use case.                                                                                  |
| Step 2         | ...obtaining the list of registered political agents so the actor can pick one?                      | PoliticalAgentRepository           | **Pure Fabrication** + **Information Expert**: the repository holds all PoliticalAgent instances and knows how to retrieve them.                                   |
| Step 3         | ...obtaining the validated declarations of the selected agent up to the reference date?              | DeclarationRepository              | **Pure Fabrication** + **Information Expert**: the repository holds all Declaration instances and is the natural place to filter by agent, status and date.        |
| Step 3         | ...knowing whether a declaration is VALIDATED and was submitted on/before the reference date?        | Declaration                        | **Information Expert**: a Declaration owns its `status` and `submissionDate`.                                                                                      |
| Step 3         | ...showing a message when there are no validated declarations? (AC3)                                 | ConsultIntegratedSituationUI       | **Pure Fabrication**: the empty-result feedback is a presentation concern.                                                                                         |
| Step 4         | ...producing the full details of the selected declaration (positions, income, assets, participations)? | Declaration                        | **Information Expert**: a Declaration aggregates its PositionEntry, Income, Asset and BusinessParticipation instances.                                             |
| Step 4         | ...presenting the details to the actor?                                                              | ConsultIntegratedSituationUI       | **Pure Fabrication**: presentation responsibility.                                                                                                                 |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* PoliticalAgent
* Declaration
* PositionEntry
* Income
* Asset
* BusinessParticipation

Other software classes (i.e. Pure Fabrication) identified:

* ConsultIntegratedSituationUI
* ConsultIntegratedSituationController
* PoliticalAgentRepository
* DeclarationRepository

## 3.2. Sequence Diagram (SD)

_In this section, it is suggested to present an UML dynamic view representing the sequence of interactions between software objects that allows to fulfill the requirements._

![US09-SD](svg/US09-SD.svg)

## 3.3. Class Diagram (CD)

_In this section, it is suggested to present an UML static view representing the main related software classes that are involved in fulfilling the requirements as well as their relations, attributes and methods._

![US09-CD](svg/US09-CD.svg)
