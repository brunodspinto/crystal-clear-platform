package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.repository.OrganizationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Arrays;
import java.util.List;

public class RegisterOrganizationController {
    private OrganizationRepository organizationRepository;

    public RegisterOrganizationController() {
        this.organizationRepository = Repositories.getInstance().getOrganizationRepository();
    }

    public RegisterOrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public List<OrganizationType> getOrganizationTypes() {
        return Arrays.asList(OrganizationType.values());
    }

    public boolean registerOrganization(String name, String nature, OrganizationType type) {
        if (organizationRepository.existsByNameAndType(name, type)) {
            return false;
        }
        Organization organization = new Organization(name, nature, type);
        return organizationRepository.save(organization);
    }
}
