package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
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

    /** @return all registered organisations */
    public List<Organization> getOrganizations() {
        return organizationRepository.getOrganizations();
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
     * authenticated agent. Used to populate the "import from previous"
     * dropdown (AC2).
     *
     * @return list of the agent's past declarations, possibly empty
     */
    public List<Declaration> getPreviousDeclarations() {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return new ArrayList<>();
        return declarationRepository.getDeclarationsByAgent(agent);
    }

    /**
     * Returns a pre-populated Declaration built from the data of a previous
     * declaration (AC2). The returned declaration has type INITIAL and status
     * PENDING — it is not yet saved. The caller must still set the correct
     * type and (for EXCEPTIONAL) the amendment fields before submitting.
     *
     * @param declarationId the id of the declaration to copy from
     * @return a new unsaved Declaration with the same entries, or null if not found
     */
    public Declaration importFromDeclaration(String declarationId) {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return null;

        Declaration source = declarationRepository.getById(declarationId);
        if (source == null || !source.getAgent().equals(agent)) return null;

        // Create a blank declaration (type placeholder — UI will let user change it)
        Declaration copy = new Declaration(DeclarationType.INITIAL, agent, new Date(),
                null, null);

        for (HouseholdMember m : source.getHouseholdMembers()) {
            copy.addHouseholdMember(m.getName(), m.getRelation());
        }
        for (PositionEntry pe : source.getPositionEntries()) {
            copy.addPositionEntry(pe.getOrganization(), pe.getFunctionDesignation(),
                    pe.getNature(), pe.getGrossSalary(), pe.getSideIncomeConsulting(),
                    pe.getSideIncomeBoardMemberships(), pe.getStartDate(), pe.getEndDate());
        }
        for (SubsidyEntry se : source.getSubsidyEntries()) {
            copy.addSubsidyEntry(se.getOrganization(), se.getAmount(),
                    se.getDescription(), se.getDate());
        }
        for (AssetEntry ae : source.getAssetEntries()) {
            copy.addAssetEntry(ae.getAssetType(), ae.getAssetValue(), ae.getDetail());
        }
        for (BusinessParticipation bp : source.getBusinessParticipations()) {
            copy.addBusinessParticipation(bp.getOrganization(), bp.getCompanyNIF(),
                    bp.getTotalValueInStocks(), bp.getCompanyPercentage());
        }
        return copy;
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

        // AC3 – validate type rules before doing anything
        String typeError = validateDeclarationType(type, amendedDeclarationId, amendmentReason);
        if (typeError != null) {
            throw new IllegalStateException(typeError);
        }

        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) return false;

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
                        (Organization)   pe[0], (String)        pe[1],
                        (PositionNature) pe[2], (double)        pe[3],
                        (double)         pe[4], (double)        pe[5],
                        (Date)           pe[6], (Date)          pe[7]);
            }
        }
        if (subsidyEntries != null) {
            for (Object[] se : subsidyEntries) {
                declaration.addSubsidyEntry(
                        (Organization) se[0], (double) se[1],
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
                        (Organization) bp[0], (long) bp[1], (double) bp[2], (double) bp[3]);
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
