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
 * Controller responsible for consulting the integrated situation of a political agent
 * on a given reference date (US09).
 * <p>
 * The integrated situation is the set of validated declarations submitted by the agent
 * on or before the reference date. The UI receives the data as DTOs (ESOFT &mdash; DTO
 * pattern) and is responsible for combining the entry lines (positions, subsidies,
 * assets, business participations) of those declarations into a single view.
 */
public class ConsultIntegratedSituationController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final DeclarationRepository declarationRepository;
    private final PoliticalAgentMapper politicalAgentMapper = new PoliticalAgentMapper();
    private final DeclarationMapper declarationMapper = new DeclarationMapper();

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
     * Ethics Committee member (AC1), as DTOs.
     *
     * @return list of {@link PoliticalAgentDTO}.
     */
    public List<PoliticalAgentDTO> getPoliticalAgents() {
        return politicalAgentMapper.toDTO(politicalAgentRepository.getAll());
    }

    /**
     * Returns the validated declarations of the given agent submitted on or before
     * the reference date (AC2), as DTOs. The list is empty when no declarations
     * match (AC3).
     *
     * @param agentDto      the selected political agent (identified by email).
     * @param referenceDate the date for which the integrated situation is requested.
     * @return list of matching declaration DTOs; never null.
     * @throws IllegalArgumentException if any argument is null or the agent is unknown.
     */
    public List<DeclarationDTO> getIntegratedSituation(PoliticalAgentDTO agentDto, Date referenceDate) {
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
        return declarationMapper.toDTO(
                declarationRepository.getValidatedDeclarationsForAgentUpTo(agent, referenceDate));
    }
}
