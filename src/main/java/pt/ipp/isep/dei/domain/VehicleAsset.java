package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * Represents the detail of a vehicle asset declared in a Declaration of Interests.
 */
public class VehicleAsset implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String description;

    /**
     * Creates a new VehicleAsset detail.
     *
     * @param description a description of the vehicle.
     * @throws IllegalArgumentException if description is null or blank.
     */
    public VehicleAsset(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        this.description = description;
    }

    /**
     * Gets description.
     *
     * @return the description of the vehicle.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
