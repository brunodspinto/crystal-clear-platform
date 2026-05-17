# US02 - Tests and Implementation

## Implementation

### Classes

| Class | Path |
|-------|------|
| `ReviewRegistrationController` | `src/main/java/pt/ipp/isep/dei/controller/ReviewRegistrationController.java` |
| `ReviewRegistrationUI` | `src/main/java/pt/ipp/isep/dei/ui/console/ReviewRegistrationUI.java` |

**`ReviewRegistrationController`**: lists pending requests from `RegistrationRequestRepository`, approves a request (sets status to APPROVED and creates the user account in `AuthenticationRepository`), or rejects it with a mandatory reason. Maps `UserRole` to the authentication system's role string via a private `roleIdFor()` method.

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

## Checklist

- [x] `ReviewRegistrationController`: list pending, approve (creates user account), reject with reason
- [x] `ReviewRegistrationUI`: request list, detail view, accept/reject decision, mandatory reason
- [x] `AdminUI`: "Review Registration Requests" option wired up
- [x] 15 unit tests (all passing)
