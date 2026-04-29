package pt.ipp.isep.dei.domain;

/**
 * Represents the category of an asset declared in a Declaration of Interests.
 */
public enum AssetType {

    /** Real estate property (urban or rural). */
    REAL_ESTATE("Real Estate"),

    /** Vehicles (cars, boats, aircraft, etc.). */
    VEHICLES("Vehicles"),

    /** Stocks, shares, and financial instruments. */
    STOCKS("Stocks");

    private final String label;

    AssetType(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this asset type.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
