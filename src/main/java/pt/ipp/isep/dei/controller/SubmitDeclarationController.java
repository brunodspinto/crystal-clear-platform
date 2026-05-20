package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller responsible for handling the submission of a Declaration of Interests (US06).
 */
public class SubmitDeclarationController {

    private final OrganizationRepository organizationRepository;
    private final DeclarationRepository declarationRepository;
    private final PoliticalAgentRepository politicalAgentRepository;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public SubmitDeclarationController() {
        Repositories repos = Repositories.getInstance();
        this.organizationRepository = repos.getOrganizationRepository();
        this.declarationRepository = repos.getDeclarationRepository();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     *
     * @param organizationRepository   the organization repository
     * @param declarationRepository    the declaration repository
     * @param politicalAgentRepository the political agent repository
     * @param authenticationRepository the authentication repository
     */
    public SubmitDeclarationController(OrganizationRepository organizationRepository,
                                        DeclarationRepository declarationRepository,
                                        PoliticalAgentRepository politicalAgentRepository,
                                        AuthenticationRepository authenticationRepository) {
        this.organizationRepository = organizationRepository;
        this.declarationRepository = declarationRepository;
        this.politicalAgentRepository = politicalAgentRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Returns all available declaration types.
     *
     * @return list of {@link DeclarationType} values.
     */
    public List<DeclarationType> getDeclarationTypes() {
        return Arrays.asList(DeclarationType.values());
    }

    /**
     * Returns all registered organizations.
     *
     * @return list of {@link Organization}.
     */
    public List<Organization> getOrganizations() {
        List<Organization> all = organizationRepository.getOrganizations();
        List<Organization> filtered = new ArrayList<>();
        for (Organization org : all) {
            if (org.getType() != null) {
                filtered.add(org);
            }
        }
        return filtered;
    }

    /**
     * Returns all available position natures.
     *
     * @return list of {@link PositionNature} values.
     */
    public List<PositionNature> getPositionNatures() {
        return Arrays.asList(PositionNature.values());
    }

    /**
     * Returns all available asset types.
     *
     * @return list of {@link AssetType} values.
     */
    public List<AssetType> getAssetTypes() {
        return Arrays.asList(AssetType.values());
    }

    /**
     * Submits a Declaration of Interests on behalf of the currently logged-in Political Agent.
     * The declaration is created with PENDING status and the current date as submissionDate.
     *
     * @param type                   the declaration type.
     * @param positionEntries        list of position entry data arrays; each array contains:                             [Organization, String functionDesignation, PositionNature, Double grossSalary,                              Double sideIncomeConsulting, Double sideIncomeBoardMemberships,                              Date startDate, Date endDate].
     * @param subsidyEntries         list of subsidy entry data arrays; each array contains:                             [Organization, Double amount, String description, Date date].
     * @param assetEntries           list of asset entry data arrays; each array contains:                             [AssetType, Double assetValue, Object detail].
     * @param businessParticipations list of holding data arrays; each array contains:                             [Organization, Long companyNIF, Double totalValue, Double percentage].
     * @param attachments            list of attachment data arrays; each array contains:                             [String fileName, Date uploadDate].
     * @return {@code true} if the declaration was saved successfully;         {@code false} if the political agent was not found in the system.
     */
    public boolean submitDeclaration(DeclarationType type,
                                      List<Object[]> positionEntries,
                                      List<Object[]> subsidyEntries,
                                      List<Object[]> assetEntries,
                                      List<Object[]> businessParticipations,
                                      List<Object[]> attachments) {
        PoliticalAgent agent = getCurrentPoliticalAgent();
        if (agent == null) {
            return false;
        }

        Declaration declaration = new Declaration(type, agent, new Date());

        for (Object[] pe : positionEntries) {
            declaration.addPositionEntry(
                    (Organization) pe[0],
                    (String) pe[1],
                    (PositionNature) pe[2],
                    (double) pe[3],
                    (double) pe[4],
                    (double) pe[5],
                    (Date) pe[6],
                    (Date) pe[7]
            );
        }

        for (Object[] se : subsidyEntries) {
            declaration.addSubsidyEntry(
                    (Organization) se[0],
                    (double) se[1],
                    (String) se[2],
                    (Date) se[3]
            );
        }

        for (Object[] ae : assetEntries) {
            declaration.addAssetEntry(
                    (AssetType) ae[0],
                    (double) ae[1],
                    ae[2]
            );
        }

        for (Object[] bp : businessParticipations) {
            declaration.addBusinessParticipation(
                    (Organization) bp[0],
                    (long) bp[1],
                    (double) bp[2],
                    (double) bp[3]
            );
        }

        for (Object[] att : attachments) {
            declaration.addAttachment(
                    (String) att[0],
                    (Date) att[1]
            );
        }

        return declarationRepository.save(declaration);
    }

    /**
     * Retrieves the currently authenticated Political Agent from the session.
     *
     * @return the {@link PoliticalAgent}, or {@code null} if not found.
     */
    private PoliticalAgent getCurrentPoliticalAgent() {
        Email email = authenticationRepository.getCurrentUserSession().getUserId();
        return politicalAgentRepository.getByEmail(email.getEmail());
    }
}
