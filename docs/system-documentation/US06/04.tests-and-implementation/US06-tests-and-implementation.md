# US06 - Submit a Declaration of Interests

## Implementation

### Classes

| Class | Path |
|-------|------|
| `SubmitDeclarationController` | `src/main/java/pt/ipp/isep/dei/controller/SubmitDeclarationController.java` |
| `SubmitDeclarationUI` | `src/main/java/pt/ipp/isep/dei/ui/console/SubmitDeclarationUI.java` |
| `Declaration` | `src/main/java/pt/ipp/isep/dei/domain/Declaration.java` |
| `PositionEntry` | `src/main/java/pt/ipp/isep/dei/domain/PositionEntry.java` |
| `SubsidyEntry` | `src/main/java/pt/ipp/isep/dei/domain/SubsidyEntry.java` |
| `AssetEntry` | `src/main/java/pt/ipp/isep/dei/domain/AssetEntry.java` |
| `BusinessParticipation` | `src/main/java/pt/ipp/isep/dei/domain/BusinessParticipation.java` |
| `Attachment` | `src/main/java/pt/ipp/isep/dei/domain/Attachment.java` |
| `DeclarationType` | `src/main/java/pt/ipp/isep/dei/domain/DeclarationType.java` |
| `DeclarationStatus` | `src/main/java/pt/ipp/isep/dei/domain/DeclarationStatus.java` |
| `AssetType` | `src/main/java/pt/ipp/isep/dei/domain/AssetType.java` |
| `PositionNature` | `src/main/java/pt/ipp/isep/dei/domain/PositionNature.java` |
| `DeclarationRepository` | `src/main/java/pt/ipp/isep/dei/repository/DeclarationRepository.java` |

**`SubmitDeclarationController`**: resolves the currently authenticated Political Agent from the session via `AuthenticationRepository` and `PoliticalAgentRepository`, builds a `Declaration` aggregate by iterating the lists of section data supplied by the UI, and delegates persistence to `DeclarationRepository.save()`. Returns `false` if the agent is not found in the system.

**`Declaration`**: central aggregate for this US. Created with status `PENDING` and a server-side submission date. Owns all section lists (positions, subsidies, assets, business participations, attachments) and exposes one `add*` method per section. All section objects are created as value objects and stored internally; getters return defensive copies.

**`PositionEntry`**: value object for a single professional position. Validates that the organization and function designation are non-null/non-blank, that nature is non-null, that all numeric income fields are non-negative, and that the start date is provided (end date may be null for currently held positions).

**`AssetEntry`**: value object for a declared asset. Enforces that the `detail` argument matches the declared `AssetType` — a `RealEstate` for `REAL_ESTATE`, a `VehicleAsset` for `VEHICLES`, and a `StockAsset` for `STOCKS`. Mismatches throw `IllegalArgumentException`.

**`BusinessParticipation`**: value object for a company holding. Validates that the percentage is within [0, 100] and that the stock value is non-negative.

**`SubmitDeclarationUI`**: collects each section interactively, building the `Object[]` arrays expected by the controller, and displays a confirmation message after submission.

### Key design decisions

- The controller receives pre-structured `List<Object[]>` parameters rather than domain objects directly, keeping the UI/controller boundary clean and avoiding circular dependencies between layers.
- The submission date is set to `new Date()` inside the controller, not in the UI, so the business rule (date = system date at submission time) is enforced independently of the interface.
- The initial status `PENDING` is set unconditionally in the `Declaration` constructor — no external code can submit a declaration in a different state.

## Tests

### `src/test/java/pt/ipp/isep/dei/domain/DeclarationTest.java`

| Test | Description |
|------|-------------|
| `ensureDeclarationCreationWorks` | A valid `Declaration` is created without exception |
| `ensureDeclarationFailsWithNullType` | `null` declaration type throws `IllegalArgumentException` |
| `ensureDeclarationFailsWithNullAgent` | `null` agent throws `IllegalArgumentException` |
| `ensureDeclarationFailsWithNullSubmissionDate` | `null` submission date throws `IllegalArgumentException` |
| `ensureInitialStatusIsPending` | Newly created declaration always has status `PENDING` |
| `ensurePositionEntriesStartEmpty` | Position entry list is empty on creation |
| `ensureSubsidyEntriesStartEmpty` | Subsidy entry list is empty on creation |
| `ensureAssetEntriesStartEmpty` | Asset entry list is empty on creation |
| `ensureBusinessParticipationsStartEmpty` | Business participations list is empty on creation |
| `ensureAttachmentsStartEmpty` | Attachments list is empty on creation |
| `ensureAddPositionEntryWorks` | A single position entry is added and the list has size 1 |
| `ensureMultiplePositionEntriesCanBeAdded` | Two position entries are both stored |
| `ensureAddSubsidyEntryWorks` | A subsidy entry is added and stored |
| `ensureAddRealEstateAssetEntryWorks` | A `REAL_ESTATE` asset entry with a `RealEstate` detail is accepted |
| `ensureAddVehicleAssetEntryWorks` | A `VEHICLES` asset entry with a `VehicleAsset` detail is accepted |
| `ensureAddStockAssetEntryWorks` | A `STOCKS` asset entry with a `StockAsset` detail is accepted |
| `ensureAddBusinessParticipationWorks` | A business participation entry is added and stored |
| `ensureAddAttachmentWorks` | An attachment is added and stored |
| `ensureSetStatusToValidatedWorks` | Status can be updated to `VALIDATED` |
| `ensureSetStatusToRejectedWorks` | Status can be updated to `REJECTED` |
| `ensureSetStatusFailsWithNull` | Setting a `null` `DeclarationStatus` throws `IllegalArgumentException` |
| `ensureGettersReturnCorrectValues` | `getType()`, `getAgent()`, and `getSubmissionDate()` return the values supplied at construction |
| `ensureGetPositionEntriesReturnsDefensiveCopy` | Two successive calls to `getPositionEntries()` return different list instances |
| `ensureSetStatusWithValidatedOutcomeSetsValidated` | `setStatus(ValidationOutcome.VALIDATED)` maps to `DeclarationStatus.VALIDATED` |
| `ensureSetStatusWithReturnedForCorrectionSetsRejected` | `setStatus(RETURNED_FOR_CORRECTION)` maps to `DeclarationStatus.REJECTED` |
| `ensureSetStatusWithNullOutcomeFails` | `setStatus((ValidationOutcome) null)` throws `IllegalArgumentException` |
| `ensureGetDetailsReturnsNonEmptyString` | `getDetails()` returns a non-null, non-blank string |
| `ensureGetDetailsContainsAgentName` | `getDetails()` output includes the agent's name |

### `src/test/java/pt/ipp/isep/dei/domain/PositionEntryTest.java`

| Test | Description |
|------|-------------|
| `ensureCreationWorks` | A valid `PositionEntry` is created without exception |
| `ensureNullOrganizationFails` | `null` organization throws `IllegalArgumentException` |
| `ensureNullFunctionDesignationFails` | `null` function designation throws `IllegalArgumentException` |
| `ensureBlankFunctionDesignationFails` | Blank function designation throws `IllegalArgumentException` |
| `ensureNullNatureFails` | `null` position nature throws `IllegalArgumentException` |
| `ensureNegativeGrossSalaryFails` | Negative gross salary throws `IllegalArgumentException` |
| `ensureNegativeSideIncomeConsultingFails` | Negative consulting side income throws `IllegalArgumentException` |
| `ensureNegativeSideIncomeBoardMembershipsFails` | Negative board memberships side income throws `IllegalArgumentException` |
| `ensureNullStartDateFails` | `null` start date throws `IllegalArgumentException` |
| `ensureNullEndDateIsAllowed` | `null` end date is accepted (position still active) |
| `ensureGettersReturnCorrectValues` | All getters return the values provided at construction |

### `src/test/java/pt/ipp/isep/dei/domain/AssetEntryTest.java`

| Test | Description |
|------|-------------|
| `ensureRealEstateCreationWorks` | `AssetEntry` with `REAL_ESTATE` type and `RealEstate` detail is accepted |
| `ensureVehicleCreationWorks` | `AssetEntry` with `VEHICLES` type and `VehicleAsset` detail is accepted |
| `ensureStockCreationWorks` | `AssetEntry` with `STOCKS` type and `StockAsset` detail is accepted |
| `ensureNullTypeFails` | `null` asset type throws `IllegalArgumentException` |
| `ensureNegativeValueFails` | Negative asset value throws `IllegalArgumentException` |
| `ensureNullDetailFails` | `null` detail throws `IllegalArgumentException` |
| `ensureWrongDetailTypeForRealEstateFails` | Providing a `VehicleAsset` for `REAL_ESTATE` throws `IllegalArgumentException` |
| `ensureWrongDetailTypeForVehiclesFails` | Providing a `StockAsset` for `VEHICLES` throws `IllegalArgumentException` |
| `ensureWrongDetailTypeForStocksFails` | Providing a `RealEstate` for `STOCKS` throws `IllegalArgumentException` |
| `ensureGetAssetTypeWorks` | `getAssetType()` returns the type provided at construction |
| `ensureGetAssetValueWorks` | `getAssetValue()` returns the value provided at construction |

### `src/test/java/pt/ipp/isep/dei/domain/BusinessParticipationTest.java`

| Test | Description |
|------|-------------|
| `ensureCreationWorks` | A valid `BusinessParticipation` is created without exception |
| `ensureNullOrganizationFails` | `null` organization throws `IllegalArgumentException` |
| `ensureNegativeTotalValueFails` | Negative total value in stocks throws `IllegalArgumentException` |
| `ensureNegativePercentageFails` | Negative company percentage throws `IllegalArgumentException` |
| `ensurePercentageAbove100Fails` | Company percentage above 100 throws `IllegalArgumentException` |
| `ensureZeroPercentageIsAllowed` | 0% participation is accepted |
| `ensureHundredPercentIsAllowed` | 100% participation is accepted |
| `ensureGettersReturnCorrectValues` | All getters return the values provided at construction |

### `src/test/java/pt/ipp/isep/dei/repository/DeclarationRepositoryTest.java`

| Test | Description |
|------|-------------|
| `ensureSaveReturnsTrueForNewDeclaration` | Saving a new declaration returns `true` |
| `ensureSaveNullThrows` | Saving `null` throws `IllegalArgumentException` |
| `ensureGetDeclarationsByStatusReturnsMatchingDeclarations` | Returns only declarations with the requested status |
| `ensureGetDeclarationsByStatusReturnsEmptyListWhenNoneMatch` | Returns empty list when no declaration has the requested status |
| `ensureGetDeclarationsByAgentReturnsCorrectDeclarations` | Returns only declarations belonging to the specified agent |
| `ensureGetAllReturnsAllSavedDeclarations` | All saved declarations are returned |
| `ensureGetAllReturnsDefensiveCopy` | Two successive calls to `getAll()` return different list instances |

## Checklist

- [x] `Declaration`: aggregate with PENDING initial status, all section lists, and `getDetails()` for display
- [x] `PositionEntry`, `SubsidyEntry`, `AssetEntry`, `BusinessParticipation`, `Attachment`: value objects with input validation
- [x] `SubmitDeclarationController`: agent resolution from session, declaration assembly, persistence
- [x] `SubmitDeclarationUI`: interactive section collection, confirmation display
- [x] `PoliticalAgentUI`: "Submit Declaration of Interests" option wired to `SubmitDeclarationUI`
- [x] 46 unit tests covering domain construction, validation rules, and repository behaviour
