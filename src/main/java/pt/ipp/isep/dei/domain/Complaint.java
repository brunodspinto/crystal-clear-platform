package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a complaint submitted by a citizen about a political agent.
 * A complaint targets a single political agent but may contain several
 * grievances ({@link ComplaintItem}), each with its own description, date and
 * the political function the agent held at the time of that behaviour.
 * The citizen, the political agent and the submission date are immutable;
 * grievances are added through {@link #addItem(String, Date, PoliticalFunction)}.
 */
public class Complaint implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Citizen citizen;
    private final PoliticalAgent politicalAgent;
    private final Date submissionDate;
    private final List<ComplaintItem> items;

    /**
     * Creates an empty complaint about a political agent. Grievances must be added
     * afterwards with {@link #addItem(String, Date, PoliticalFunction)} before the
     * complaint is saved.
     *
     * @param citizen        the citizen submitting the complaint.
     * @param politicalAgent the political agent the complaint is about.
     * @throws IllegalArgumentException if the citizen or the political agent is null.
     */
    public Complaint(Citizen citizen, PoliticalAgent politicalAgent) {
        if (citizen == null) {
            throw new IllegalArgumentException("Citizen cannot be null");
        }
        if (politicalAgent == null) {
            throw new IllegalArgumentException("Political agent cannot be null");
        }
        this.citizen = citizen;
        this.politicalAgent = politicalAgent;
        this.submissionDate = new Date();
        this.items = new ArrayList<>();
    }

    /**
     * Creates a complaint with a single grievance. Kept for backward compatibility
     * with callers that submit one grievance at a time.
     *
     * @param description       a description of the reported behaviour.
     * @param complaintDate     the date when the behaviour occurred (cannot be in the future).
     * @param citizen           the citizen submitting the complaint.
     * @param politicalAgent    the political agent being complained about.
     * @param politicalFunction the function the agent held at the time of the behaviour.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public Complaint(String description, Date complaintDate, Citizen citizen,
                     PoliticalAgent politicalAgent, PoliticalFunction politicalFunction) {
        this(citizen, politicalAgent);
        addItem(description, complaintDate, politicalFunction);
    }

    /**
     * Adds a grievance to this complaint. All grievances refer to the same
     * political agent as the complaint.
     *
     * @param description       a description of the reported behaviour.
     * @param complaintDate     the date when the behaviour occurred (cannot be in the future).
     * @param politicalFunction the function the agent held at the time of the behaviour.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public void addItem(String description, Date complaintDate, PoliticalFunction politicalFunction) {
        items.add(new ComplaintItem(description, complaintDate, politicalFunction));
    }

    /**
     * @return an unmodifiable copy of the grievances of this complaint.
     */
    public List<ComplaintItem> getItems() {
        return List.copyOf(items);
    }

    /**
     * @return the number of grievances in this complaint.
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * @return the citizen who submitted the complaint.
     */
    public Citizen getCitizen() {
        return citizen;
    }

    /**
     * @return the political agent being complained about.
     */
    public PoliticalAgent getPoliticalAgent() {
        return politicalAgent;
    }

    /**
     * @return the date when the complaint was submitted (set automatically).
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @return the description of the first grievance, or {@code null} if there is none.
     */
    public String getDescription() {
        return items.isEmpty() ? null : items.get(0).getDescription();
    }

    /**
     * @return the date of the first grievance, or {@code null} if there is none.
     */
    public Date getComplaintDate() {
        return items.isEmpty() ? null : items.get(0).getComplaintDate();
    }

    /**
     * @return the political function of the first grievance, or {@code null} if there is none.
     */
    public PoliticalFunction getPoliticalFunction() {
        return items.isEmpty() ? null : items.get(0).getPoliticalFunction();
    }

    @Override
    public String toString() {
        return String.format("Complaint about %s — %d grievance(s)",
                politicalAgent.getName(), items.size());
    }
}
