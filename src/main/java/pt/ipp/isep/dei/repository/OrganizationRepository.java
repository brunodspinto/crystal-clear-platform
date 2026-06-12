package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Employee;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Organization repository.
 */
public class OrganizationRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Organization> organizations;

    /**
     * Instantiates a new Organization repository.
     */
    public OrganizationRepository() {
        organizations = new ArrayList<>();
    }

    /**
     * Gets organization by employee.
     *
     * @param employee the employee
     * @return the organization that employs the given employee, or {@code null} if none.
     */
    public Organization getOrganizationByEmployee(Employee employee) {
        Organization returnOrganization = null;

        for (Organization organization : organizations) {
            if (organization.employs(employee)) {
                returnOrganization = organization;
            }
        }

        return returnOrganization;
    }

    /**
     * Gets organization by employee email.
     *
     * @param email the email
     * @return the organization with an employee matching the email, or {@code null} if none.
     */
    public Organization getOrganizationByEmployeeEmail(String email) {
        Organization returnOrganization = null;

        for (Organization organization : organizations) {
            if (organization.anyEmployeeHasEmail(email)) {
                returnOrganization = organization;
            }
        }

        return returnOrganization;
    }

    /**
     * Adds an organization if no duplicate (by equals) already exists.
     *
     * @param organization the organization to add.
     * @return the stored (cloned) organization if added; {@code null} if a duplicate exists.
     */
    public Organization add(Organization organization) {
        if (!validateOrganization(organization)) {
            return null;
        }
        Organization clone = organization.clone();
        if (organizations.add(clone)) {
            return clone;
        }
        return null;
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
     * Registers a new organization, creating it if no duplicate with the same
     * name and type already exists. Following the GRASP <b>Creator</b> pattern,
     * the repository &mdash; which records all {@link Organization} instances &mdash;
     * is responsible for instantiating the new organization; the organization
     * validates its own data in its constructor (Information Expert).
     *
     * @param name   the organization name.
     * @param nature the legal nature (public, private or social).
     * @param type   the organization type.
     * @return {@code true} if registered; {@code false} if a duplicate exists.
     */
    public boolean registerOrganization(String name, OrganizationNature nature, OrganizationType type) {
        if (existsByNameAndType(name, type)) {
            return false;
        }
        return organizations.add(new Organization(name, nature, type));
    }

    /**
     * Returns an unmodifiable copy of all organizations in the repository.
     *
     * @return list of all organizations.
     */
    public List<Organization> getOrganizations() {
        return new ArrayList<>(organizations);
    }
}