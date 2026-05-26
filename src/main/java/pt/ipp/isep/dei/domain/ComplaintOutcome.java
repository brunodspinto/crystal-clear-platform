package pt.ipp.isep.dei.domain;

/**
 * Represents the outcome of an Ethics Committee assessment of a complaint.
 */
public enum ComplaintOutcome {

    /**
     * The complaint was found to be valid.
     */
    VALID("Valid"),

    /**
     * The complaint was found to be invalid.
     */
    INVALID("Invalid");

    private final String label;

    ComplaintOutcome(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
