package pt.ipp.isep.dei.domain;

/**
 * Represents the political function held by an agent at the time of a reported behavior.
 */
public enum PoliticalFunction {
    /**
     * A government minister.
     */
    MINISTER("Minister"),
    /**
     * A member of parliament.
     */
    DEPUTY("Deputy"),
    /**
     * A member of a local council.
     */
    COUNCILLOR("Councillor"),
    /**
     * President of a parish council (Junta de Freguesia).
     */
    PARISH_COUNCIL_PRESIDENT("Parish Council President"),
    /**
     * Mayor of a municipality.
     */
    MAYOR("Mayor");

    private final String label;

    PoliticalFunction(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this political function.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
