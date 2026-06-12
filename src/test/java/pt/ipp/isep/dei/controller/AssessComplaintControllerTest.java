package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class AssessComplaintControllerTest {

    private static final Date PAST_DATE = new Date(0);

    private Complaint createComplaint() {
        Citizen citizen = new Citizen("citizen@test.com", "John Citizen", "CC111111111");
        PoliticalAgent agent = new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678",
                "123456789", new Date(), null);
        return new Complaint("Test complaint", PAST_DATE, citizen, agent, PoliticalFunction.MAYOR);
    }

    private AuthenticationRepository createAuthRepoLoggedInAs(String email, String name) {
        AuthenticationRepository authRepo = new AuthenticationRepository();
        authRepo.addUserRole("ETHICS", "Ethics Committee");
        authRepo.addUserWithRole(name, email, "pass", "ETHICS");
        authRepo.doLogin(email, "pass");
        return authRepo;
    }

    private AssessComplaintController createController(String memberEmail,
                                                       ComplaintRepository complaintRepo,
                                                       ComplaintAssessmentRepository assessmentRepo) {
        EthicsCommitteeMemberRepository memberRepo = new EthicsCommitteeMemberRepository();
        memberRepo.save(new EthicsCommitteeMember("EC Member", memberEmail));

        AuthenticationRepository authRepo = createAuthRepoLoggedInAs(memberEmail, "EC Member");

        return new AssessComplaintController(complaintRepo, assessmentRepo, memberRepo, authRepo);
    }

    @Test
    void ensureGetComplaintsReturnsAll() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        complaintRepo.save(createComplaint());
        complaintRepo.save(createComplaint());

        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, new ComplaintAssessmentRepository());

        assertEquals(2, controller.getComplaints().size());
    }

    @Test
    void ensureGetComplaintsReturnsEmptyWhenNone() {
        AssessComplaintController controller = createController("ec@test.com",
                new ComplaintRepository(), new ComplaintAssessmentRepository());

        assertTrue(controller.getComplaints().isEmpty());
    }

    @Test
    void ensureAssessComplaintValidWorks() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, assessmentRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.VALID, null);

        assertTrue(result);
        assertEquals(1, assessmentRepo.getAssessments().size());
        assertEquals(ComplaintOutcome.VALID, assessmentRepo.getAssessments().get(0).getOutcome());
    }

    @Test
    void ensureAssessComplaintInvalidWithReasonWorks() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, assessmentRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.INVALID, "No evidence.");

        assertTrue(result);
        assertEquals(ComplaintOutcome.INVALID, assessmentRepo.getAssessments().get(0).getOutcome());
        assertEquals("No evidence.", assessmentRepo.getAssessments().get(0).getReason());
    }

    @Test
    void ensureAssessComplaintFailsWhenMemberNotInRepository() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        EthicsCommitteeMemberRepository memberRepo = new EthicsCommitteeMemberRepository();
        // member not saved in the repository
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("unknown@ec.pt", "Unknown");

        AssessComplaintController controller = new AssessComplaintController(
                complaintRepo, assessmentRepo, memberRepo, authRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.VALID, null);

        assertFalse(result);
        assertTrue(assessmentRepo.getAssessments().isEmpty());
    }

    @Test
    void ensureAssessComplaintInvalidWithoutReasonThrows() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, new ComplaintAssessmentRepository());

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.assessComplaint(complaint, ComplaintOutcome.INVALID, null);
            }
        });
    }
}
