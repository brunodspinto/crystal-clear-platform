package pt.ipp.isep.dei.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * The type Political agent.
 */
public class PoliticalAgent extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nationalIdentityCard;
    private final String taxIdentificationNumber;
    private final Date mandateStart;
    private final Date mandateEnd;

    /**
     * Instantiates a new Political agent.
     *
     * @param name                    the name
     * @param email                   the email
     * @param nationalIdentityCard    the national identity card
     * @param taxIdentificationNumber the tax identification number
     * @param mandateStart            the mandate start
     * @param mandateEnd              the mandate end
     */
    public PoliticalAgent(String name, String email, String nationalIdentityCard,
                          String taxIdentificationNumber, Date mandateStart, Date mandateEnd) {
        super(name, email);
        if (nationalIdentityCard == null || nationalIdentityCard.isBlank())
            throw new IllegalArgumentException("National Identity Card cannot be null or empty");
        if (taxIdentificationNumber == null || taxIdentificationNumber.isBlank())
            throw new IllegalArgumentException("Tax Identification Number cannot be null or empty");
        if (mandateStart == null)
            throw new IllegalArgumentException("Mandate start date cannot be null");
        this.nationalIdentityCard = nationalIdentityCard;
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.mandateStart = mandateStart;
        this.mandateEnd = mandateEnd;
    }

    /**
     * Gets national identity card.
     *
     * @return the national identity card
     */
    public String getNationalIdentityCard() { return nationalIdentityCard; }

    /**
     * Gets tax identification number.
     *
     * @return the tax identification number
     */
    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }

    /**
     * Gets mandate start.
     *
     * @return the mandate start
     */
    public Date getMandateStart() { return mandateStart; }

    /**
     * Gets mandate end.
     *
     * @return the mandate end
     */
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

    public PoliticalAgent clone() {
        return new PoliticalAgent(name, email, nationalIdentityCard, taxIdentificationNumber, mandateStart, mandateEnd);
    }
}
