package pt.ipp.isep.dei.domain;

import java.util.Date;

public class Income {

    private final Organization organization;
    private final double amount;
    private final String source;
    private final Date date;

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

    public Organization getOrganization() { return organization; }
    public double getAmount() { return amount; }
    public String getSource() { return source; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Income{org='%s', amount=%.2f, source='%s'}", organization.getName(), amount, source);
    }
}
