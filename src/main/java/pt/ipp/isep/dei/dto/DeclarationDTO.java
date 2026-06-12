package pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object (DTO) carrying the data of a validated declaration that
 * the consultation UIs need to display (US09 and US10), decoupling the UI from
 * the domain {@code Declaration} (ESOFT &mdash; DTO pattern). The entry sections
 * are carried as display-ready strings and it holds no business logic.
 */
public class DeclarationDTO {

    private final String id;
    private final String type;
    private final Date submissionDate;
    private final String status;
    private final String summary;
    private final String details;
    private final List<String> positions;
    private final List<String> incomes;
    private final List<String> subsidies;
    private final List<String> assets;
    private final List<String> businessParticipations;
    private final double totalIncome;
    private final String agentName;
    private final int attachmentsCount;

    /**
     * Creates a declaration DTO.
     *
     * @param id                     the declaration id.
     * @param type                   the declaration type as text.
     * @param submissionDate         the submission date.
     * @param status                 the declaration status as text.
     * @param summary                the one-line summary of the declaration.
     * @param details                the header details of the declaration.
     * @param positions              the position entries, one line each.
     * @param incomes                the incomes, one line each.
     * @param subsidies              the subsidy entries, one line each.
     * @param assets                 the asset entries, one line each.
     * @param businessParticipations the business participations, one line each.
     * @param totalIncome            the sum of all income amounts.
     * @param agentName              the name of the agent that submitted it.
     * @param attachmentsCount       the number of attachments.
     */
    public DeclarationDTO(String id, String type, Date submissionDate, String status,
                          String summary, String details,
                          List<String> positions, List<String> incomes,
                          List<String> subsidies, List<String> assets,
                          List<String> businessParticipations, double totalIncome,
                          String agentName, int attachmentsCount) {
        this.id = id;
        this.type = type;
        this.submissionDate = submissionDate;
        this.status = status;
        this.summary = summary;
        this.details = details;
        this.positions = positions;
        this.incomes = incomes;
        this.subsidies = subsidies;
        this.assets = assets;
        this.businessParticipations = businessParticipations;
        this.totalIncome = totalIncome;
        this.agentName = agentName;
        this.attachmentsCount = attachmentsCount;
    }

    /**
     * @return the declaration id.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the declaration type as text.
     */
    public String getType() {
        return type;
    }

    /**
     * @return the submission date.
     */
    public Date getSubmissionDate() {
        return submissionDate;
    }

    /**
     * @return the declaration status as text.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the one-line summary of the declaration.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the header details of the declaration.
     */
    public String getDetails() {
        return details;
    }

    /**
     * @return the position entries, one display line each.
     */
    public List<String> getPositions() {
        return new ArrayList<>(positions);
    }

    /**
     * @return the incomes, one display line each.
     */
    public List<String> getIncomes() {
        return new ArrayList<>(incomes);
    }

    /**
     * @return the subsidy entries, one display line each.
     */
    public List<String> getSubsidies() {
        return new ArrayList<>(subsidies);
    }

    /**
     * @return the asset entries, one display line each.
     */
    public List<String> getAssets() {
        return new ArrayList<>(assets);
    }

    /**
     * @return the business participations, one display line each.
     */
    public List<String> getBusinessParticipations() {
        return new ArrayList<>(businessParticipations);
    }

    /**
     * @return the sum of all income amounts of the declaration.
     */
    public double getTotalIncome() {
        return totalIncome;
    }

    /**
     * @return the name of the political agent that submitted the declaration.
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * @return the number of attachments of the declaration.
     */
    public int getAttachmentsCount() {
        return attachmentsCount;
    }

    @Override
    public String toString() {
        return summary;
    }
}
