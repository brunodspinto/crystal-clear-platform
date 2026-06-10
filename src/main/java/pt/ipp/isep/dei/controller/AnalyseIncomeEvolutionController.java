package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.mapper.DeclarationMapper;
import pt.ipp.isep.dei.mapper.PoliticalAgentMapper;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Date;
import java.util.List;

/**
 * Controller for US10 - Analyse the evolution of a political agent's income
 * over a period. Only validated declarations are considered and they are
 * returned ordered by submission date.
 */
public class AnalyseIncomeEvolutionController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;
    private final PoliticalAgentMapper politicalAgentMapper = new PoliticalAgentMapper();
    private final DeclarationMapper declarationMapper = new DeclarationMapper();

    /**
     * Creates a controller using the singleton repositories.
     */
    public AnalyseIncomeEvolutionController() {
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
    public AnalyseIncomeEvolutionController(PoliticalAgentRepository politicalAgentRepository,
                                            DeclarationRepository declarationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.declarationRepository = declarationRepository;
    }

    /**
     * Returns all registered political agents available for selection by the
     * Journalist (AC1).
     *
     * @return list of {@link PoliticalAgentDTO}.
     */
    public List<PoliticalAgentDTO> getPoliticalAgents() {
        return politicalAgentMapper.toDTO(politicalAgentRepository.getAll());
    }

    /**
     * Returns the validated declarations of the given agent submitted between
     * the start and end dates, in chronological order (AC3 and AC4). Empty
     * when no declarations match (AC5).
     *
     * @param agentDto  the selected political agent (identified by email).
     * @param startDate the start of the period.
     * @param endDate   the end of the period.
     * @return list of matching declarations sorted by submission date.
     * @throws IllegalArgumentException if any argument is null or if                                  startDate is after endDate (AC2).
     */
    public List<DeclarationDTO> getIncomeEvolution(PoliticalAgentDTO agentDto, Date startDate, Date endDate) {
        if (agentDto == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        PoliticalAgent agent = politicalAgentRepository.getByEmail(agentDto.getEmail());
        if (agent == null) {
            throw new IllegalArgumentException("Unknown political agent.");
        }
        return declarationMapper.toDTO(
                declarationRepository.getValidatedDeclarationsForAgentBetween(agent, startDate, endDate));
    }
}
