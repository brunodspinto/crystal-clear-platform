package pt.ipp.isep.dei.domain;

/**
 * Represents the relationship of a household member to the political agent
 * (US06, AC1): spouse/partner, descendant, or another household member.
 */
public enum HouseholdRelation {

    /**
     * Spouse or partner of the political agent.
     */
    SPOUSE("Spouse / Partner"),

    /**
     * Descendant of the political agent (child, grandchild).
     */
    DESCENDANT("Descendant"),

    /**
     * Another member of the household not covered by the other relations.
     */
    OTHER("Other");

    private final String label;

    HouseholdRelation(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this household relation.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
