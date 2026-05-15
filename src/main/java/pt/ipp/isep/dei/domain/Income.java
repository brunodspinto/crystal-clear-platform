package pt.ipp.isep.dei.domain;

import java.util.Date;

/**
 * The type Income.
 */
public class Income {

    private final Organization organization;
    private final double amount;
    private final String source;
    private final Date date;

    /**
     * Instantiates a new Income.
     *
     * @param organization the organization
     * @param amount       the amount
     * @param source       the source
     * @param date         the date
     */
    public Income(Organization organization, double amount, String source, Date date) {
        if (organization == null)
            throw new IllegalArgumentException("Organization cannot be null");
        if (source == null || source.isBlank())
            throw new IllegalArgumentException("Source cannot be null or blank");
        if (date == null)
            throw new IllegalArgumentException("Date cannot be null");
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
        this.organization = organization;
        this.amount = amount;
        this.source = source;
        this.date = date;
    }

    /**
     * Gets organization.
     *
     * @return the organization
     */
    public Organization getOrganization() { return organization; }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public double getAmount() { return amount; }

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() { return source; }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Income{org='%s', amount=%.2f, source='%s'}", organization.getName(), amount, source);
    }
}
