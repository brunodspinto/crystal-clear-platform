package pt.ipp.isep.dei.dto;

/**
 * Data Transfer Object (DTO) carrying the data of a political agent that the UI
 * layer needs to display and select one, decoupling the UI from the domain
 * (ESOFT &mdash; DTO pattern). It holds no business logic. The {@code email}
 * identifies the agent so the controller can map the DTO back to the
 * corresponding domain object.
 */
public class PoliticalAgentDTO {

    private final String name;
    private final String email;

    /**
     * Creates a political agent DTO.
     *
     * @param name  the agent's name.
     * @param email the agent's email (used as identifier to map back to the domain).
     */
    public PoliticalAgentDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return the agent's name (shown to the user).
     */
    public String getName() {
        return name;
    }

    /**
     * @return the agent's email (identifier to map back to the domain).
     */
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name;
    }
}
