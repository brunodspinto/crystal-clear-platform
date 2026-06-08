package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper (ESOFT &mdash; DTO pattern) that converts {@link PoliticalAgent} domain
 * objects into {@link PoliticalAgentDTO}. The controller delegates this
 * conversion to the mapper so the domain layer is not exposed to the UI.
 */
public class PoliticalAgentMapper {

    /**
     * Converts a single political agent into its DTO.
     *
     * @param agent the domain agent.
     * @return the corresponding {@link PoliticalAgentDTO}.
     */
    public PoliticalAgentDTO toDTO(PoliticalAgent agent) {
        return new PoliticalAgentDTO(agent.getName(), agent.getEmail());
    }

    /**
     * Converts a list of political agents into a list of DTOs.
     *
     * @param agents the domain agents.
     * @return the corresponding list of {@link PoliticalAgentDTO}.
     */
    public List<PoliticalAgentDTO> toDTO(List<PoliticalAgent> agents) {
        List<PoliticalAgentDTO> dtos = new ArrayList<>();
        for (PoliticalAgent agent : agents) {
            dtos.add(toDTO(agent));
        }
        return dtos;
    }
}
