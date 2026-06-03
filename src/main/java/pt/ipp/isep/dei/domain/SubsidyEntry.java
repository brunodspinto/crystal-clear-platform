package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single support or subsidy entry within a Declaration of Interests.
 */
public class SubsidyEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Organization organization;
    private final double amount;
    private final String description;
    private final Date date;

    /**
     * Creates a new SubsidyEntry.
     *
     * @param organization the organization from which the subsidy was received.
     * @param amount       the subsidy amount; must be non-negative.
     * @param description  a description of the subsidy.
     * @param date         the date the subsidy was received.
     * @throws IllegalArgumentException if any required argument is invalid.
     */
    public SubsidyEntry(Organization organization, double amount, String description, Date date) {
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null.");
        }
        this.organization = organization;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    /**
     * Gets organization.
     *
     * @return the organization from which the subsidy was received.
     */
    public Organization getOrganization() { return organization; }

    /**
     * Gets amount.
     *
     * @return the subsidy amount.
     */
    public double getAmount() { return amount; }

    /**
     * Gets description.
     *
     * @return the description of the subsidy.
     */
    public String getDescription() { return description; }

    /**
     * Gets date.
     *
     * @return the date the subsidy was received.
     */
    public Date getDate() { return date; }

    @Override
    public String toString() {
        return String.format("SubsidyEntry{org='%s', amount=%.2f, description='%s'}",
                organization.getName(), amount, description);
    }
}
