package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintRepositoryTest {

    private Complaint createTestComplaint(String description) {
        Citizen citizen = new Citizen("citizen@test.com", "Test Citizen", "CC333333333");
        PoliticalAgent agent = new PoliticalAgent("Agent", "agent@gov.pt", "12345678", "123456789",
                new Date(), null);
        return new Complaint(description, new Date(0), citizen, agent, PoliticalFunction.MAYOR);
    }

    @Test
    void ensureSaveComplaintWorks() {
        ComplaintRepository repo = new ComplaintRepository();
        Complaint complaint = createTestComplaint("A valid complaint.");
        assertTrue(repo.save(complaint));
    }

    @Test
    void ensureGetComplaintsReturnsAllSaved() {
        ComplaintRepository repo = new ComplaintRepository();
        repo.save(createTestComplaint("Complaint 1"));
        repo.save(createTestComplaint("Complaint 2"));
        assertEquals(2, repo.getComplaints().size());
    }

    @Test
    void ensureGetComplaintsReturnsDefensiveCopy() {
        ComplaintRepository repo = new ComplaintRepository();
        repo.save(createTestComplaint("A complaint"));
        assertNotSame(repo.getComplaints(), repo.getComplaints());
    }

    @Test
    void ensureEmptyRepositoryReturnsEmptyList() {
        ComplaintRepository repo = new ComplaintRepository();
        assertTrue(repo.getComplaints().isEmpty());
    }

    @Test
    void ensureCreateComplaintInstantiatesWithoutStoring() {
        ComplaintRepository repo = new ComplaintRepository();
        Citizen citizen = new Citizen("citizen@test.com", "Test Citizen", "CC333333333");
        PoliticalAgent agent = new PoliticalAgent("Agent", "agent@gov.pt", "12345678", "123456789",
                new Date(), null);

        Complaint complaint = repo.createComplaint(citizen, agent);

        assertNotNull(complaint);
        assertEquals(0, complaint.getItemCount());
        assertTrue(repo.getComplaints().isEmpty()); // created (Creator) but not stored until save
    }
}
