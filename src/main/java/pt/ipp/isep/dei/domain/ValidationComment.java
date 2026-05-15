package pt.ipp.isep.dei.domain;

/**
 * Represents a comment attached to a specific section of a rejected Declaration of Interests.
 * A ValidationComment is always part of a {@link ValidationRecord} and is created
 * when a declaration is returned for correction (AC2 of US08).
 */
public class ValidationComment {

    private final String section;
    private final String comment;

    /**
     * Creates a new ValidationComment.
     *
     * @param section the section of the declaration that contains the inconsistency.
     * @param comment a description of the inconsistency found in that section.
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public ValidationComment(String section, String comment) {
        if (section == null || section.isBlank()) {
            throw new IllegalArgumentException("Section cannot be null or empty.");
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be null or empty.");
        }
        this.section = section;
        this.comment = comment;
    }

    /**
     * Gets section.
     *
     * @return the section of the declaration that contains the inconsistency.
     */
    public String getSection() { return section; }

    /**
     * Gets comment.
     *
     * @return the description of the inconsistency.
     */
    public String getComment() { return comment; }

    @Override
    public String toString() {
        return String.format("Section: '%s' — %s", section, comment);
    }
}
