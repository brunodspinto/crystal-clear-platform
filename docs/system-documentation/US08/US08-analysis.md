# US008 - Validate a Declaration of Interests

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![Domain Model](svg/US08-DM.svg)

### 2.2. Other Remarks

* The `ValidationRecord` is the key concept introduced by this US. It captures the result of a single validation action performed by an `EthicsCommitteeMember` on a `DeclarationOfInterests`, and is permanently stored for audit purposes.
* A `DeclarationOfInterests` can only be acted upon when its state is **"pending validation"** (AC3). The system must enforce this guard.
* When validated, the declaration transitions to state **"validated"** and becomes accessible to other users according to role-based visibility rules.
* When rejected, the declaration transitions to state **"returned for correction"** and is made available to the `PoliticalAgent` for amendment. The `ValidationRecord` in this case must contain at least one `ValidationComment` identifying the section and item with the inconsistency (AC2).
* Each validation action (approval or rejection) produces exactly one `ValidationRecord`, which records the identity of the `EthicsCommitteeMember` and the timestamp automatically (AC4).
* A `ValidationComment` is only created in the context of a rejection. It must reference a specific section and item of the declaration to guide the Political Agent's correction.
* The same `EthicsCommitteeMember` may validate the same declaration more than once (e.g., after resubmission), generating a new independent `ValidationRecord` each time.
