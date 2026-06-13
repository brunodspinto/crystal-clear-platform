# US37 - Build the Declaration Interest Graph

> As a Product Owner, I want to build the interest graph from the validated declarations and export its entities and relations to CSV, with the family relationships inferred from the declared kinships.

The family ties are expanded by `FamilyRelationshipInferrer`, which applies three closures over the seed relationships:

- **Symmetric** (e.g. `spouseOf`, `siblingOf`)
- **Inverse** (e.g. `parentOf` / `childOf`)
- **Transitive** (`parentOf` + `parentOf` => `grandparentOf`)

## Classes

| Class | Path |
|-------|------|
| `ExportDeclarationGraphController` | `src/main/java/pt/ipp/isep/dei/controller/ExportDeclarationGraphController.java` |
| `ExportDeclarationGraphUI` | `src/main/java/pt/ipp/isep/dei/ui/console/ExportDeclarationGraphUI.java` |
| `DeclarationGraphCsvExporter` | `src/main/java/pt/ipp/isep/dei/domain/graph/DeclarationGraphCsvExporter.java` |
| `FamilyRelationshipInferrer` | `src/main/java/pt/ipp/isep/dei/domain/graph/FamilyRelationshipInferrer.java` |
| `FamilyRelationshipType` | `src/main/java/pt/ipp/isep/dei/domain/graph/FamilyRelationshipType.java` |

The UI is reachable from the Admin menu ("Export Declaration Interest Graph (US37)"). It asks for an optional family relationships CSV (seed), an entities output path and a relations output path, then exports both files and reports how many family ties were inferred.
