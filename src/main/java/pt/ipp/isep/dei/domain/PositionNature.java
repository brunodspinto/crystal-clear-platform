package pt.ipp.isep.dei.domain;

/**
 * Represents the legal nature of a position held by a political agent.
 */
public enum PositionNature {

    /**
     * A position held in a public institution.
     */
    PUBLIC("Public"),

    /**
     * A position held in a private entity.
     */
    PRIVATE("Private"),

    /**
     * A position held in a social or non-profit entity.
     */
    SOCIAL("Social");

    private final String label;

    PositionNature(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this position nature.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
