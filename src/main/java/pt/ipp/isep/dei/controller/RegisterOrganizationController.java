package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.repository.OrganizationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Arrays;
import java.util.List;

/**
 * Controller responsible for handling the registration of an organization (US04).
 */
public class RegisterOrganizationController {
    private OrganizationRepository organizationRepository;

    /**
     * Creates a controller using the singleton repository.
     */
    public RegisterOrganizationController() {
        this.organizationRepository = Repositories.getInstance().getOrganizationRepository();
    }

    /**
     * Creates a controller with an injected repository (used in tests).
     *
     * @param organizationRepository the repository to use.
     */
    public RegisterOrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Returns all available organization types.
     *
     * @return list of {@link OrganizationType} values.
     */
    public List<OrganizationType> getOrganizationTypes() {
        return Arrays.asList(OrganizationType.values());
    }

    /**
     * Returns all available organization natures.
     *
     * @return list of {@link OrganizationNature} values.
     */
    public List<OrganizationNature> getOrganizationNatures() {
        return Arrays.asList(OrganizationNature.values());
    }

    /**
     * Registers a new organization if no duplicate exists with the same name and type.
     *
     * @param name   the name of the organization.
     * @param nature the legal nature (e.g. "public", "private", "social").
     * @param type   the type of the organization.
     * @return {@code true} if registered successfully, {@code false} if a duplicate exists.
     */
    public boolean registerOrganization(String name, String nature, OrganizationType type) {
        if (organizationRepository.existsByNameAndType(name, type)) {
            return false;
        }
        Organization organization = new Organization(name, nature, type);
        return organizationRepository.save(organization);
    }
}
