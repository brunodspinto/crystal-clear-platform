package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class ComplaintAssessmentTest {

    private static final Date PAST_DATE = new Date(0);

    private Complaint createComplaint() {
        Citizen citizen = new Citizen("citizen@test.com", "John Citizen", "CC111111111");
        PoliticalAgent agent = new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678", "123456789",
                new Date(), null);
        return new Complaint("Test complaint", PAST_DATE, citizen, agent, PoliticalFunction.MAYOR);
    }

    private EthicsCommitteeMember createMember() {
        return new EthicsCommitteeMember("EC Member", "member@ec.pt");
    }

    @Test
    void ensureValidAssessmentCreationWorks() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.VALID, null);
        assertNotNull(assessment);
    }

    @Test
    void ensureInvalidAssessmentWithReasonWorks() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.INVALID, "Lacks evidence.");
        assertNotNull(assessment);
        assertEquals("Lacks evidence.", assessment.getReason());
    }

    @Test
    void ensureInvalidOutcomeWithoutReasonThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(createMember(), createComplaint(), new Date(),
                            ComplaintOutcome.INVALID, null);
            }
        });
    }

    @Test
    void ensureInvalidOutcomeWithBlankReasonThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(createMember(), createComplaint(), new Date(),
                            ComplaintOutcome.INVALID, "   ");
            }
        });
    }

    @Test
    void ensureValidOutcomeHasNullReason() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.VALID, "ignored");
        assertNull(assessment.getReason());
    }

    @Test
    void ensureNullMemberThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(null, createComplaint(), new Date(), ComplaintOutcome.VALID, null);
            }
        });
    }

    @Test
    void ensureNullComplaintThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(createMember(), null, new Date(), ComplaintOutcome.VALID, null);
            }
        });
    }

    @Test
    void ensureNullDateThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(createMember(), createComplaint(), null, ComplaintOutcome.VALID, null);
            }
        });
    }

    @Test
    void ensureNullOutcomeThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ComplaintAssessment(createMember(), createComplaint(), new Date(), null, null);
            }
        });
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        EthicsCommitteeMember member = createMember();
        Complaint complaint = createComplaint();
        Date date = new Date();
        ComplaintAssessment assessment = new ComplaintAssessment(
                member, complaint, date, ComplaintOutcome.INVALID, "No proof.");

        assertEquals(member, assessment.getMember());
        assertEquals(complaint, assessment.getComplaint());
        assertEquals(date, assessment.getAssessmentDate());
        assertEquals(ComplaintOutcome.INVALID, assessment.getOutcome());
        assertEquals("No proof.", assessment.getReason());
    }
}
