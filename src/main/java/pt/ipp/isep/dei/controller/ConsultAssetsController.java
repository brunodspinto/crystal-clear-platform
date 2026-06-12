package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.dto.AssetEntryDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.mapper.AssetEntryMapper;
import pt.ipp.isep.dei.mapper.PoliticalAgentMapper;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.UserSession;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for US11 - Consult the assets of a political agent on a specific
 * date. Only validated declarations submitted on or before the reference date
 * are considered. Sensitive values are masked for citizens (AC4); journalists
 * see full details.
 */
public class ConsultAssetsController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;
    private final AuthenticationRepository authenticationRepository;
    private final PoliticalAgentMapper politicalAgentMapper = new PoliticalAgentMapper();
    private final AssetEntryMapper assetEntryMapper = new AssetEntryMapper();

    /**
     * Creates a controller using the singleton repositories.
     */
    public ConsultAssetsController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.declarationRepository = repos.getDeclarationRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /**
     * Creates a controller with injected repositories. Used in tests.
     *
     * @param politicalAgentRepository the political agent repository.
     * @param declarationRepository    the declaration repository.
     * @param authenticationRepository the authentication repository.
     */
    public ConsultAssetsController(PoliticalAgentRepository politicalAgentRepository,
                                   DeclarationRepository declarationRepository,
                                   AuthenticationRepository authenticationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Returns all registered political agents available for selection by the
     * actor (AC1).
     *
     * @return list of {@link PoliticalAgentDTO}.
     */
    public List<PoliticalAgentDTO> getPoliticalAgents() {
        return politicalAgentMapper.toDTO(politicalAgentRepository.getAll());
    }

    /**
     * Returns only the political agents that have at least one validated
     * declaration submitted on or before the reference date. The UI uses
     * this to hide agents with nothing to consult at the chosen date, so
     * the dropdown follows the same date rule as the search itself.
     *
     * @param referenceDate the date for which the assets will be requested.
     * @return list of matching {@link PoliticalAgentDTO}; never null.
     * @throws IllegalArgumentException if the date is null.
     */
    public List<PoliticalAgentDTO> getPoliticalAgentsWithDataAt(Date referenceDate) {
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date cannot be null.");
        }
        List<PoliticalAgent> matching = new ArrayList<>();
        for (PoliticalAgent agent : politicalAgentRepository.getAll()) {
            List<Declaration> declarations =
                    declarationRepository.getValidatedDeclarationsForAgentUpTo(agent, referenceDate);
            if (!declarations.isEmpty()) {
                matching.add(agent);
            }
        }
        return politicalAgentMapper.toDTO(matching);
    }

    /**
     * Returns the asset entries declared by the given agent up to the
     * reference date, gathered across every validated declaration (AC2).
     * Empty when no declarations match (AC3).
     *
     * @param agentDto      the selected political agent (identified by email).
     * @param referenceDate the date for which the assets are requested.
     * @return list of matching {@link AssetEntryDTO}; never null.
     * @throws IllegalArgumentException if any argument is null or the agent is unknown.
     */
    public List<AssetEntryDTO> getAssetsAt(PoliticalAgentDTO agentDto, Date referenceDate) {
        if (agentDto == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date cannot be null.");
        }
        PoliticalAgent agent = politicalAgentRepository.getByEmail(agentDto.getEmail());
        if (agent == null) {
            throw new IllegalArgumentException("Unknown political agent.");
        }
        List<Declaration> declarations =
                declarationRepository.getValidatedDeclarationsForAgentUpTo(agent, referenceDate);
        List<AssetEntry> assets = new ArrayList<>();
        for (Declaration d : declarations) {
            for (AssetEntry a : d.getAssetEntries()) {
                assets.add(a);
            }
        }
        return assetEntryMapper.toDTO(assets);
    }

    /**
     * Returns whether the user currently logged in has the Journalist role.
     * The UI uses this flag to honour AC4: journalists see full asset values,
     * citizens see sensitive values masked.
     *
     * @return true if there is a logged-in user whose roles include         Journalist; false otherwise.
     */
    public boolean isCurrentUserJournalist() {
        if (authenticationRepository == null) {
            return false;
        }
        UserSession session = authenticationRepository.getCurrentUserSession();
        if (session == null || !session.isLoggedIn()) {
            return false;
        }
        List<UserRoleDTO> roles = session.getUserRoles();
        if (roles == null) {
            return false;
        }
        for (UserRoleDTO role : roles) {
            if (AuthenticationController.ROLE_JOURNALIST.equals(role.getDescription())) {
                return true;
            }
        }
        return false;
    }
}
