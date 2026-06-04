package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a complaint submitted by a citizen about a political agent.
 * All fields are immutable after creation. The submission date is set automatically.
 */
public class Complaint implements Serializable {
    private final String description;
    private final Date complaintDate;
    private final Date submissionDate;
    private final Citizen citizen;
    private final PoliticalAgent politicalAgent;
    private final PoliticalFunction politicalFunction;

    /**
     * Creates a new Complaint.
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
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (complaintDate == null) {
            throw new IllegalArgumentException("Complaint date cannot be null");
        }
        if (complaintDate.after(new Date())) {
            throw new IllegalArgumentException("Complaint date cannot be in the future");
        }
        if (citizen == null) {
            throw new IllegalArgumentException("Citizen cannot be null");
        }
        if (politicalAgent == null) {
            throw new IllegalArgumentException("Political agent cannot be null");
        }
        if (politicalFunction == null) {
            throw new IllegalArgumentException("Political function cannot be null");
        }
        this.description = description;
        this.complaintDate = complaintDate;
        this.submissionDate = new Date();
        this.citizen = citizen;
        this.politicalAgent = politicalAgent;
        this.politicalFunction = politicalFunction;
    }

    /**
     * Gets description.
     *
     * @return the description of the reported behaviour.
     */
    public String getDescription() { return description; }

    /**
     * Gets complaint date.
     *
     * @return the date when the behaviour occurred.
     */
    public Date getComplaintDate() { return complaintDate; }

    /**
     * Gets submission date.
     *
     * @return the date when the complaint was submitted (set automatically).
     */
    public Date getSubmissionDate() { return submissionDate; }

    /**
     * Gets citizen.
     *
     * @return the citizen who submitted the complaint.
     */
    public Citizen getCitizen() { return citizen; }

    /**
     * Gets political agent.
     *
     * @return the political agent being complained about.
     */
    public PoliticalAgent getPoliticalAgent() { return politicalAgent; }

    /**
     * Gets political function.
     *
     * @return the political function the agent held at the time.
     */
    public PoliticalFunction getPoliticalFunction() { return politicalFunction; }

    @Override
    public String toString() {
        return String.format("Complaint[agent=%s, function=%s, date=%s]",
                politicalAgent.getName(), politicalFunction, complaintDate);
    }
}
