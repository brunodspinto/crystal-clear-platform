package pt.ipp.isep.dei.domain.graph;

/**
 * The type Organization.
 */
public class Organization extends Entity {

    private final String name;
    private final String organizationType;
    private final String country;

    /**
     * Instantiates a new Organization.
     *
     * @param id               the id
     * @param type             the type
     * @param startDate        the start date
     * @param endDate          the end date
     * @param name             the name
     * @param organizationType the organization type
     * @param country          the country
     */
    public Organization(String id, String type, String startDate, String endDate,
                        String name, String organizationType, String country) {
        super(id, type, startDate, endDate);
        this.name = name == null ? "" : name;
        this.organizationType = organizationType == null ? "" : organizationType;
        this.country = country == null ? "" : country;
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
     * Gets organization type.
     *
     * @return the organization type
     */
    public String getOrganizationType() {
        return organizationType;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Organization{id='" + getId() + "', name='" + name + "', organizationType='" + organizationType + "', country='" + country + "'}";
    }
}
