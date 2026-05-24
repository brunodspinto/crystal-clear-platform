package pt.ipp.isep.dei.domain.graph;

/**
 * Family relationship types used by US37 when inferring family ties from
 * the Declaration of Interests. Each type carries a label (used in the
 * relations CSV), a symmetric flag, and an inverse type when applicable.
 */
public enum FamilyRelationshipType {

    SPOUSE_OF("spouseOf", true),
    SIBLING_OF("siblingOf", true),
    PARENT_OF("parentOf", false),
    CHILD_OF("childOf", false),
    GRANDPARENT_OF("grandparentOf", false),
    GRANDCHILD_OF("grandchildOf", false);

    private final String label;
    private final boolean symmetric;

    FamilyRelationshipType(String label, boolean symmetric) {
        this.label = label;
        this.symmetric = symmetric;
    }

    /**
     * Gets the label used to represent this relationship type in the CSV file.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Whether this relationship is symmetric (e.g. spouseOf).
     *
     * @return true if symmetric
     */
    public boolean isSymmetric() {
        return symmetric;
    }

    /**
     * Returns the inverse relationship type. Symmetric types return themselves.
     *
     * @return the inverse type
     */
    public FamilyRelationshipType getInverse() {
        switch (this) {
            case SPOUSE_OF:       return SPOUSE_OF;
            case SIBLING_OF:      return SIBLING_OF;
            case PARENT_OF:       return CHILD_OF;
            case CHILD_OF:        return PARENT_OF;
            case GRANDPARENT_OF:  return GRANDCHILD_OF;
            case GRANDCHILD_OF:   return GRANDPARENT_OF;
            default:
                throw new IllegalStateException("Unknown type: " + this);
        }
    }

    /**
     * Looks up a relationship type by its label.
     *
     * @param label the label (e.g. "parentOf")
     * @return the matching type, or {@code null} if none matches
     */
    public static FamilyRelationshipType fromLabel(String label) {
        if (label == null) {
            return null;
        }
        for (FamilyRelationshipType t : values()) {
            if (t.label.equals(label)) {
                return t;
            }
        }
        return null;
    }
}
