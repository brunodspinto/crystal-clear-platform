# US008 - Validate a Declaration of Interests

## 1. Requirements Engineering

### 1.1. User Story Description

As a member of the Ethics Committee, I want to validate a submitted Declaration of Interests.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

> The Ethics Committee is normally responsible for: i) managing and supervising the Declarations of Interests of political actors; ii) verifying incompatibilities and impediments: situations where the professional or personal activity of a political actor may conflict with their mandate; iii) verifying the Code of Conduct: application and investigation of possible violations of the rules of conduct and civility.

> Declarations of Interest may be initial, regular, or exceptional.

**From the client clarifications:**

> **Question:** What happens when a declaration is validated?
>
> **Answer:** AC1: When correct, it is validated. AC2: When incorrect, the section/item with inconsistencies must be commented.

### 1.3. Acceptance Criteria

* **AC1:** When the declaration is found to be correct and complete, the Ethics Committee member must mark it as **validated**; the declaration then becomes visible according to role-based access rules.
* **AC2:** When the declaration contains inconsistencies or errors, the Ethics Committee member must add a comment identifying the specific **section** and **item** with the issue before rejecting it; the declaration is returned to the Political Agent for correction.
* **AC3:** It must not be possible to validate or reject a declaration that is not in "pending validation" state.
* **AC4:** The identity of the Ethics Committee member performing the validation and the timestamp of the action must be automatically recorded by the system.
* **AC5:** A validated declaration cannot be modified by the Political Agent; only a new exceptional declaration may be submitted to amend it.

**_AC3,4,5 where determined without input from the client and might change when more questions are asked_**

### 1.4. Found out Dependencies

* There is a dependency on **US006 - Submit a Declaration of Interests**, as a declaration must have been submitted and be in "pending validation" state before it can be validated.
* There is a dependency on **US01 - Request Registration** and **US02 - Accept/Reject Registration**, as the Ethics Committee member must be registered and authenticated in the platform with the appropriate role.

### 1.5. Input and Output Data

**Input Data:**

* Selected data:
    * declaration to validate (from the list of pending declarations)
    * validation outcome (validated / rejected with comments)

* Typed data (only when rejecting):
    * section(s) containing inconsistencies
    * item(s) within each section containing inconsistencies
    * comment(s) describing the nature of each inconsistency

**Output Data:**

* List of declarations pending validation
* Full content of the selected declaration
* (In)Success of the operation
* Updated declaration state (validated or returned for correction)

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/US08-SSD.svg)

**_Other alternatives might exist._**

### 1.7. Other Relevant Remarks

* All validation actions (both approvals and rejections) are permanently logged in the system for audit purposes, including the identity of the Ethics Committee member and the timestamp.
* A rejected declaration is returned to the submitting Political Agent, who may correct it and resubmit; the resubmitted declaration re-enters the "pending validation" queue.
* Once validated, the declaration is accessible to citizens, journalists, and other authorised users subject to role-based data visibility rules (e.g., sensitive data may be partially omitted for certain roles — see US011).
