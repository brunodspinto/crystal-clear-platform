package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a single grievance (a reported behaviour) within a {@link Complaint}.
 * Each grievance has its own description, the date when the behaviour occurred and
 * the political function the agent held at that time. All grievances of the same
 * complaint refer to the same political agent. Immutable after creation.
 */
public class ComplaintItem implements Serializable {

    private final String description;
    private final Date complaintDate;
    private final PoliticalFunction politicalFunction;

    /**
     * Creates a new grievance.
     *
     * @param description       a description of the reported behaviour.
     * @param complaintDate     the date when the behaviour occurred (cannot be in the future).
     * @param politicalFunction the function the agent held at the time of the behaviour.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public ComplaintItem(String description, Date complaintDate, PoliticalFunction politicalFunction) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (complaintDate == null) {
            throw new IllegalArgumentException("Complaint date cannot be null");
        }
        if (complaintDate.after(new Date())) {
            throw new IllegalArgumentException("Complaint date cannot be in the future");
        }
        if (politicalFunction == null) {
            throw new IllegalArgumentException("Political function cannot be null");
        }
        this.description = description;
        this.complaintDate = complaintDate;
        this.politicalFunction = politicalFunction;
    }

    /**
     * @return the description of the reported behaviour.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the date when the behaviour occurred.
     */
    public Date getComplaintDate() {
        return complaintDate;
    }

    /**
     * @return the political function the agent held at the time.
     */
    public PoliticalFunction getPoliticalFunction() {
        return politicalFunction;
    }

    @Override
    public String toString() {
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(complaintDate);
        return politicalFunction + " | " + formattedDate + " | " + description;
    }
}
