package pt.ipp.isep.dei.domain;

import java.util.Date;

/**
 * Represents a political agent who can be the subject of a citizen complaint.
 * Identity is based on the tax identification number (NIF).
 */
public class PoliticalAgent {
    private final String name;
    private final String email;
    private final String nationalIdentityCard;
    private final String taxIdentificationNumber;
    private final Date mandateStart;
    private final Date mandateEnd;

    /**
     * Creates a new PoliticalAgent.
     *
     * @param name                    the agent's full name.
     * @param email                   the agent's email address.
     * @param nationalIdentityCard    the agent's national identity card number.
     * @param taxIdentificationNumber the agent's tax identification number (NIF), used as identity.
     * @param mandateStart            the start date of the mandate.
     * @param mandateEnd              the end date of the mandate, or {@code null} if still active.
     * @throws IllegalArgumentException if any required field is null or blank.
     */
    public PoliticalAgent(String name, String email, String nationalIdentityCard,
                          String taxIdentificationNumber, Date mandateStart, Date mandateEnd) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (nationalIdentityCard == null || nationalIdentityCard.isBlank()) {
            throw new IllegalArgumentException("National Identity Card cannot be null or empty");
        }
        if (taxIdentificationNumber == null || taxIdentificationNumber.isBlank()) {
            throw new IllegalArgumentException("Tax Identification Number cannot be null or empty");
        }
        if (mandateStart == null) {
            throw new IllegalArgumentException("Mandate start date cannot be null");
        }
        this.name = name;
        this.email = email;
        this.nationalIdentityCard = nationalIdentityCard;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.mandateStart = mandateStart;
        this.mandateEnd = mandateEnd;
    }

    /** @return the agent's full name. */
    public String getName() { return name; }
    /** @return the agent's email address. */
    public String getEmail() { return email; }
    /** @return the agent's national identity card number. */
    public String getNationalIdentityCard() { return nationalIdentityCard; }
    /** @return the agent's tax identification number (NIF). */
    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
    /** @return the mandate start date. */
    public Date getMandateStart() { return mandateStart; }
    /** @return the mandate end date, or {@code null} if the mandate is still active. */
    public Date getMandateEnd() { return mandateEnd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PoliticalAgent)) return false;
        PoliticalAgent that = (PoliticalAgent) o;
        return taxIdentificationNumber.equals(that.taxIdentificationNumber);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, email);
    }

    /**
     * Creates and returns a copy of this political agent.
     *
     * @return a new {@link PoliticalAgent} with the same field values.
     */
    public PoliticalAgent clone() {
        return new PoliticalAgent(name, email, nationalIdentityCard, taxIdentificationNumber, mandateStart, mandateEnd);
    }
}
