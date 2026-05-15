package pt.ipp.isep.dei.domain;

/**
 * Represents the type of a Declaration of Interests.
 */
public enum DeclarationType {

    /**
     * Submitted when beginning a term of office.
     */
    INITIAL("Initial"),

    /**
     * Submitted annually while performing a political function.
     */
    REGULAR("Regular"),

    /**
     * Submitted when significant changes occur or when requested by the ethics committee.
     */
    EXCEPTIONAL("Exceptional");

    private final String label;

    DeclarationType(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this declaration type.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
