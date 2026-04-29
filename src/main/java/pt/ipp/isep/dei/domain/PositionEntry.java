package pt.ipp.isep.dei.domain;

import java.util.Date;

/**
 * Represents a single professional position entry within a Declaration of Interests.
 * Captures the organization, function, nature, remuneration, and tenure dates.
 */
public class PositionEntry {

    private final Organization organization;
    private final Function function;
    private final PositionNature nature;
    private final double grossSalary;
    private final double sideIncome;
    private final Date startDate;
    private final Date endDate;

    /**
     * Creates a new PositionEntry.
     *
     * @param organization the organization where the position is held.
     * @param function     the function performed at the organization.
     * @param nature       the legal nature of the position (public, private, social).
     * @param grossSalary  the annual gross salary; must be non-negative.
     * @param sideIncome   additional earnings; must be non-negative.
     * @param startDate    the start date of the position; cannot be null.
     * @param endDate      the end date of the position; may be null if still active.
     * @throws IllegalArgumentException if any required argument is invalid.
     */
    public PositionEntry(Organization organization, Function function, PositionNature nature,
                         double grossSalary, double sideIncome, Date startDate, Date endDate) {
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null.");
        }
        if (function == null) {
            throw new IllegalArgumentException("Function cannot be null.");
        }
        if (nature == null) {
            throw new IllegalArgumentException("Nature cannot be null.");
        }
        if (grossSalary < 0) {
            throw new IllegalArgumentException("Gross salary must be non-negative.");
        }
        if (sideIncome < 0) {
            throw new IllegalArgumentException("Side income must be non-negative.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        this.organization = organization;
        this.function = function;
        this.nature = nature;
        this.grossSalary = grossSalary;
        this.sideIncome = sideIncome;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** @return the organization where the position is held. */
    public Organization getOrganization() { return organization; }

    /** @return the function performed. */
    public Function getFunction() { return function; }

    /** @return the legal nature of the position. */
    public PositionNature getNature() { return nature; }

    /** @return the annual gross salary. */
    public double getGrossSalary() { return grossSalary; }

    /** @return the side income (additional earnings). */
    public double getSideIncome() { return sideIncome; }

    /** @return the start date of the position. */
    public Date getStartDate() { return startDate; }

    /** @return the end date of the position, or {@code null} if still active. */
    public Date getEndDate() { return endDate; }

    @Override
    public String toString() {
        return String.format("PositionEntry{org='%s', function='%s', nature=%s, grossSalary=%.2f, sideIncome=%.2f}",
                organization.getName(), function.getDesignation(), nature, grossSalary, sideIncome);
    }
}
