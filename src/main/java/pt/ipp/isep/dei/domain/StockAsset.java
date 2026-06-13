package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * Represents the detail of a stock asset declared in a Declaration of Interests.
 */
public class StockAsset implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String description;

    /**
     * Creates a new StockAsset detail.
     *
     * @param description a description of the stock or financial instrument.
     * @throws IllegalArgumentException if description is null or blank.
     */
    public StockAsset(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        this.description = description;
    }

    /**
     * Gets description.
     *
     * @return the description of the stock or financial instrument.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
