# US01 - Tests and Implementation

## Implementation

### Classes

| Class | Path |
|-------|------|
| `RegisterController` | `src/main/java/pt/ipp/isep/dei/controller/RegisterController.java` |
| `RegisterUI` | `src/main/java/pt/ipp/isep/dei/ui/console/RegisterUI.java` |
| `RegistrationRequest` | `src/main/java/pt/ipp/isep/dei/domain/RegistrationRequest.java` |

**`RegisterController`**: exposes the available roles (excluding ADMINISTRATOR), validates the password format, checks whether an identification document is required, and delegates request creation and persistence to `RegistrationRequest` and `RegistrationRequestRepository`.

**`RegisterUI`**: collects the user's role selection, name, email, password (with up to 3 retry attempts), and optional identification document; shows a confirmation summary and calls the controller.

**`RegistrationRequest`**: domain class that validates all fields on construction (password: 7 alphanumeric chars, ≥3 uppercase, ≥2 digits; required fields non-blank; document required for JOURNALIST and CITIZEN). Equality is determined by email + role to enforce the duplicate check.

## Tests

- `src/test/java/pt/ipp/isep/dei/controller/RegisterControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureGetAvailableRolesExcludesAdministrator` | ADMINISTRATOR role is not included in the self-registration list |
| `ensureGetDocumentLabelForJournalist` | Document label for JOURNALIST contains "press" |
| `ensureGetDocumentLabelForCitizen` | Document label for CITIZEN contains "national" |
| `ensureGetDocumentLabelForOtherRoleReturnsNull` | Roles that don't require a document return null label |
| `ensureJournalistRequiresDocument` | `requiresDocument` returns true for JOURNALIST |
| `ensureCitizenRequiresDocument` | `requiresDocument` returns true for CITIZEN |
| `ensureAdminDoesNotRequireDocument` | `requiresDocument` returns false for ADMINISTRATOR |
| `ensurePoliticalAgentDoesNotRequireDocument` | `requiresDocument` returns false for POLITICAL_AGENT |
| `ensureValidRequestIsSubmitted` | A valid request is saved and the repository has 1 entry |
| `ensureDuplicateRequestIsRejected` | Submitting the same email + role twice returns false and keeps 1 entry |
| `ensureSameEmailDifferentRoleIsAllowed` | Same email with a different role is accepted as a second entry |
| `ensureInvalidPasswordThrows` | A password that fails the policy throws `IllegalArgumentException` |
| `ensureJournalistWithDocumentIsSubmitted` | A JOURNALIST request with a document is accepted |
| `ensureJournalistWithoutDocumentThrows` | A JOURNALIST request with no document throws `IllegalArgumentException` |
| `ensureIsValidPasswordDelegatesToDomain` | `isValidPassword` returns true for a valid password and false for a weak one |

## Checklist

- [x] `RegistrationRequest`: domain class with password policy and duplicate-check equality
- [x] `RegisterController`: role listing, document requirement, request submission
- [x] `RegisterUI`: role selection, password retry, confirmation, error handling
- [x] `MainMenuUI`: "Register" option accessible before login
- [x] 15 unit tests (all passing)
