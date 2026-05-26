package pt.ipp.isep.dei.domain;

import java.util.Date;

/**
 * Represents the assessment of a complaint by an Ethics Committee Member.
 * If the outcome is INVALID, a reason must be provided.
 */
public class ComplaintAssessment {

    private final EthicsCommitteeMember member;
    private final Complaint complaint;
    private final Date assessmentDate;
    private final ComplaintOutcome outcome;
    private final String reason;

    /**
     * Creates a new ComplaintAssessment.
     *
     * @param member         the Ethics Committee Member performing the assessment.
     * @param complaint      the complaint being assessed.
     * @param assessmentDate the date of the assessment.
     * @param outcome        the result of the assessment.
     * @param reason         the reason if the outcome is INVALID; must be non-blank in that case.
     * @throws IllegalArgumentException if any required argument is invalid.
     */
    public ComplaintAssessment(EthicsCommitteeMember member, Complaint complaint,
                               Date assessmentDate, ComplaintOutcome outcome, String reason) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (complaint == null) {
            throw new IllegalArgumentException("Complaint cannot be null.");
        }
        if (assessmentDate == null) {
            throw new IllegalArgumentException("Assessment date cannot be null.");
        }
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null.");
        }
        if (outcome == ComplaintOutcome.INVALID && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("A reason must be provided when the outcome is INVALID.");
        }
        this.member = member;
        this.complaint = complaint;
        this.assessmentDate = assessmentDate;
        this.outcome = outcome;
        this.reason = (outcome == ComplaintOutcome.INVALID) ? reason : null;
    }

    /**
     * Gets member.
     *
     * @return the Ethics Committee Member who performed the assessment.
     */
    public EthicsCommitteeMember getMember() { return member; }

    /**
     * Gets complaint.
     *
     * @return the complaint that was assessed.
     */
    public Complaint getComplaint() { return complaint; }

    /**
     * Gets assessment date.
     *
     * @return the date of the assessment.
     */
    public Date getAssessmentDate() { return assessmentDate; }

    /**
     * Gets outcome.
     *
     * @return the outcome of the assessment.
     */
    public ComplaintOutcome getOutcome() { return outcome; }

    /**
     * Gets reason.
     *
     * @return the reason for an INVALID outcome, or null if the outcome is VALID.
     */
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return String.format("ComplaintAssessment{complaint=%s, outcome=%s, date=%s}",
                complaint, outcome, assessmentDate);
    }
}
