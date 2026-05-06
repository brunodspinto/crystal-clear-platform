# US03 - Design

## 3.1. Sequence Diagram

The Political Agent selects "List Institutions" from the menu. The `ListOrganizationsUI` delegates to `ListOrganizationsController`, which fetches all organizations from `OrganizationRepository`, groups them by type, and sorts each group alphabetically by name before returning.

## 3.2. Class Diagram

| Class | Responsibility |
|-------|---------------|
| `ListOrganizationsUI` | Collects the request from the Political Agent and displays the grouped result |
| `ListOrganizationsController` | Retrieves organizations from the repository, groups by type, sorts alphabetically |
| `OrganizationRepository` | Stores and retrieves `Organization` instances |
| `Organization` | Domain entity with `name` and `type` fields |
| `OrganizationType` | Enum with values: COMPANY, POLITICAL_PARTY, FOUNDATION, INSTITUTE, ASSOCIATION |

## 3.3. Design Decisions

- `EnumMap<OrganizationType, List<Organization>>` is used so that groups are always presented in the fixed declaration order of the enum.
- Sorting within each group uses a `Comparator` (case-insensitive `compareToIgnoreCase`) consistent with the Comparable/Comparator pattern taught in PPROG.
- The controller accepts an injected `OrganizationRepository` to allow unit testing without the singleton.
