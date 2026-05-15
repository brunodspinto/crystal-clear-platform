package pt.ipp.isep.dei.domain;

/**
 * Represents the detail of a real estate asset declared in a Declaration of Interests.
 */
public class RealEstate {

    private final String description;
    private final String municipality;

    /**
     * Creates a new RealEstate detail.
     *
     * @param description  a description of the property.
     * @param municipality the municipality where the property is located.
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public RealEstate(String description, String municipality) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (municipality == null || municipality.isBlank()) {
            throw new IllegalArgumentException("Municipality cannot be null or empty.");
        }
        this.description = description;
        this.municipality = municipality;
    }

    /**
     * Gets description.
     *
     * @return the description of the property.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets municipality.
     *
     * @return the municipality where the property is located.
     */
    public String getMunicipality() {
        return municipality;
    }

    @Override
    public String toString() {
        return String.format("RealEstate{description='%s', municipality='%s'}", description, municipality);
    }
}
