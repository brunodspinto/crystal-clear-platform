package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a Declaration of Interests submitted by a Political Agent.
 * This is the central aggregate for US06. It owns all entry sections
 * (positions, subsidies, assets, business participations, attachments).
 * Upon creation, the status is always set to {@link DeclarationStatus#PENDING}.
 */
public class Declaration {

    private static int nextId = 1;

    private final String id;
    private final PoliticalAgent agent;
    private final DeclarationType type;
    private final Date submissionDate;
    private DeclarationStatus status;

    private final List<PositionEntry> positionEntries;
    private final List<Income> incomes;
    private final List<SubsidyEntry> subsidyEntries;
    private final List<AssetEntry> assetEntries;
    private final List<BusinessParticipation> businessParticipations;
    private final List<Attachment> attachments;

    /**
     * Creates a new Declaration. The status is automatically set to PENDING.
     * Called by the controller after collecting all data from the agent.
     *
     * @param type           the type of declaration (initial, regular, or exceptional).
     * @param agent          the political agent submitting the declaration.
     * @param submissionDate the date of submission (set to now by the controller).
     * @throws IllegalArgumentException if any required argument is null.
     */
    public Declaration(DeclarationType type, PoliticalAgent agent, Date submissionDate) {
        if (type == null) {
            throw new IllegalArgumentException("Declaration type cannot be null.");
        }
        if (agent == null) {
            throw new IllegalArgumentException("Political agent cannot be null.");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Submission date cannot be null.");
        }
        this.id = "DECL-" + nextId;
        nextId = nextId + 1;
        this.type = type;
        this.agent = agent;
        this.submissionDate = submissionDate;
        this.status = DeclarationStatus.PENDING;
        this.positionEntries = new ArrayList<>();
        this.incomes = new ArrayList<>();
        this.subsidyEntries = new ArrayList<>();
        this.assetEntries = new ArrayList<>();
        this.businessParticipations = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    /**
     * Adds a position entry to the declaration.
     *
     * @param organization               the organization where the position is held.
     * @param functionDesignation        the function performed.
     * @param nature                     the nature of the position.
     * @param grossSalary                the annual gross salary.
     * @param sideIncomeConsulting       consulting side income.
     * @param sideIncomeBoardMemberships board membership side income.
     * @param startDate                  the start date.
     * @param endDate                    the end date, or {@code null} if still active.
     */
    public void addPositionEntry(Organization organization, String functionDesignation, PositionNature nature,
                                  double grossSalary, double sideIncomeConsulting,
                                  double sideIncomeBoardMemberships, Date startDate, Date endDate) {
        positionEntries.add(new PositionEntry(organization, functionDesignation, nature,
                grossSalary, sideIncomeConsulting, sideIncomeBoardMemberships, startDate, endDate));
    }

    /**
     * Adds an income entry to the declaration.
     * <p>
     * An {@link Income} represents an earning received from an organization (e.g. salary
     * complements, fees, royalties), characterized by its {@code source}. It is a distinct
     * concept from a {@link SubsidyEntry}, which represents a non-reciprocal support or
     * subsidy granted to the agent and is characterized by its {@code description}.
     *
     * @param organization the organization that paid the income.
     * @param amount       the income amount.
     * @param source       the source of the income (e.g. consulting, royalties).
     * @param date         the date received.
     */
    public void addIncome(Organization organization, double amount, String source, Date date) {
        incomes.add(new Income(organization, amount, source, date));
    }

    /**
     * Adds a subsidy entry to the declaration.
     * <p>
     * A {@link SubsidyEntry} represents a non-reciprocal support or subsidy received from
     * an organization. It is a distinct concept from {@link Income} (see {@link #addIncome}).
     *
     * @param organization the organization from which the subsidy was received.
     * @param amount       the subsidy amount.
     * @param description  a description of the subsidy.
     * @param date         the date received.
     */
    public void addSubsidyEntry(Organization organization, double amount, String description, Date date) {
        subsidyEntries.add(new SubsidyEntry(organization, amount, description, date));
    }

    /**
     * Adds an asset entry to the declaration.
     * The {@code detail} must match the {@code assetType}
     * ({@link RealEstate}, {@link VehicleAsset}, or {@link StockAsset}).
     *
     * @param assetType  the category of the asset.
     * @param assetValue the declared value.
     * @param detail     the detail object matching the asset type.
     */
    public void addAssetEntry(AssetType assetType, double assetValue, Object detail) {
        assetEntries.add(new AssetEntry(assetType, assetValue, detail));
    }

    /**
     * Adds a business participation entry to the declaration.
     *
     * @param organization       the company.
     * @param companyNIF         the NIF of the company.
     * @param totalValueInStocks the total declared stock value.
     * @param companyPercentage  the ownership percentage.
     */
    public void addBusinessParticipation(Organization organization, long companyNIF,
                                          double totalValueInStocks, double companyPercentage) {
        businessParticipations.add(new BusinessParticipation(organization, companyNIF,
                totalValueInStocks, companyPercentage));
    }

    /**
     * Adds an attachment to the declaration.
     *
     * @param fileName   the name of the file.
     * @param uploadDate the date of upload.
     */
    public void addAttachment(String fileName, Date uploadDate) {
        attachments.add(new Attachment(fileName, uploadDate));
    }

    /**
     * Updates the status of this declaration.
     * Used by the Ethics Committee validation process (US08).
     *
     * @param status the new status; cannot be null.
     * @throws IllegalArgumentException if status is null.
     */
    public void setStatus(DeclarationStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    /**
     * Updates the status of this declaration based on a validation outcome (US08).
     * VALIDATED → DeclarationStatus.VALIDATED
     * RETURNED_FOR_CORRECTION → DeclarationStatus.REJECTED
     *
     * @param outcome the validation outcome; cannot be null.
     * @throws IllegalArgumentException if outcome is null.
     */
    public void setStatus(ValidationOutcome outcome) {
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null.");
        }
        switch (outcome) {
            case VALIDATED:
                this.status = DeclarationStatus.VALIDATED;
                break;
            case RETURNED_FOR_CORRECTION:
                this.status = DeclarationStatus.REJECTED;
                break;
            default:
                throw new IllegalArgumentException("Unknown outcome: " + outcome);
        }
    }

    /**
     * Returns a summary of the declaration's content for display purposes (used by US08).
     *
     * @return a formatted string with the declaration details.
     */
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Declaration [%s | %s | %s]%n", type, submissionDate, status));
        sb.append(String.format("  Agent                 : %s%n", agent.getName()));
        sb.append(String.format("  Position entries      : %d%n", positionEntries.size()));
        sb.append(String.format("  Income entries        : %d%n", incomes.size()));
        sb.append(String.format("  Subsidy entries       : %d%n", subsidyEntries.size()));
        sb.append(String.format("  Asset entries         : %d%n", assetEntries.size()));
        sb.append(String.format("  Business participations: %d%n", businessParticipations.size()));
        sb.append(String.format("  Attachments           : %d%n", attachments.size()));
        return sb.toString();
    }

    /**
     * Gets id.
     *
     * @return the unique identifier of this declaration.
     */
    public String getId() { return id; }

    /**
     * Gets agent.
     *
     * @return the political agent who submitted the declaration.
     */
    public PoliticalAgent getAgent() { return agent; }

    /**
     * Gets type.
     *
     * @return the type of this declaration.
     */
    public DeclarationType getType() { return type; }

    /**
     * Gets submission date.
     *
     * @return the submission date.
     */
    public Date getSubmissionDate() { return submissionDate; }

    /**
     * Gets status.
     *
     * @return the current status of the declaration.
     */
    public DeclarationStatus getStatus() { return status; }

    /**
     * Gets position entries.
     *
     * @return an unmodifiable copy of the position entries.
     */
    public List<PositionEntry> getPositionEntries() { return new ArrayList<>(positionEntries); }

    /**
     * Gets incomes.
     *
     * @return an unmodifiable copy of the income entries.
     */
    public List<Income> getIncomes() { return new ArrayList<>(incomes); }

    /**
     * Gets subsidy entries.
     *
     * @return an unmodifiable copy of the subsidy entries.
     */
    public List<SubsidyEntry> getSubsidyEntries() { return new ArrayList<>(subsidyEntries); }

    /**
     * Gets asset entries.
     *
     * @return an unmodifiable copy of the asset entries.
     */
    public List<AssetEntry> getAssetEntries() { return new ArrayList<>(assetEntries); }

    /**
     * Gets business participations.
     *
     * @return an unmodifiable copy of the business participations.
     */
    public List<BusinessParticipation> getBusinessParticipations() { return new ArrayList<>(businessParticipations); }

    /**
     * Gets attachments.
     *
     * @return an unmodifiable copy of the attachments.
     */
    public List<Attachment> getAttachments() { return new ArrayList<>(attachments); }

    @Override
    public String toString() {
        return String.format("Declaration{agent='%s', type=%s, date=%s, status=%s}",
                agent.getName(), type, submissionDate, status);
    }
}
