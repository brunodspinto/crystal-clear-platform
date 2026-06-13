package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.ComplaintAssessment;
import pt.ipp.isep.dei.domain.ComplaintOutcome;
import pt.ipp.isep.dei.domain.EthicsCommitteeMember;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.ComplaintAssessmentRepository;
import pt.ipp.isep.dei.repository.ComplaintRepository;
import pt.ipp.isep.dei.repository.EthicsCommitteeMemberRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for handling the assessment of a citizen complaint (US27).
 */
public class AssessComplaintController {

    private final ComplaintRepository complaintRepository;
    private final ComplaintAssessmentRepository complaintAssessmentRepository;
    private final EthicsCommitteeMemberRepository ethicsCommitteeMemberRepository;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public AssessComplaintController() {
        Repositories repos = Repositories.getInstance();
        this.complaintRepository = repos.getComplaintRepository();
        this.complaintAssessmentRepository = repos.getComplaintAssessmentRepository();
        this.ethicsCommitteeMemberRepository = repos.getEthicsCommitteeMemberRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     *
     * @param complaintRepository           the complaint repository.
     * @param complaintAssessmentRepository the complaint assessment repository.
     * @param ethicsCommitteeMemberRepository the ethics committee member repository.
     * @param authenticationRepository      the authentication repository.
     */
    public AssessComplaintController(ComplaintRepository complaintRepository,
                                     ComplaintAssessmentRepository complaintAssessmentRepository,
                                     EthicsCommitteeMemberRepository ethicsCommitteeMemberRepository,
                                     AuthenticationRepository authenticationRepository) {
        this.complaintRepository = complaintRepository;
        this.complaintAssessmentRepository = complaintAssessmentRepository;
        this.ethicsCommitteeMemberRepository = ethicsCommitteeMemberRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Returns the complaints that are still waiting to be assessed, i.e. every
     * submitted complaint that does not yet have a {@link ComplaintAssessment}.
     * Once a complaint is assessed it no longer appears here.
     *
     * @return list of complaints pending assessment.
     */
    public List<Complaint> getComplaints() {
        List<Complaint> assessed = new ArrayList<>();
        for (ComplaintAssessment a : complaintAssessmentRepository.getAssessments()) {
            assessed.add(a.getComplaint());
        }
        List<Complaint> pending = new ArrayList<>();
        for (Complaint complaint : complaintRepository.getComplaints()) {
            if (!assessed.contains(complaint)) {
                pending.add(complaint);
            }
        }
        return pending;
    }

    /**
     * Assesses a complaint as valid or invalid on behalf of the logged-in Ethics Committee Member.
     * A reason is required when the outcome is INVALID.
     *
     * @param complaint the complaint to assess.
     * @param outcome   the assessment outcome.
     * @param reason    the reason for an INVALID outcome; may be null or blank when outcome is VALID.
     * @return {@code true} if the assessment was saved; {@code false} if the member was not found.
     */
    public boolean assessComplaint(Complaint complaint, ComplaintOutcome outcome, String reason) {
        Email email = authenticationRepository.getCurrentUserSession().getUserId();
        EthicsCommitteeMember member = ethicsCommitteeMemberRepository.getByEmail(email.getEmail());
        if (member == null) {
            return false;
        }
        ComplaintAssessment assessment = new ComplaintAssessment(member, complaint, new Date(), outcome, reason);
        return complaintAssessmentRepository.save(assessment);
    }
}
