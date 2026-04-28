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

    public List<Organization> getOrganizations() {
        return List.copyOf(organizations);
    }
}