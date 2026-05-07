package pt.ipp.isep.dei.domain;

/**
 * Represents the outcome of a validation action performed by an Ethics Committee Member.
 */
public enum ValidationOutcome {

    /** The declaration was found correct and has been validated. */
    VALIDATED("Validated"),

    /** The declaration contains inconsistencies and has been returned for correction. */
    RETURNED_FOR_CORRECTION("Returned for Correction");

    private final String label;

    ValidationOutcome(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
