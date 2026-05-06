package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Date;
import java.util.List;

/**
 * Controller responsible for consulting the integrated situation of a political agent
 * on a given reference date (US09).
 *
 * The integrated situation is the set of validated declarations submitted by the agent
 * on or before the reference date. The UI is responsible for combining the entries
 * (positions, subsidies, assets, business participations) of those declarations into a
 * single view.
 */
public class ConsultIntegratedSituationController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public ConsultIntegratedSituationController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.declarationRepository = repos.getDeclarationRepository();
    }

    /**
     * Creates a controller with injected repositories. Used in tests.
     *
     * @param politicalAgentRepository the political agent repository.
     * @param declarationRepository    the declaration repository.
     */
    public ConsultIntegratedSituationController(PoliticalAgentRepository politicalAgentRepository,
                                                DeclarationRepository declarationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
    }

    /**
     * Returns all registered political agents available for selection by the
     * Ethics Committee member (AC1).
     *
     * @return list of {@link PoliticalAgent}.
     */
    public List<PoliticalAgent> getPoliticalAgents() {
        return politicalAgentRepository.getAll();
    }

    /**
     * Returns the validated declarations of the given agent submitted on or before
     * the reference date (AC2). The list is empty when no declarations match (AC3).
     *
     * @param agent         the selected political agent.
     * @param referenceDate the date for which the integrated situation is requested.
     * @return list of matching declarations; never null.
     * @throws IllegalArgumentException if any argument is null.
     */
    public List<Declaration> getIntegratedSituation(PoliticalAgent agent, Date referenceDate) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date cannot be null.");
        }
        return declarationRepository.getValidatedDeclarationsForAgentUpTo(agent, referenceDate);
    }
}
