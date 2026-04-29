package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Employee;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizationRepository {
    private final List<Organization> organizations;

    public OrganizationRepository() {
        organizations = new ArrayList<>();
    }

    public Optional<Organization> getOrganizationByEmployee(Employee employee) {
        Optional<Organization> returnOrganization = Optional.empty();

        for (Organization organization : organizations) {
            if (organization.employs(employee)) {
                returnOrganization = Optional.of(organization);
            }
        }

        return returnOrganization;
    }

    public Optional<Organization> getOrganizationByEmployeeEmail(String email) {
        Optional<Organization> returnOrganization = Optional.empty();

        for (Organization organization : organizations) {
            if (organization.anyEmployeeHasEmail(email)) {
                returnOrganization = Optional.of(organization);
            }
        }

        return returnOrganization;
    }

    public Optional<Organization> add(Organization organization) {
        Optional<Organization> newOrganization = Optional.empty();
        boolean operationSuccess = false;

        if (validateOrganization(organization)) {
            newOrganization = Optional.of(organization.clone());
            operationSuccess = organizations.add(newOrganization.get());
        }

        if (!operationSuccess) {
            newOrganization = Optional.empty();
        }

        return newOrganization;

    }

    private boolean validateOrganization(Organization organization) {
        return !organizations.contains(organization);
    }

    /**
     * Checks whether an organization with the given name and type already exists.
     * The name comparison is case-insensitive.
     *
     * @param name the name to search for.
     * @param type the type to match.
     * @return {@code true} if a matching organization exists, {@code false} otherwise.
     */
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

    /**
     * Saves a new organization if no duplicate with the same name and type exists.
     * A clone of the organization is stored to protect internal state.
     *
     * @param organization the organization to save.
     * @return {@code true} if saved successfully, {@code false} if a duplicate exists.
     */
    public boolean save(Organization organization) {
        if (existsByNameAndType(organization.getName(), organization.getType())) {
            return false;
        }
        return organizations.add(organization.clone());
    }

    /**
     * Returns an unmodifiable copy of all organizations in the repository.
     *
     * @return list of all organizations.
     */
    public List<Organization> getOrganizations() {
        return List.copyOf(organizations);
    }
}