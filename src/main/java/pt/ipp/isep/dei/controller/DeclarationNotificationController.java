package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;

/**
 * Controller that tells whether a Political Agent has declarations that were
 * returned for correction by the Ethics Committee (status REJECTED). Used by
 * the login screen to warn the agent right after they log in (US08 follow-up).
 */
public class DeclarationNotificationController {

    private final DeclarationRepository declarationRepository;
    private final PoliticalAgentRepository politicalAgentRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public DeclarationNotificationController() {
        Repositories repositories = Repositories.getInstance();
        this.declarationRepository = repositories.getDeclarationRepository();
        this.politicalAgentRepository = repositories.getPoliticalAgentRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     *
     * @param declarationRepository    the declaration repository
     * @param politicalAgentRepository the political agent repository
     */
    public DeclarationNotificationController(DeclarationRepository declarationRepository,
                                             PoliticalAgentRepository politicalAgentRepository) {
        this.declarationRepository = declarationRepository;
        this.politicalAgentRepository = politicalAgentRepository;
    }

    /**
     * Counts the declarations of the agent with the given email that were
     * returned for correction (status REJECTED).
     *
     * @param email the email of the logged-in agent
     * @return the number of declarations returned for correction; 0 if the
     *         email does not belong to a registered political agent
     */
    public int countReturnedForCorrection(String email) {
        PoliticalAgent agent = politicalAgentRepository.getByEmail(email);
        if (agent == null) {
            return 0;
        }
        return declarationRepository
                .getDeclarationsForAgentByStatus(agent, DeclarationStatus.REJECTED)
                .size();
    }
}
