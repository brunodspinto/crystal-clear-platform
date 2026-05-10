package pt.ipp.isep.dei.domain;

public enum UserRole {
    POLITICAL_AGENT("Political Agent"),
    CITIZEN("Ordinary Citizen"),
    JOURNALIST("Journalist"),
    ETHICS_COMMITTEE("Ethics Committee Member"),
    ADMINISTRATOR("System Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
