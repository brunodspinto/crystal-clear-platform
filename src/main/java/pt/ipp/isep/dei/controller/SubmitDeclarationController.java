package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.OrganizationDTO;
import pt.ipp.isep.dei.mapper.DeclarationMapper;
import pt.ipp.isep.dei.mapper.OrganizationMapper;
import pt.ipp.isep.dei.repository.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for US06 – Submit a Declaration of Interests.
 *
 * <p>AC1: supports household member entries (spouse, descendants, others).</p>
 * <p>AC2: provides {@link #getPreviousDeclarations()} and
 *         {@link #importFromDeclaration(String)} to pre-populate the form
 *         from a previous declaration.</p>
 * <p>AC3: enforces submission rules per declaration type:
 * <ul>
 *   <li>INITIAL – only allowed once per agent (no previous INITIAL may exist).</li>
 *   <li>REGULAR – only allowed once per calendar year per agent.</li>
 *   <li>EXCEPTIONAL – requires a non-blank amended declaration id and
 *       amendment reason.</li>
 * </ul>
 * </p>
 */
public class SubmitDeclarationController {

    private final OrganizationRepository    organizationRepository;
    private final DeclarationRepository     declarationRepository;
    private final PoliticalAgentRepository  politicalAgentRepository;
    private final AuthenticationRepository  authenticationRepository;

    public SubmitDeclarationController() {
        Repositories repos = Repositories.getInstance();
        this.organizationRepository   = repos.getOrganizationRepository();
        this.declarationRepository    = repos.getDeclarationRepository();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /** Constructor for unit tests — repositories injected directly. */
    public SubmitDeclarationController(OrganizationRepository organizationRepository,
                                       DeclarationRepository declarationRepository,
                                       PoliticalAgentRepository politicalAgentRepository,
                                       AuthenticationRepository authenticationRepository) {
        this.organizationRepository   = organizationRepository;
        this.declarationRepository    = declarationRepository;
        this.politicalAgentRepository = politicalAgentRepository;
        this.authenticationRepository = authenticationRepository;
    }

    // -------------------------------------------------------------------------
    // Catalogue methods (no session dependency)
    // -------------------------------------------------------------------------

    /** @return all available declaration types */
    public List<DeclarationType> getDeclarationTypes() {
        List<DeclarationType> list = new ArrayList<>();
        for (DeclarationType t : DeclarationType.values()) {
            list.add(t);
        }
        return list;
    }

    /** @return all registered organisations as DTOs (ESOFT — DTO pattern) */
    public List<OrganizationDTO> getOrganizations() {
        OrganizationMapper mapper = new OrganizationMapper();
        return mapper.toDTO(organizationRepository.getOrganizations());
    }

    /** @return all position natures */
    public List<PositionNature> getPositionNatures() {
        List<PositionNature> list = new ArrayList<>();
        for (PositionNature n : PositionNature.values()) {
            list.add(n);
        }
        return list;
    }

    /** @return all asset types */
    public List<AssetType> getAssetTypes() {
        List<AssetType> list = new ArrayList<>();
        for (AssetType t : AssetType.values()) {
            list.add(t);
        }
        return list;
    }

    /** @return all household relation types (AC1) */
    public List<HouseholdRelation> getHouseholdRelations() {
        List<HouseholdRelation> list = new ArrayList<>();
        for (HouseholdRelation r : HouseholdRelation.values()) {
            list.add(r);
        }
        return list;
    }

    // -------------------------------------------------------------------------
    // AC2 – Import from previous declaration
    // -------------------------------------------------------------------------

    /**
     * Returns all previously submitted declarations for the currently
     * authenticated agent, as DTOs. Used to populate the "import from
     * previous" dropdown (AC2).
     *
     * @return list of the agent's past declarations, possibly empty
     */
    public List<DeclarationDTO> getPreviousDeclarations() {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return new ArrayList<>();
        DeclarationMapper mapper = new DeclarationMapper();
        return mapper.toDTO(declarationRepository.getDeclarationsByAgent(agent));
    }

    /**
     * Returns the entry data of a previous declaration so the UI can
     * pre-populate the form (AC2). The data comes in the same Object[]
     * shapes that {@link #submitDeclaration} accepts, with the organization
     * passed by name — the domain objects never leave the controller.
     *
     * @param declarationId the id of the declaration to copy from
     * @return the imported entry lists, or null if the id is unknown or
     *         does not belong to the authenticated agent
     */
    public ImportedData importFromDeclaration(String declarationId) {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return null;

        Declaration source = declarationRepository.getById(declarationId);
        if (source == null || !source.getAgent().equals(agent)) return null;

        ImportedData data = new ImportedData();
        for (HouseholdMember m : source.getHouseholdMembers()) {
            data.householdMembers.add(new Object[]{m.getName(), m.getRelation()});
        }
        for (PositionEntry pe : source.getPositionEntries()) {
            data.positionEntries.add(new Object[]{pe.getOrganization().getName(),
                    pe.getFunctionDesignation(), pe.getNature(), pe.getGrossSalary(),
                    pe.getSideIncomeConsulting(), pe.getSideIncomeBoardMemberships(),
                    pe.getStartDate(), pe.getEndDate()});
        }
        for (SubsidyEntry se : source.getSubsidyEntries()) {
            data.subsidyEntries.add(new Object[]{se.getOrganization().getName(),
                    se.getAmount(), se.getDescription(), se.getDate()});
        }
        for (AssetEntry ae : source.getAssetEntries()) {
            data.assetEntries.add(new Object[]{ae.getAssetType(), ae.getAssetValue(),
                    ae.getDetail() != null ? ae.getDetail().toString() : ""});
        }
        for (BusinessParticipation bp : source.getBusinessParticipations()) {
            data.businessParticipations.add(new Object[]{bp.getOrganization().getName(),
                    bp.getCompanyNIF(), bp.getTotalValueInStocks(), bp.getCompanyPercentage()});
        }
        return data;
    }

    /**
     * Entry data imported from a previous declaration (AC2). Each list uses
     * the same Object[] shape that {@link #submitDeclaration} expects.
     */
    public static class ImportedData {
        private final List<Object[]> householdMembers = new ArrayList<>();
        private final List<Object[]> positionEntries = new ArrayList<>();
        private final List<Object[]> subsidyEntries = new ArrayList<>();
        private final List<Object[]> assetEntries = new ArrayList<>();
        private final List<Object[]> businessParticipations = new ArrayList<>();

        /** @return the household member entries */
        public List<Object[]> getHouseholdMembers() { return householdMembers; }

        /** @return the position entries */
        public List<Object[]> getPositionEntries() { return positionEntries; }

        /** @return the subsidy entries */
        public List<Object[]> getSubsidyEntries() { return subsidyEntries; }

        /** @return the asset entries */
        public List<Object[]> getAssetEntries() { return assetEntries; }

        /** @return the business participation entries */
        public List<Object[]> getBusinessParticipations() { return businessParticipations; }
    }

    // -------------------------------------------------------------------------
    // AC3 – Type validation
    // -------------------------------------------------------------------------

    /**
     * Validates the declaration type rules before submission (AC3).
     *
     * @param type                 the chosen declaration type
     * @param amendedDeclarationId required when type is EXCEPTIONAL; null otherwise
     * @param amendmentReason      required when type is EXCEPTIONAL; null otherwise
     * @return null if valid, or an error message string if the rule is violated
     */
    public String validateDeclarationType(DeclarationType type,
                                           String amendedDeclarationId,
                                           String amendmentReason) {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return "You are not registered as a Political Agent.";

        List<Declaration> existing = declarationRepository.getDeclarationsByAgent(agent);

        if (type == DeclarationType.INITIAL) {
            for (Declaration d : existing) {
                if (d.getType() == DeclarationType.INITIAL) {
                    return "An INITIAL declaration has already been submitted. " +
                           "Use REGULAR for annual updates or EXCEPTIONAL to correct a previous one.";
                }
            }
        }

        if (type == DeclarationType.REGULAR) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (Declaration d : existing) {
                if (d.getType() == DeclarationType.REGULAR) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d.getSubmissionDate());
                    if (cal.get(Calendar.YEAR) == currentYear) {
                        return "A REGULAR declaration has already been submitted this calendar year ("
                               + currentYear + ").";
                    }
                }
            }
        }

        if (type == DeclarationType.EXCEPTIONAL) {
            if (amendedDeclarationId == null || amendedDeclarationId.isBlank()) {
                return "EXCEPTIONAL declarations require the id of the declaration being amended.";
            }
            if (amendmentReason == null || amendmentReason.isBlank()) {
                return "EXCEPTIONAL declarations require an amendment reason.";
            }
            Declaration amended = declarationRepository.getById(amendedDeclarationId);
            if (amended == null || !amended.getAgent().equals(agent)) {
                return "The referenced declaration id was not found or does not belong to you.";
            }
        }

        return null; // valid
    }

    // -------------------------------------------------------------------------
    // Submit
    // -------------------------------------------------------------------------

    /**
     * Submits a declaration of interests (US06).
     *
     * @param type                  declaration type
     * @param amendedDeclarationId  id of the amended declaration (EXCEPTIONAL only)
     * @param amendmentReason       reason for the amendment (EXCEPTIONAL only)
     * @param householdMembers      list of {name, HouseholdRelation} pairs (AC1)
     * @param positionEntries       list of position entry data arrays
     * @param subsidyEntries        list of subsidy entry data arrays
     * @param assetEntries          list of asset entry data arrays
     * @param businessParticipations list of business participation data arrays
     * @param attachments           list of attachment data arrays
     * @return true if saved successfully; false if the agent is not found
     * @throws IllegalStateException if AC3 rules are violated
     */
    public boolean submitDeclaration(DeclarationType type,
                                     String amendedDeclarationId,
                                     String amendmentReason,
                                     List<Object[]> householdMembers,
                                     List<Object[]> positionEntries,
                                     List<Object[]> subsidyEntries,
                                     List<Object[]> assetEntries,
                                     List<Object[]> businessParticipations,
                                     List<Object[]> attachments) {

        // The agent must be authenticated before anything else (contract: return
        // false when there is no political agent in session).
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return false;

        // AC3 – validate type rules before persisting.
        String typeError = validateDeclarationType(type, amendedDeclarationId, amendmentReason);
        if (typeError != null) {
            throw new IllegalStateException(typeError);
        }

        Declaration declaration = new Declaration(type, agent, new Date(),
                amendedDeclarationId, amendmentReason);

        // AC1 – household members
        if (householdMembers != null) {
            for (Object[] hm : householdMembers) {
                declaration.addHouseholdMember((String) hm[0], (HouseholdRelation) hm[1]);
            }
        }

        if (positionEntries != null) {
            for (Object[] pe : positionEntries) {
                declaration.addPositionEntry(
                        findOrganization((String) pe[0]), (String) pe[1],
                        (PositionNature) pe[2], (double)        pe[3],
                        (double)         pe[4], (double)        pe[5],
                        (Date)           pe[6], (Date)          pe[7]);
            }
        }
        if (subsidyEntries != null) {
            for (Object[] se : subsidyEntries) {
                declaration.addSubsidyEntry(
                        findOrganization((String) se[0]), (double) se[1],
                        (String)       se[2], (Date)   se[3]);
            }
        }
        if (assetEntries != null) {
            for (Object[] ae : assetEntries) {
                declaration.addAssetEntry((AssetType) ae[0], (double) ae[1], ae[2]);
            }
        }
        if (businessParticipations != null) {
            for (Object[] bp : businessParticipations) {
                declaration.addBusinessParticipation(
                        findOrganization((String) bp[0]), (long) bp[1], (double) bp[2], (double) bp[3]);
            }
        }
        if (attachments != null) {
            for (Object[] att : attachments) {
                declaration.addAttachment((String) att[0], (Date) att[1]);
            }
        }

        return declarationRepository.save(declaration);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves an organization by its name. The UI works with DTOs, so the
     * entries arrive with the organization name and the domain object is
     * looked up here.
     *
     * @param name the organization name
     * @return the matching organization
     * @throws IllegalStateException if no organization has that name
     */
    private Organization findOrganization(String name) {
        for (Organization org : organizationRepository.getOrganizations()) {
            if (org.getName().equals(name)) {
                return org;
            }
        }
        throw new IllegalStateException("Unknown organization: " + name);
    }

    private PoliticalAgent getCurrentPoliticalAgent() {
        if (authenticationRepository == null) return null;
        try {
            String email = authenticationRepository
                    .getCurrentUserSession().getUserId().getEmail();
            return politicalAgentRepository.getByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
}
