package pt.ipp.isep.dei.domain;

/**
 * Represents the current status of a Declaration of Interests.
 */
public enum DeclarationStatus {

    /**
     * The declaration has been submitted and is awaiting validation by the Ethics Committee.
     */
    PENDING("Pending"),

    /**
     * The declaration has been validated by the Ethics Committee.
     */
    VALIDATED("Validated"),

    /**
     * The declaration has been rejected by the Ethics Committee and returned for correction.
     */
    REJECTED("Rejected");

    private final String label;

    DeclarationStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the human-readable label for this declaration status.
     *
     * @return the display label.
     */
    @Override
    public String toString() {
        return label;
    }
}
