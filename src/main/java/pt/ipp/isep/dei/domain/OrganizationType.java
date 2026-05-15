package pt.ipp.isep.dei.domain;

/**
 * Represents the type of an organization that can be registered in the system.
 */
public enum OrganizationType {
    /**
     * A private or public company.
     */
    COMPANY("Company"),
    /**
     * A political party.
     */
    POLITICAL_PARTY("Political Party"),
    /**
     * A non-profit foundation.
     */
    FOUNDATION("Foundation"),
    /**
     * A public or private institute.
     */
    INSTITUTE("Institute"),
    /**
     * A civil or professional association.
     */
    ASSOCIATION("Association");

    private final String label;

    OrganizationType(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this organization type.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
