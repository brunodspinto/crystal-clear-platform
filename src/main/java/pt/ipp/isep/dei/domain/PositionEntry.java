package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single professional position entry within a Declaration of Interests.
 * Captures the organization, function, nature, remuneration, and tenure dates.
 */
public class PositionEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Organization organization;
    private final String functionDesignation;
    private final PositionNature nature;
    private final double grossSalary;
    private final double sideIncomeConsulting;
    private final double sideIncomeBoardMemberships;
    private final Date startDate;
    private final Date endDate;

    /**
     * Creates a new PositionEntry.
     *
     * @param organization               the organization where the position is held.
     * @param functionDesignation        the function performed at the organization.
     * @param nature                     the legal nature of the position (public, private, social).
     * @param grossSalary                the annual gross salary; must be non-negative.
     * @param sideIncomeConsulting       consulting side income; must be non-negative.
     * @param sideIncomeBoardMemberships board membership side income; must be non-negative.
     * @param startDate                  the start date of the position; cannot be null.
     * @param endDate                    the end date of the position; may be null if still active.
     * @throws IllegalArgumentException if any required argument is invalid.
     */
    public PositionEntry(Organization organization, String functionDesignation, PositionNature nature,
                         double grossSalary, double sideIncomeConsulting,
                         double sideIncomeBoardMemberships, Date startDate, Date endDate) {
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null.");
        }
        if (functionDesignation == null || functionDesignation.trim().isEmpty()) {
            throw new IllegalArgumentException("Function designation cannot be null or empty.");
        }
        if (nature == null) {
            throw new IllegalArgumentException("Nature cannot be null.");
        }
        if (grossSalary < 0) {
            throw new IllegalArgumentException("Gross salary must be non-negative.");
        }
        if (sideIncomeConsulting < 0) {
            throw new IllegalArgumentException("Side income (consulting) must be non-negative.");
        }
        if (sideIncomeBoardMemberships < 0) {
            throw new IllegalArgumentException("Side income (board memberships) must be non-negative.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        this.organization = organization;
        this.functionDesignation = functionDesignation;
        this.nature = nature;
        this.grossSalary = grossSalary;
        this.sideIncomeConsulting = sideIncomeConsulting;
        this.sideIncomeBoardMemberships = sideIncomeBoardMemberships;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Gets organization.
     *
     * @return the organization where the position is held.
     */
    public Organization getOrganization() { return organization; }

    /**
     * Gets function designation.
     *
     * @return the function designation.
     */
    public String getFunctionDesignation() { return functionDesignation; }

    /**
     * Gets nature.
     *
     * @return the legal nature of the position.
     */
    public PositionNature getNature() { return nature; }

    /**
     * Gets gross salary.
     *
     * @return the annual gross salary.
     */
    public double getGrossSalary() { return grossSalary; }

    /**
     * Gets side income consulting.
     *
     * @return the consulting side income.
     */
    public double getSideIncomeConsulting() { return sideIncomeConsulting; }

    /**
     * Gets side income board memberships.
     *
     * @return the board memberships side income.
     */
    public double getSideIncomeBoardMemberships() { return sideIncomeBoardMemberships; }

    /**
     * Gets start date.
     *
     * @return the start date of the position.
     */
    public Date getStartDate() { return startDate; }

    /**
     * Gets end date.
     *
     * @return the end date of the position, or {@code null} if still active.
     */
    public Date getEndDate() { return endDate; }

    @Override
    public String toString() {
        return String.format("%s at %s (%s), gross salary %.2f, consulting %.2f, board memberships %.2f",
                functionDesignation, organization.getName(), nature, grossSalary, sideIncomeConsulting, sideIncomeBoardMemberships);
    }
}
