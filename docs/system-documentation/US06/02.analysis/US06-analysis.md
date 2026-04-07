# US06 - Submit a Declaration of Interests

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![Domain Model](svg/US06-DM.svg)

### 2.2. Other Remarks

* The `DeclarationOfInterests` is the central aggregate for this US. It groups all sections that a Political Agent must fill in upon submission.
* A declaration always belongs to exactly one `PoliticalAgent` and the submission date is automatically recorded by the system at the moment of submission.
* The declaration type (initial, regular, or exceptional) is represented as the `DeclarationType` enum, consistent with the global domain model.
* The declaration status is represented as the `DeclarationStatus` enum with values `pending`, `validated`, and `rejected`. Upon submission, the status is always set to `pending`.
* Each declaration is composed of zero or more entries per section (`PositionEntry`, `SubsidyEntry`, `AssetEntry`, `BusinessParticipation`). At least one `PositionEntry` is mandatory (AC3).
* `PositionEntry` holds `remuneration: Double`, `startDate: Date`, and `endDate: Date`, and references a pre-existing `Function` and `Institution`. The nature of the position (public, private, social) is an attribute of `PositionEntry`.
* `AssetEntry` holds `acquisitionValue: Double` and `marketValue: Double`, and is linked to a `RealEstate` description. The asset type (urban or rural) is represented by the `AssetType` enum.
* `BusinessParticipation` holds `numberOfShares: Integer` and `marketValue: Double`, and references the company as an `Institution`.
* `SubsidyEntry` holds `amount: Double`, `description: String`, and `date: Date`, and references the source as an `Institution`.
* `Institution` and `Function` are pre-existing entities registered in the system (dependencies on US04 and US05).
* `InstitutionType` is an enum classifying institutions as company, politicalParty, foundation, institute, or association.
