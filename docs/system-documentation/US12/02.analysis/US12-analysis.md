# US12 - Make a Complaint About a Political Agent

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![Domain Model](svg/US12-DM.svg)

### 2.2. Other Remarks

* The `Complaint` is the central concept introduced by this US. It records the citizen's report of a behaviour considered to lack transparency, targeting a specific `PoliticalAgent` in a specific `PoliticalFunction` on a specific date.
* A `Complaint` is submitted by exactly one `Citizen` and targets exactly one `PoliticalAgent`. The submission date is automatically recorded by the system at the moment of submission.
* The `complaintDate` (the date on which the reported behaviour occurred) is provided by the citizen and must be a valid past or present date (AC3). It is distinct from the submission date recorded by the system.
* The `PoliticalFunction` referenced in the complaint represents the role held by the political agent at the time of the behaviour, selected from the predefined enum `{MINISTER, DEPUTY, COUNCILLOR, PARISH_COUNCIL_PRESIDENT, MAYOR}`, consistent with the global domain model.
* A complaint is assumed to be independent of any `DeclarationOfInterests` — it can be submitted about any registered political agent regardless of their declaration history. **This assumption is pending client confirmation.**
* The identity of the submitting `Citizen` is stored internally (associated with the `Complaint`) for audit purposes but is not publicly disclosed.
