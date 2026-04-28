package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintTest {

    private static final Date PAST_DATE = new Date(0); // 1 Jan 1970 — always in the past

    private Citizen createTestCitizen() {
        return new Citizen("citizen@test.com", "John Citizen");
    }

    private PoliticalAgent createTestAgent() {
        return new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678", "123456789",
                new Date(), null);
    }

    @Test
    void ensureComplaintCreationWorks() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        Complaint complaint = new Complaint("This is a valid complaint.", PAST_DATE,
                citizen, agent, PoliticalFunction.MAYOR);
        assertNotNull(complaint);
    }

    @Test
    void ensureComplaintFailsWithNullDescription() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint(null, PAST_DATE, citizen, agent, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintFailsWithBlankDescription() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("   ", PAST_DATE, citizen, agent, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintFailsWithNullDate() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", null, citizen, agent, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintFailsWithFutureDate() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        Date futureDate = new Date(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", futureDate, citizen, agent, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintWithTodayDateWorks() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        Complaint complaint = new Complaint("Description", new Date(), citizen, agent, PoliticalFunction.COUNCILLOR);
        assertNotNull(complaint);
    }

    @Test
    void ensureComplaintFailsWithNullCitizen() {
        PoliticalAgent agent = createTestAgent();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, null, agent, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintFailsWithNullPoliticalAgent() {
        Citizen citizen = createTestCitizen();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, citizen, null, PoliticalFunction.MAYOR));
    }

    @Test
    void ensureComplaintFailsWithNullPoliticalFunction() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, citizen, agent, null));
    }

    @Test
    void ensureSubmissionDateIsSetAutomatically() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        Complaint complaint = new Complaint("Description", PAST_DATE, citizen, agent, PoliticalFunction.DEPUTY);
        assertNotNull(complaint.getSubmissionDate());
        assertFalse(complaint.getSubmissionDate().after(new Date()));
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        Citizen citizen = createTestCitizen();
        PoliticalAgent agent = createTestAgent();
        Complaint complaint = new Complaint("My complaint description", PAST_DATE,
                citizen, agent, PoliticalFunction.MINISTER);

        assertEquals("My complaint description", complaint.getDescription());
        assertEquals(PAST_DATE, complaint.getComplaintDate());
        assertEquals(citizen, complaint.getCitizen());
        assertEquals(agent, complaint.getPoliticalAgent());
        assertEquals(PoliticalFunction.MINISTER, complaint.getPoliticalFunction());
    }
}
