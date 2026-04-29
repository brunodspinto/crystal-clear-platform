package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Complaint;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link Complaint} instances.
 * Allows duplicate complaints (the same citizen may submit multiple complaints).
 */
public class ComplaintRepository {
    private final List<Complaint> complaints;

    /**
     * Creates an empty ComplaintRepository.
     */
    public ComplaintRepository() {
        complaints = new ArrayList<>();
    }

    /**
     * Saves a complaint. Duplicates are allowed.
     *
     * @param complaint the complaint to save.
     * @return {@code true} always, since duplicates are permitted.
     */
    public boolean save(Complaint complaint) {
        return complaints.add(complaint);
    }

    /**
     * Returns an unmodifiable copy of all submitted complaints.
     *
     * @return list of all complaints.
     */
    public List<Complaint> getComplaints() {
        return List.copyOf(complaints);
    }
}
