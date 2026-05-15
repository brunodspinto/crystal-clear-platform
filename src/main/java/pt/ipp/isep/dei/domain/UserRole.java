package pt.ipp.isep.dei.domain;

/**
 * The enum User role.
 */
public enum UserRole {
    /**
     * The Political agent.
     */
    POLITICAL_AGENT("Political Agent"),
    /**
     * The Citizen.
     */
    CITIZEN("Ordinary Citizen"),
    /**
     * Journalist user role.
     */
    JOURNALIST("Journalist"),
    /**
     * The Ethics committee.
     */
    ETHICS_COMMITTEE("Ethics Committee Member"),
    /**
     * The Administrator.
     */
    ADMINISTRATOR("System Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
