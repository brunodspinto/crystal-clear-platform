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

    private final PoliticalAgent agent;
    private final DeclarationType type;
    private final Date submissionDate;
    private DeclarationStatus status;

    private final List<PositionEntry> positionEntries;
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
        this.type = type;
        this.agent = agent;
        this.submissionDate = submissionDate;
        this.status = DeclarationStatus.PENDING;
        this.positionEntries = new ArrayList<>();
        this.subsidyEntries = new ArrayList<>();
        this.assetEntries = new ArrayList<>();
        this.businessParticipations = new ArrayList<>();
        this.attachments = new ArrayList<>();
    }

    /**
     * Adds a position entry to the declaration.
     *
     * @param organization the organization where the position is held.
     * @param function     the function performed.
     * @param nature       the nature of the position.
     * @param grossSalary  the annual gross salary.
     * @param sideIncome   additional earnings.
     * @param startDate    the start date.
     * @param endDate      the end date, or {@code null} if still active.
     */
    public void addPositionEntry(Organization organization, Function function, PositionNature nature,
                                  double grossSalary, double sideIncome, Date startDate, Date endDate) {
        positionEntries.add(new PositionEntry(organization, function, nature,
                grossSalary, sideIncome, startDate, endDate));
    }

    /**
     * Adds a subsidy entry to the declaration.
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

    /** @return the political agent who submitted the declaration. */
    public PoliticalAgent getAgent() { return agent; }

    /** @return the type of this declaration. */
    public DeclarationType getType() { return type; }

    /** @return the submission date. */
    public Date getSubmissionDate() { return submissionDate; }

    /** @return the current status of the declaration. */
    public DeclarationStatus getStatus() { return status; }

    /** @return an unmodifiable copy of the position entries. */
    public List<PositionEntry> getPositionEntries() { return new ArrayList<>(positionEntries); }

    /** @return an unmodifiable copy of the subsidy entries. */
    public List<SubsidyEntry> getSubsidyEntries() { return new ArrayList<>(subsidyEntries); }

    /** @return an unmodifiable copy of the asset entries. */
    public List<AssetEntry> getAssetEntries() { return new ArrayList<>(assetEntries); }

    /** @return an unmodifiable copy of the business participations. */
    public List<BusinessParticipation> getBusinessParticipations() { return new ArrayList<>(businessParticipations); }

    /** @return an unmodifiable copy of the attachments. */
    public List<Attachment> getAttachments() { return new ArrayList<>(attachments); }

    @Override
    public String toString() {
        return String.format("Declaration{agent='%s', type=%s, date=%s, status=%s}",
                agent.getName(), type, submissionDate, status);
    }
}
