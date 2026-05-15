package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the record of a single validation action performed by an Ethics Committee Member
 * on a Declaration of Interests. Each validation (approval or rejection) produces exactly
 * one ValidationRecord, stored permanently for audit purposes (AC4 of US08).
 */
public class ValidationRecord {

    private final EthicsCommitteeMember member;
    private final Declaration declaration;
    private final Date validationDate;
    private final ValidationOutcome outcome;
    private final List<ValidationComment> comments;

    /**
     * Creates a new ValidationRecord.
     *
     * @param member         the Ethics Committee Member who performed the validation.
     * @param declaration    the declaration being validated.
     * @param validationDate the date of the validation action.
     * @param outcome        the result of the validation.
     * @throws IllegalArgumentException if any argument is null.
     */
    public ValidationRecord(EthicsCommitteeMember member, Declaration declaration,
                             Date validationDate, ValidationOutcome outcome) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (declaration == null) {
            throw new IllegalArgumentException("Declaration cannot be null.");
        }
        if (validationDate == null) {
            throw new IllegalArgumentException("Validation date cannot be null.");
        }
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null.");
        }
        this.member = member;
        this.declaration = declaration;
        this.validationDate = validationDate;
        this.outcome = outcome;
        this.comments = new ArrayList<>();
    }

    /**
     * Adds a comment identifying a section with an inconsistency (AC2 of US08).
     *
     * @param section the section of the declaration containing the issue.
     * @param comment a description of the inconsistency.
     * @throws IllegalArgumentException if section or comment is null or blank.
     */
    public void addComment(String section, String comment) {
        comments.add(new ValidationComment(section, comment));
    }

    /**
     * Gets member.
     *
     * @return the Ethics Committee Member who performed the validation.
     */
    public EthicsCommitteeMember getMember() { return member; }

    /**
     * Gets declaration.
     *
     * @return the declaration that was validated.
     */
    public Declaration getDeclaration() { return declaration; }

    /**
     * Gets validation date.
     *
     * @return the date of the validation action.
     */
    public Date getValidationDate() { return validationDate; }

    /**
     * Gets outcome.
     *
     * @return the outcome of the validation.
     */
    public ValidationOutcome getOutcome() { return outcome; }

    /**
     * Gets comments.
     *
     * @return a defensive copy of the list of comments.
     */
    public List<ValidationComment> getComments() { return new ArrayList<>(comments); }

    @Override
    public String toString() {
        return String.format("ValidationRecord{member='%s', outcome=%s, date=%s, comments=%d}",
                member.getName(), outcome, validationDate, comments.size());
    }
}
