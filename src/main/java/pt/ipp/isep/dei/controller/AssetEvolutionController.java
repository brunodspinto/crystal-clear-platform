package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Controller for US15 - Examine the evolution of a political agent's total assets
 * and net worth over time, across all declarations (initial, regular, and exceptional).
 */
public class AssetEvolutionController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public AssetEvolutionController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.declarationRepository = repos.getDeclarationRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     */
    public AssetEvolutionController(PoliticalAgentRepository politicalAgentRepository,
                                     DeclarationRepository declarationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
    }

    /**
     * Returns all registered political agents.
     *
     * @return list of political agents.
     */
    public List<PoliticalAgent> getPoliticalAgents() {
        return politicalAgentRepository.getAll();
    }

    /**
     * Returns all declarations of the given agent, sorted chronologically by submission date.
     * Includes all declaration types and all statuses (US15 uses all declarations).
     *
     * @param agent the political agent; cannot be null.
     * @return chronologically sorted list of all declarations for the agent.
     * @throws IllegalArgumentException if agent is null.
     */
    public List<Declaration> getAllDeclarationsForAgent(PoliticalAgent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        List<Declaration> all = declarationRepository.getAll();
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : all) {
            if (d.getAgent().equals(agent)) {
                result.add(d);
            }
        }
        Collections.sort(result, new Comparator<Declaration>() {
            @Override
            public int compare(Declaration d1, Declaration d2) {
                return d1.getSubmissionDate().compareTo(d2.getSubmissionDate());
            }
        });
        return result;
    }
}
