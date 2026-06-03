package pt.ipp.isep.dei.domain;

/**
 * Represents the legal nature of an organization registered in the system.
 * The Administrator selects one of these predefined values (US04).
 */
public enum OrganizationNature {

    /**
     * A public organization (e.g. a state institution).
     */
    PUBLIC("Public"),

    /**
     * A private organization (e.g. a company).
     */
    PRIVATE("Private"),

    /**
     * A social or non-profit organization (e.g. a foundation).
     */
    SOCIAL("Social");

    private final String label;

    OrganizationNature(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this organization nature.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
