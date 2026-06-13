package pt.ipp.isep.dei.domain;

import java.io.Serializable;
/**
 * The enum User role.
 */
public enum UserRole implements Serializable{

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

    /**
     * Returns whether this role requires an identification document on registration.
     *
     * @return true if a document is required
     */
    public boolean requiresDocument() {
        return this == JOURNALIST || this == CITIZEN;
    }

    /**
     * Returns whether this role needs the extra Political Agent data on
     * registration (national identity card, tax number and mandate start).
     *
     * @return true if this is the Political Agent role
     */
    public boolean requiresPoliticalData() {
        return this == POLITICAL_AGENT;
    }

    /**
     * Returns the label describing what document is required, or null if none.
     *
     * @return the document label
     */
    public String getDocumentLabel() {
        if (this == JOURNALIST) {
            return "Press card number (Cartão de Jornalista): ";
        }
        if (this == CITIZEN) {
            return "National identity card number (Cartão de Cidadão): ";
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
