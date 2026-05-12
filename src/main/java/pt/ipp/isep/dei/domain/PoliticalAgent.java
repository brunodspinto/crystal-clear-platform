package pt.ipp.isep.dei.domain;

import java.util.Date;

public class PoliticalAgent extends User {

    private final String nationalIdentityCard;
    private final String taxIdentificationNumber;
    private final Date mandateStart;
    private final Date mandateEnd;

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

    public String getNationalIdentityCard() { return nationalIdentityCard; }
    public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
    public Date getMandateStart() { return mandateStart; }
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
