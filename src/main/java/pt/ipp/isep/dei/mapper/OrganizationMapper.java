package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.dto.OrganizationDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT DTO pattern) that converts {@link Organization} domain
 * objects into {@link OrganizationDTO}. The controller delegates this
 * conversion to the mapper so the domain layer is not exposed to the UI.
 */
public class OrganizationMapper {

    /**
     * Converts a single organization into its DTO.
     *
     * @param organization the domain organization.
     * @return the corresponding {@link OrganizationDTO}.
     */
    public OrganizationDTO toDTO(Organization organization) {
        String typeDesignation = organization.getType() != null ? organization.getType().toString() : "";
        String natureDesignation = organization.getNature() != null ? organization.getNature().toString() : "";
        return new OrganizationDTO(organization.getName(), typeDesignation, natureDesignation);
    }

    /**
     * Converts a list of organizations into a list of DTOs.
     *
     * @param organizations the domain organizations.
     * @return the corresponding list of {@link OrganizationDTO}.
     */
    public List<OrganizationDTO> toDTO(List<Organization> organizations) {
        List<OrganizationDTO> dtos = new ArrayList<>();
        for (Organization organization : organizations) {
            dtos.add(toDTO(organization));
        }
        return dtos;
    }
}
