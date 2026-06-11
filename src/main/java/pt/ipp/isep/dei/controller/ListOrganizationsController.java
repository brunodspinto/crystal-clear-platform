package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.dto.OrganizationDTO;
import pt.ipp.isep.dei.mapper.OrganizationMapper;
import pt.ipp.isep.dei.repository.OrganizationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for listing institutions grouped by type (US03).
 */
public class ListOrganizationsController {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper = new OrganizationMapper();

    /**
     * Creates a controller using the singleton repository.
     */
    public ListOrganizationsController() {
        this.organizationRepository = Repositories.getInstance().getOrganizationRepository();
    }

    /**
     * Creates a controller with an injected repository (used in tests).
     *
     * @param organizationRepository the repository to use.
     */
    public ListOrganizationsController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Returns all registered organizations grouped by type and sorted alphabetically by name within each group.
     * The organizations are returned as DTOs so the UI does not depend on the domain entity.
     * The map preserves the declaration order of {@link OrganizationType}.
     *
     * @return a map from each {@link OrganizationType} to the sorted list of organization DTOs of that type.
     */
    public Map<OrganizationType, List<OrganizationDTO>> getOrganizationsGroupedByType() {
        List<Organization> all = organizationRepository.getOrganizations();

        Map<OrganizationType, List<Organization>> grouped = new EnumMap<>(OrganizationType.class);
        for (OrganizationType type : OrganizationType.values()) {
            grouped.put(type, new ArrayList<>());
        }

        for (Organization org : all) {
            if (org.getType() != null) {
                grouped.get(org.getType()).add(org);
            }
        }

        for (List<Organization> group : grouped.values()) {
            Collections.sort(group, new Comparator<Organization>() {
                @Override
                public int compare(Organization o1, Organization o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }

        Map<OrganizationType, List<OrganizationDTO>> groupedDTOs = new EnumMap<>(OrganizationType.class);
        for (Map.Entry<OrganizationType, List<Organization>> entry : grouped.entrySet()) {
            groupedDTOs.put(entry.getKey(), organizationMapper.toDTO(entry.getValue()));
        }

        return groupedDTOs;
    }
}
