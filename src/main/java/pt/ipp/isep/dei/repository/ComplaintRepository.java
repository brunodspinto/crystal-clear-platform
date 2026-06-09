package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Citizen;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link Complaint} instances.
 * Allows duplicate complaints (the same citizen may submit multiple complaints).
 */
public class ComplaintRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Complaint> complaints;

    /**
     * Creates an empty ComplaintRepository.
     */
    public ComplaintRepository() {
        complaints = new ArrayList<>();
    }

    /**
     * Creates a new (empty) complaint for the given citizen and political agent.
     * Following the GRASP <b>Creator</b> pattern, the repository &mdash; which
     * records all {@link Complaint} instances &mdash; is responsible for
     * instantiating them; the complaint validates its own citizen and agent in its
     * constructor (Information Expert). The complaint is only stored later, via
     * {@link #save}, once the citizen has finished adding grievances.
     *
     * @param citizen        the citizen submitting the complaint.
     * @param politicalAgent the political agent the complaint is about.
     * @return the newly created (not yet stored) complaint.
     */
    public Complaint createComplaint(Citizen citizen, PoliticalAgent politicalAgent) {
        return new Complaint(citizen, politicalAgent);
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
