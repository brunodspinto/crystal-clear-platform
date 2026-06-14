# US02 - Tests and Implementation

## Implementation

### Classes

| Class | Path |
|-------|------|
| `ReviewRegistrationController` | `src/main/java/pt/ipp/isep/dei/controller/ReviewRegistrationController.java` |
| `ReviewRegistrationUI` | `src/main/java/pt/ipp/isep/dei/ui/console/ReviewRegistrationUI.java` |

**`ReviewRegistrationController`**: lists pending requests from `RegistrationRequestRepository` (both as domain objects and as `RegistrationRequestDTO` for the GUI, the latter requiring an administrator session), approves a request (sets status to APPROVED and creates the user account in `AuthenticationRepository`), or rejects it with a mandatory reason. When an approved request is for a Political Agent that carries the extra data (national identity card, tax number and mandate start), it also creates the corresponding `PoliticalAgent` in `PoliticalAgentRepository`. Approval and rejection are also available by email (`approveRequestByEmail` / `rejectRequestByEmail`) and a notification email is sent to the applicant through `EmailService`. Maps `UserRole` to the authentication system's role string via a private `roleIdFor()` method.

**`ReviewRegistrationUI`**: lists pending requests, shows full details of the selected request, presents Accept/Reject options, and collects a mandatory rejection reason when rejecting.

## Tests

- `src/test/java/pt/ipp/isep/dei/controller/ReviewRegistrationControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureGetPendingRequestsReturnsEmptyWhenNone` | Returns empty list when the repository has no requests |
| `ensureGetPendingRequestsReturnsSavedRequest` | Returns a saved PENDING request |
| `ensureGetPendingRequestsExcludesApprovedRequests` | Already-approved requests do not appear in the list |
| `ensureGetPendingRequestsExcludesRejectedRequests` | Already-rejected requests do not appear in the list |
| `ensureApproveRequestSetsStatusApproved` | Approving a request sets its status to APPROVED |
| `ensureRejectRequestSetsStatusRejected` | Rejecting a request sets its status to REJECTED |
| `ensureRejectRequestStoresReason` | The rejection reason is stored and retrievable |
| `ensureRejectRequestWithBlankReasonThrows` | Rejecting with a blank reason throws `IllegalArgumentException` |
| `ensureApproveAlreadyApprovedThrows` | Approving an already-approved request throws `IllegalStateException` |
| `ensureRejectAlreadyRejectedThrows` | Rejecting an already-rejected request throws `IllegalStateException` |
| `ensureMultiplePendingRequestsAllReturned` | All pending requests are returned when multiple exist |
| `ensureApproveCitizenRequestSetsStatusApproved` | Approving a CITIZEN request sets its status to APPROVED |
| `ensureApproveJournalistRequestSetsStatusApproved` | Approving a JOURNALIST request sets its status to APPROVED |
| `ensureApproveEthicsCommitteeRequestSetsStatusApproved` | Approving an ETHICS_COMMITTEE request sets its status to APPROVED |
| `ensureApproveAdministratorRequestSetsStatusApproved` | Approving an ADMINISTRATOR request sets its status to APPROVED |
| `ensureDefaultConstructorInitialisesRepositoriesFromSingleton` | The no-argument constructor wires the repositories from the `Repositories` singleton |
| `ensureApproveRequestSendsNotificationToCorrectEmail` | Approving a request sends one notification to the applicant's email |
| `ensureRejectRequestSendsNotificationToCorrectEmail` | Rejecting a request sends one notification to the applicant's email |
| `ensureApproveNotificationSubjectIndicatesApproval` | The approval notification subject mentions "Approved" |
| `ensureRejectNotificationBodyContainsReason` | The rejection notification body contains the given reason |
| `ensureGetPendingRequestsAsDTOReturnsAllPending` | `getPendingRequestsAsDTO` returns one DTO per pending request |
| `ensureDTOCarriesTheRequestData` | The DTO carries the name, email, role, document and submission date of the request |
| `ensureGetPendingRequestsAsDTOWithoutSessionThrows` | Requesting the DTO list with no session throws `IllegalStateException` |
| `ensureGetPendingRequestsAsDTOForNonAdminThrows` | Requesting the DTO list as a non-administrator throws `IllegalStateException` |
| `ensureApproveRequestByEmailApprovesAndNotifies` | `approveRequestByEmail` approves the request, notifies the applicant and clears it from the pending list |
| `ensureApproveRequestByEmailUnknownEmailThrows` | Approving by an unknown email throws `IllegalArgumentException` |
| `ensureRejectRequestByEmailRejectsAndStoresReason` | `rejectRequestByEmail` rejects the request and stores the reason |
| `ensureRejectRequestByEmailUnknownEmailThrows` | Rejecting by an unknown email throws `IllegalArgumentException` |
| `ensureApprovePoliticalAgentWithDataCreatesAgentInRepository` | Approving a Political Agent request that carries the data creates the `PoliticalAgent` with the correct tax number |
| `ensureApprovePoliticalAgentWithoutDataDoesNotCreateAgent` | Approving a Political Agent request with no extra data does not create an agent |
| `ensureApproveCitizenDoesNotCreatePoliticalAgent` | Approving a CITIZEN request never creates a Political Agent |

## Checklist

- [x] `ReviewRegistrationController`: list pending (domain and DTO), approve (creates user account and Political Agent), reject with reason, notify by email
- [x] `ReviewRegistrationUI`: request list, detail view, accept/reject decision, mandatory reason
- [x] `AdminUI`: "Review Registration Requests" option wired up
- [x] 31 unit tests (all passing)
