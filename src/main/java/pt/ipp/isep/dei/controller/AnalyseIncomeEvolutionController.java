package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Date;
import java.util.List;

// US10 - controller to analyse income evolution of a political agent over a period.
// Only validated declarations counted, ordered by submission date.
public class AnalyseIncomeEvolutionController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;

    public AnalyseIncomeEvolutionController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.declarationRepository = repos.getDeclarationRepository();
    }

    // used in tests
    public AnalyseIncomeEvolutionController(PoliticalAgentRepository politicalAgentRepository,
                                            DeclarationRepository declarationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
    }

    public List<PoliticalAgent> getPoliticalAgents() {
        return politicalAgentRepository.getAll();
    }

    public List<Declaration> getIncomeEvolution(PoliticalAgent agent, Date startDate, Date endDate) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (startDate.after(endDate)) {
            // AC2 - reject before going to the repository
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        return declarationRepository.getValidatedDeclarationsForAgentBetween(agent, startDate, endDate);
    }
}
