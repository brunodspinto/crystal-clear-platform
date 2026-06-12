package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * Represents a single household member declared in a Declaration of Interests
 * (US06, AC1). Holds the member's full name and their relationship to the
 * political agent.
 */
public class HouseholdMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final HouseholdRelation relation;

    /**
     * Creates a new household member.
     *
     * @param name     the full name of the household member; cannot be blank.
     * @param relation the relationship to the political agent; cannot be null.
     * @throws IllegalArgumentException if the name is blank or the relation is null.
     */
    public HouseholdMember(String name, HouseholdRelation relation) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Household member name cannot be blank.");
        }
        if (relation == null) {
            throw new IllegalArgumentException("Household relation cannot be null.");
        }
        this.name = name.trim();
        this.relation = relation;
    }

    /**
     * Gets the household member's name.
     *
     * @return the full name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the household member's relationship to the agent.
     *
     * @return the relation.
     */
    public HouseholdRelation getRelation() {
        return relation;
    }

    @Override
    public String toString() {
        return name + " (" + relation + ")";
    }
}
