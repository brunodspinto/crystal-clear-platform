# US03 - Tests and Implementation

## Implementation

### Classes

| Class | Path |
|-------|------|
| `ListOrganizationsController` | `src/main/java/pt/ipp/isep/dei/controller/ListOrganizationsController.java` |
| `ListOrganizationsUI` | `src/main/java/pt/ipp/isep/dei/ui/console/ListOrganizationsUI.java` |

**`ListOrganizationsController`** — fetches all organizations from `OrganizationRepository`, groups them into an `EnumMap<OrganizationType, List<Organization>>`, and sorts each group alphabetically by name (case-insensitive).

**`ListOrganizationsUI`** — iterates over the grouped map and prints each non-empty group with a header, followed by the organization names sorted alphabetically. Shows a "no institutions" message if the repository is empty.

## Tests

- `src/test/java/pt/ipp/isep/dei/controller/ListOrganizationsControllerTest.java`

| Test | Description |
|------|-------------|
| `ensureEmptyRepositoryReturnsEmptyGroups` | All type groups are present but empty when the repository has no organizations |
| `ensureOrganizationAppearsInCorrectGroup` | A saved organization appears under its correct type group |
| `ensureOrganizationsAreSortedAlphabeticallyWithinGroup` | Multiple organizations of the same type are returned in alphabetical order |
| `ensureOrganizationsOfDifferentTypesAreInSeparateGroups` | Organizations of different types end up in separate groups |
| `ensureAllTypesAlwaysPresentInResult` | The result map always contains an entry for every `OrganizationType` |
| `ensureSortingIsCaseInsensitive` | Alphabetical sort is case-insensitive |

## Checklist

- [x] `ListOrganizationsController` — groups and sorts organizations
- [x] `ListOrganizationsUI` — displays grouped result to Political Agent
- [x] `PoliticalAgentUI` — "List Institutions" option wired up
- [x] 6 unit tests (all passing)
