package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.List;

/**
 * Repository for storing and retrieving {@link PoliticalAgent} instances.
 */
public class PoliticalAgentRepository {
    private final List<PoliticalAgent> politicalAgents;

    /**
     * Creates an empty PoliticalAgentRepository.
     */
    public PoliticalAgentRepository() {
        politicalAgents = new ArrayList<>();
    }

    /**
     * Saves a political agent if no duplicate (by NIF) already exists.
     * A clone is stored to protect internal state.
     *
     * @param agent the political agent to save.
     * @return {@code true} if saved, {@code false} if a duplicate exists.
     */
    public boolean save(PoliticalAgent agent) {
        if (politicalAgents.contains(agent)) {
            return false;
        }
        return politicalAgents.add(agent.clone());
    }

    /**
     * Returns an unmodifiable list of all registered political agents.
     *
     * @return list of all political agents.
     */
    public List<PoliticalAgent> getAll() {
        return new ArrayList<>(politicalAgents);
    }

    /**
     * Finds a political agent by email address (case-insensitive).
     *
     * @param email the email to search for.
     * @return an {@link Optional} containing the agent, or empty if not found.
     */
    public Optional<PoliticalAgent> getByEmail(String email) {
        for (PoliticalAgent agent : politicalAgents) {
            if (agent.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(agent);
            }
        }
        return Optional.empty();
    }
}
