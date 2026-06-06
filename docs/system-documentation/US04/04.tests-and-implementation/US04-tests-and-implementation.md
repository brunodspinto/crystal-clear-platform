# US04 - Register an Organization

## 4. Tests

**Test 1:** Check that it is not possible to create an Organization with a null name — AC1.

    @Test
    void ensureOrganizationUS04CreationFailsWithNullName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization(null, "nature", OrganizationType.COMPANY));
    }

**Test 2:** Check that it is not possible to create an Organization with a blank name — AC1.

    @Test
    void ensureOrganizationUS04CreationFailsWithBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("   ", "nature", OrganizationType.COMPANY));
    }

**Test 3:** Check that it is not possible to create an Organization with a null type — AC2.

    @Test
    void ensureOrganizationUS04CreationFailsWithNullType() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", "nature", null));
    }

**Test 4:** Check that the repository rejects duplicate organizations with the same name and type — AC3.

    @Test
    void ensureSaveDuplicateOrganizationFails() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org = new Organization("ACME Corp", "private", OrganizationType.COMPANY);
        repo.save(org);
        Organization dup = new Organization("ACME Corp", "public", OrganizationType.COMPANY);
        assertFalse(repo.save(dup));
    }

**Test 5:** Check that two organizations with the same name but different types are both allowed — AC3.

    @Test
    void ensureSameNameDifferentTypeIsAllowed() {
        OrganizationRepository repo = new OrganizationRepository();
        Organization org1 = new Organization("ACME Corp", "private", OrganizationType.COMPANY);
        Organization org2 = new Organization("ACME Corp", "social", OrganizationType.FOUNDATION);
        repo.save(org1);
        assertTrue(repo.save(org2));
    }

**Test 6:** Check that `existsByNameAndType` is case-insensitive — AC3.

    @Test
    void ensureExistsByNameAndTypeIsCaseInsensitive() {
        OrganizationRepository repo = new OrganizationRepository();
        repo.save(new Organization("ACME Corp", "social", OrganizationType.FOUNDATION));
        assertTrue(repo.existsByNameAndType("acme corp", OrganizationType.FOUNDATION));
    }

**Test 7:** Check that it is not possible to create an Organization with a null nature — AC4.

    @Test
    void ensureOrganizationUS04CreationFailsWithNullNature() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", null, OrganizationType.FOUNDATION));
    }

**Test 8:** Check that it is not possible to create an Organization with a blank nature — AC4.

    @Test
    void ensureOrganizationUS04CreationFailsWithBlankNature() {
        assertThrows(IllegalArgumentException.class, () ->
                new Organization("Test Org", "   ", OrganizationType.FOUNDATION));
    }

**Test 9:** Check that the controller provides all predefined organization natures for the Administrator to select from — AC4.

    @Test
    void ensureGetOrganizationNaturesReturnsAllValues() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());
        assertEquals(OrganizationNature.values().length,
                controller.getOrganizationNatures().size());
    }

**Test 10:** Check that the controller provides all predefined organization types for the Administrator to select from — AC2.

    @Test
    void ensureGetOrganizationTypesReturnsAllValues() {
        RegisterOrganizationController controller =
                new RegisterOrganizationController(new OrganizationRepository());
        assertEquals(OrganizationType.values().length,
                controller.getOrganizationTypes().size());
    }

> The tests above are split between `OrganizationTest` / `OrganizationRepositoryTest` (domain/repository validation) and `RegisterOrganizationControllerTest` (the predefined type/nature lists and the duplicate handling).


## 5. Construction (Implementation)

### Class RegisterOrganizationController

```java
public boolean registerOrganization(String name, String nature, OrganizationType type) {
    if (organizationRepository.existsByNameAndType(name, type)) {
        return false;
    }
    Organization organization = new Organization(name, nature, type);
    return organizationRepository.save(organization);
}
```

### Class Organization (US04 constructor)

```java
public Organization(String name, String nature, OrganizationType type) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (nature == null || nature.isBlank()) {
        throw new IllegalArgumentException("Nature cannot be null or empty");
    }
    if (type == null) {
        throw new IllegalArgumentException("Type cannot be null");
    }
    this.vatNumber = "GEN-" + nextGeneratedVat;
    nextGeneratedVat = nextGeneratedVat + 1;
    this.name = name;
    this.nature = nature;
    this.type = type;
    this.website = null;
    this.phone = null;
    this.email = null;
    this.employees = new ArrayList<>();
    this.tasks = new ArrayList<>();
}
```

### Class OrganizationRepository

```java
public boolean existsByNameAndType(String name, OrganizationType type) {
    for (Organization org : organizations) {
        if (org.getType() != null
                && org.getName().equalsIgnoreCase(name)
                && org.getType() == type) {
            return true;
        }
    }
    return false;
}

public boolean save(Organization organization) {
    if (existsByNameAndType(organization.getName(), organization.getType())) {
        return false;
    }
    return organizations.add(organization.clone());
}
```

### Enum OrganizationNature

The nature is a predefined value (AC4), modelled as an enum so the Administrator selects it from a list instead of typing free text.

```java
public enum OrganizationNature {
    PUBLIC("Public"),
    PRIVATE("Private"),
    SOCIAL("Social");

    private final String label;

    OrganizationNature(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
```

### Class RegisterOrganizationController (lists for selection)

```java
public List<OrganizationType> getOrganizationTypes() {
    return Arrays.asList(OrganizationType.values());
}

public List<OrganizationNature> getOrganizationNatures() {
    return Arrays.asList(OrganizationNature.values());
}
```

### Class RegisterOrganizationUI (nature is selected, not typed)

```java
private OrganizationNature displayAndSelectOrganizationNature() {
    List<OrganizationNature> natures = controller.getOrganizationNatures();
    return (OrganizationNature) Utils.showAndSelectOne(natures, "Select the organization nature:");
}
// the selected nature's label is passed to registerOrganization(...)
```


## 6. Integration and Demo

* A new option **"Register Organization"** was added to the Admin menu.
* For demo purposes, three sample organizations (a political party, a company, and a foundation) are bootstrapped when the system starts.
* The admin credentials for demo: **admin@this.app / admin**.


## 7. Observations

* The `nature` field indicates the legal nature of the organization. At the UI level it is selected from the predefined `OrganizationNature` enum (`PUBLIC`, `PRIVATE`, `SOCIAL`) via `RegisterOrganizationController.getOrganizationNatures()` (AC4); the selected label is then passed to the `Organization` constructor, which still validates it as non-null/non-blank as a defensive measure.
* The internal `vatNumber` is generated using a static counter (`"GEN-" + nextGeneratedVat`) when using the US04 constructor, since this use case does not require a VAT number.
* To support the object-serialization persistence requirement, `Organization` and `OrganizationRepository` implement `java.io.Serializable` (the `OrganizationType` and `OrganizationNature` enums are serializable by default), so they can be persisted together with the `Repositories` singleton.
* The persistence itself is performed by `RepositoriesFile` (object serialization to a binary file, following the PPROG pattern): the `Repositories` singleton is loaded on startup and saved on exit by both entry points (`Main` for the console and `App` for the GUI). As a result, registered organizations survive between two successive runs.
