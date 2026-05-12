# US01 - Submit Registration Request

## 3. Design

### 3.1. Rationale

**The rationale grounds on the SSD interactions and the identified input/output data.**

| Interaction ID | Question: Which class is responsible for...                                        | Answer                          | Justification (with patterns)                                                                                                                            |
|:---------------|:-----------------------------------------------------------------------------------|:--------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------|
| Step 1         | ...interacting with the actor?                                                     | RegisterUI                      | **Pure Fabrication**: the UI has no business responsibilities; it only handles I/O with the future user.                                                 |
| Step 1         | ...coordinating the US?                                                            | RegisterController              | **Controller**: decouples the UI from the domain and orchestrates the use case.                                                                          |
| Step 2         | ...knowing the available roles?                                                    | RegisterController              | **Information Expert**: the controller knows the UserRole enum and which roles are self-registrable (all except ADMINISTRATOR).                           |
| Step 3         | ...validating the password format?                                                 | RegistrationRequest             | **Information Expert**: the domain class owns the password policy and validates it in its constructor via the static `isValidPassword()` method.          |
| Step 4         | ...knowing whether an identification document is required for the selected role?   | RegisterController              | **Information Expert**: the controller encapsulates the rule that JOURNALIST and CITIZEN roles require a document number.                                 |
| Step 5         | ...creating the registration request?                                              | RegistrationRequest             | **Creator**: RegistrationRequest is responsible for its own data and invariants (password, required fields, duplicate check by email+role).              |
| Step 6         | ...persisting the registration request?                                            | RegistrationRequestRepository   | **Information Expert**: the repository holds all RegistrationRequest instances and knows how to detect duplicates (same email + role).                   |
| Step 7         | ...informing operation (in)success?                                                | RegisterUI                      | **Information Expert**: the UI is responsible for user interactions and feedback.                                                                        |

### Systematization

According to the taken rationale, the conceptual classes promoted to software classes are:

* RegistrationRequest

Other software classes (i.e. Pure Fabrication) identified:

* RegisterUI
* RegisterController
* RegistrationRequestRepository

## 3.2. Sequence Diagram (SD)

![US01-SD](svg/US01-SD.svg)

## 3.3. Class Diagram (CD)

![US01-CD](svg/US01-CD.svg)
