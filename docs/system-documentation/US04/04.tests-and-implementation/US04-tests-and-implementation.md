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
    this.vatNumber = UUID.randomUUID().toString();
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


## 6. Integration and Demo

* A new option **"Register Organization"** was added to the Admin menu.
* For demo purposes, three sample organizations (a political party, a company, and a foundation) are bootstrapped when the system starts.
* The admin credentials for demo: **admin@this.app / admin**.


## 7. Observations

* The `nature` field indicates the legal nature of the organization (e.g. "public", "private", "social") and cannot be null or empty (AC4).
* The internal `vatNumber` is generated as a UUID when using the US04 constructor, since this use case does not require a VAT number.
