# US006 - Submit a Declaration of Interests

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![Domain Model](svg/US06-DM.svg)

### 2.2. Other Remarks

* The `DeclarationOfInterests` is the central aggregate for this US. It groups all sections that a Political Agent must fill in upon submission.
* A declaration always belongs to exactly one `PoliticalAgent` and is automatically timestamped by the system at the moment of submission.
* The declaration type (initial, regular, or exceptional) determines the context of submission but does not alter the required data structure.
* Each declaration is composed of zero or more entries per section (positions/income, subsidies, assets, business participations). At least one `PositionEntry` is mandatory (AC3).
* `Institution` and `Function` are pre-existing entities that must be registered in the system before a declaration can be submitted (dependencies on US04 and US05).
* Upon successful submission, the declaration's state is automatically set to **"pending validation"** by the system — it is never set directly to "validated" by the Political Agent.
