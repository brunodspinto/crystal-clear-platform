package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a Declaration of Interests submitted by a Political Agent.
 * This is the central aggregate for US06. It owns all entry sections
 * (positions, subsidies, assets, business participations, attachments).
 * Upon creation, the status is always set to {@link DeclarationStatus#PENDING}.
 *
 * <p>AC1: may include a household section (partner, descendants, others).</p>
 * <p>AC3: EXCEPTIONAL declarations carry the id of the amended declaration
 * and the reason for the amendment.</p>
 */
public class Declaration implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int nextId = 1;

    private final String id;
    private final PoliticalAgent agent;
    private final DeclarationType type;
    private final Date submissionDate;
    private DeclarationStatus status;

    // AC3 – only set for EXCEPTIONAL declarations
    private final String amendedDeclarationId;
    private final String amendmentReason;

    private final List<HouseholdMember>      householdMembers;     // AC1
    private final List<PositionEntry>         positionEntries;
    private final List<Income>                incomes;
    private final List<SubsidyEntry>          subsidyEntries;
    private final List<AssetEntry>            assetEntries;
    private final List<BusinessParticipation> businessParticipations;
    private final List<Attachment>            attachments;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates a new INITIAL or REGULAR declaration.
     * The status is automatically set to PENDING.
     *
     * @param type           INITIAL or REGULAR (not EXCEPTIONAL — use the other constructor)
     * @param agent          the political agent submitting the declaration
     * @param submissionDate the date of submission
     * @throws IllegalArgumentException if any argument is null, or if type is EXCEPTIONAL
     */
    public Declaration(DeclarationType type, PoliticalAgent agent, Date submissionDate) {
        this(type, agent, submissionDate, null, null);
        if (type == DeclarationType.EXCEPTIONAL) {
            throw new IllegalArgumentException(
                "EXCEPTIONAL declarations require an amended declaration id and amendment reason. " +
                "Use Declaration(type, agent, date, amendedDeclarationId, amendmentReason).");
        }
    }

    /**
     * Creates a new declaration of any type, including EXCEPTIONAL.
     * For INITIAL and REGULAR declarations, pass {@code null} for both
     * {@code amendedDeclarationId} and {@code amendmentReason}.
     *
     * @param type                  the declaration type
     * @param agent                 the political agent submitting the declaration
     * @param submissionDate        the date of submission
     * @param amendedDeclarationId  id of the declaration being amended (required for EXCEPTIONAL)
     * @param amendmentReason       reason for the amendment (required for EXCEPTIONAL)
     * @throws IllegalArgumentException if required fields are missing or blank
     */
    public Declaration(DeclarationType type, PoliticalAgent agent, Date submissionDate,
                       String amendedDeclarationId, String amendmentReason) {
        if (type == null)           throw new IllegalArgumentException("Declaration type cannot be null.");
        if (agent == null)          throw new IllegalArgumentException("Political agent cannot be null.");
        if (submissionDate == null) throw new IllegalArgumentException("Submission date cannot be null.");

        if (type == DeclarationType.EXCEPTIONAL) {
            if (amendedDeclarationId == null || amendedDeclarationId.isBlank()) {
                throw new IllegalArgumentException(
                    "EXCEPTIONAL declaration requires the id of the declaration being amended.");
            }
            if (amendmentReason == null || amendmentReason.isBlank()) {
                throw new IllegalArgumentException(
                    "EXCEPTIONAL declaration requires an amendment reason.");
            }
        }

        this.id                   = "DECL-" + nextId++;
        this.type                 = type;
        this.agent                = agent;
        this.submissionDate       = submissionDate;
        this.status               = DeclarationStatus.PENDING;
        this.amendedDeclarationId = amendedDeclarationId;
        this.amendmentReason      = amendmentReason;

        this.householdMembers      = new ArrayList<>();
        this.positionEntries       = new ArrayList<>();
        this.incomes               = new ArrayList<>();
        this.subsidyEntries        = new ArrayList<>();
        this.assetEntries          = new ArrayList<>();
        this.businessParticipations = new ArrayList<>();
        this.attachments           = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // AC1 – Household
    // -------------------------------------------------------------------------

    /**
     * Adds a household member to the declaration (AC1).
     *
     * @param name     the full name of the household member
     * @param relation the relationship to the agent (SPOUSE, DESCENDANT, OTHER)
     */
    public void addHouseholdMember(String name, HouseholdRelation relation) {
        householdMembers.add(new HouseholdMember(name, relation));
    }

    /** @return a defensive copy of the household members list */
    public List<HouseholdMember> getHouseholdMembers() {
        return new ArrayList<>(householdMembers);
    }

    // -------------------------------------------------------------------------
    // Existing section adders (unchanged)
    // -------------------------------------------------------------------------

    /** Adds a position entry to the declaration. */
    public void addPositionEntry(Organization organization, String functionDesignation,
                                  PositionNature nature, double grossSalary,
                                  double sideIncomeConsulting, double sideIncomeBoardMemberships,
                                  Date startDate, Date endDate) {
        positionEntries.add(new PositionEntry(organization, functionDesignation, nature,
                grossSalary, sideIncomeConsulting, sideIncomeBoardMemberships, startDate, endDate));
    }

    /** Adds an income entry to the declaration. */
    public void addIncome(Organization organization, double amount, String source, Date date) {
        incomes.add(new Income(organization, amount, source, date));
    }

    /** Adds a subsidy entry to the declaration. */
    public void addSubsidyEntry(Organization organization, double amount,
                                 String description, Date date) {
        subsidyEntries.add(new SubsidyEntry(organization, amount, description, date));
    }

    /** Adds an asset entry to the declaration. */
    public void addAssetEntry(AssetType assetType, double assetValue, Object detail) {
        assetEntries.add(new AssetEntry(assetType, assetValue, detail));
    }

    /** Adds a business participation entry to the declaration. */
    public void addBusinessParticipation(Organization organization, long companyNIF,
                                          double totalValueInStocks, double companyPercentage) {
        businessParticipations.add(new BusinessParticipation(organization, companyNIF,
                totalValueInStocks, companyPercentage));
    }

    /** Adds an attachment to the declaration. */
    public void addAttachment(String fileName, Date uploadDate) {
        attachments.add(new Attachment(fileName, uploadDate));
    }

    // -------------------------------------------------------------------------
    // Status management (unchanged)
    // -------------------------------------------------------------------------

    /** Updates the status of this declaration. */
    public void setStatus(DeclarationStatus status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null.");
        this.status = status;
    }

    /** Updates the status based on a validation outcome (US08). */
    public void setStatus(ValidationOutcome outcome) {
        if (outcome == null) throw new IllegalArgumentException("Outcome cannot be null.");
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

    // -------------------------------------------------------------------------
    // Display
    // -------------------------------------------------------------------------

    /** Returns a summary for display purposes (US08). */
    public String getDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Declaration [%s | %s | %s]%n", type, submissionDate, status));
        sb.append(String.format("  Agent                  : %s%n", agent.getName()));
        if (type == DeclarationType.EXCEPTIONAL) {
            sb.append(String.format("  Amends declaration     : %s%n", amendedDeclarationId));
            sb.append(String.format("  Amendment reason       : %s%n", amendmentReason));
        }
        sb.append(String.format("  Household members      : %d%n", householdMembers.size()));
        sb.append(String.format("  Position entries       : %d%n", positionEntries.size()));
        sb.append(String.format("  Income entries         : %d%n", incomes.size()));
        sb.append(String.format("  Subsidy entries        : %d%n", subsidyEntries.size()));
        sb.append(String.format("  Asset entries          : %d%n", assetEntries.size()));
        sb.append(String.format("  Business participations: %d%n", businessParticipations.size()));
        sb.append(String.format("  Attachments            : %d%n", attachments.size()));
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getId()                  { return id; }
    public PoliticalAgent getAgent()       { return agent; }
    public DeclarationType getType()       { return type; }
    public Date getSubmissionDate()        { return submissionDate; }
    public DeclarationStatus getStatus()   { return status; }

    /** @return the id of the declaration being amended, or null for non-EXCEPTIONAL */
    public String getAmendedDeclarationId() { return amendedDeclarationId; }

    /** @return the reason for the amendment, or null for non-EXCEPTIONAL */
    public String getAmendmentReason()      { return amendmentReason; }

    public List<PositionEntry>         getPositionEntries()        { return new ArrayList<>(positionEntries); }
    public List<Income>                getIncomes()                { return new ArrayList<>(incomes); }
    public List<SubsidyEntry>          getSubsidyEntries()         { return new ArrayList<>(subsidyEntries); }
    public List<AssetEntry>            getAssetEntries()           { return new ArrayList<>(assetEntries); }
    public List<BusinessParticipation> getBusinessParticipations() { return new ArrayList<>(businessParticipations); }
    public List<Attachment>            getAttachments()            { return new ArrayList<>(attachments); }

    @Override
    public String toString() {
        return String.format("Declaration{agent='%s', type=%s, date=%s, status=%s}",
                agent.getName(), type, submissionDate, status);
    }
}
