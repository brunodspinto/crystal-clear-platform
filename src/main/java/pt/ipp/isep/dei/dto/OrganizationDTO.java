package pt.ipp.isep.dei.dto;

/**
 * Data transfer object for an organization.
 * Used to pass display data to the UI without exposing the domain entity.
 */
public class OrganizationDTO {

    private final String name;
    private final String typeDesignation;
    private final String natureDesignation;

    /**
     * Instantiates a new Organization dto.
     *
     * @param name              the organization name
     * @param typeDesignation   the designation of the organization type
     * @param natureDesignation the designation of the organization nature
     */
    public OrganizationDTO(String name, String typeDesignation, String natureDesignation) {
        this.name = name;
        this.typeDesignation = typeDesignation;
        this.natureDesignation = natureDesignation;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets type designation.
     *
     * @return the type designation
     */
    public String getTypeDesignation() {
        return typeDesignation;
    }

    /**
     * Gets nature designation.
     *
     * @return the nature designation
     */
    public String getNatureDesignation() {
        return natureDesignation;
    }

    @Override
    public String toString() {
        return name;
    }
}
