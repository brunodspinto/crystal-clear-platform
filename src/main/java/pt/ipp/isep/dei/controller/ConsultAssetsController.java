package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.UserSession;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// US11 - controller to consult the assets of a political agent on a specific date.
// Only validated declarations submitted on or before the reference date are considered.
public class ConsultAssetsController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;
    private final AuthenticationRepository authenticationRepository;

    public ConsultAssetsController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.declarationRepository = repos.getDeclarationRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    // used in tests
    public ConsultAssetsController(PoliticalAgentRepository politicalAgentRepository,
                                   DeclarationRepository declarationRepository,
                                   AuthenticationRepository authenticationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
        this.authenticationRepository = authenticationRepository;
    }

    public List<PoliticalAgent> getPoliticalAgents() {
        return politicalAgentRepository.getAll();
    }

    public List<AssetEntry> getAssetsAt(PoliticalAgent agent, Date referenceDate) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date cannot be null.");
        }
        List<Declaration> declarations =
                declarationRepository.getValidatedDeclarationsForAgentUpTo(agent, referenceDate);
        List<AssetEntry> assets = new ArrayList<>();
        for (Declaration d : declarations) {
            for (AssetEntry a : d.getAssetEntries()) {
                assets.add(a);
            }
        }
        return assets;
    }

    // AC4 - journalists see full values, citizens get sensitive values masked.
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
