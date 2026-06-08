package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.PoliticalFunction;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data Transfer Object (DTO) carrying the data of a single grievance to show in
 * the UI (the list of grievances already added to the complaint being built),
 * decoupling the UI from the domain {@code ComplaintItem} (ESOFT &mdash; DTO
 * pattern). It holds no business logic.
 */
public class ComplaintItemDTO {

    private final String description;
    private final Date complaintDate;
    private final PoliticalFunction politicalFunction;

    /**
     * Creates a grievance DTO.
     *
     * @param description       the grievance description.
     * @param complaintDate     the date of the reported behaviour.
     * @param politicalFunction the function the agent held at the time.
     */
    public ComplaintItemDTO(String description, Date complaintDate, PoliticalFunction politicalFunction) {
        this.description = description;
        this.complaintDate = complaintDate;
        this.politicalFunction = politicalFunction;
    }

    /**
     * @return the grievance description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the date when the reported behaviour occurred.
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
