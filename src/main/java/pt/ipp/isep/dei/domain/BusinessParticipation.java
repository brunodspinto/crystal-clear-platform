package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * Represents a single business participation (holding) entry within a Declaration of Interests.
 * Captures the company, NIF, total stock value, and ownership percentage.
 */
public class BusinessParticipation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Organization organization;
    private final long companyNIF;
    private final double totalValueInStocks;
    private final double companyPercentage;

    /**
     * Creates a new BusinessParticipation.
     *
     * @param organization       the company in which participation is held.
     * @param companyNIF         the tax identification number (NIF) of the company.
     * @param totalValueInStocks the total declared value of stocks held; must be non-negative.
     * @param companyPercentage  the percentage of the company held; must be between 0 and 100.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public BusinessParticipation(Organization organization, long companyNIF,
                                  double totalValueInStocks, double companyPercentage) {
        if (organization == null) {
            throw new IllegalArgumentException("Organization cannot be null.");
        }
        if (totalValueInStocks < 0) {
            throw new IllegalArgumentException("Total value in stocks must be non-negative.");
        }
        if (companyPercentage < 0 || companyPercentage > 100) {
            throw new IllegalArgumentException("Company percentage must be between 0 and 100.");
        }
        this.organization = organization;
        this.companyNIF = companyNIF;
        this.totalValueInStocks = totalValueInStocks;
        this.companyPercentage = companyPercentage;
    }

    /**
     * Gets organization.
     *
     * @return the company in which participation is held.
     */
    public Organization getOrganization() { return organization; }

    /**
     * Gets company nif.
     *
     * @return the NIF of the company.
     */
    public long getCompanyNIF() { return companyNIF; }

    /**
     * Gets total value in stocks.
     *
     * @return the total declared value of stocks held.
     */
    public double getTotalValueInStocks() { return totalValueInStocks; }

    /**
     * Gets company percentage.
     *
     * @return the ownership percentage of the company.
     */
    public double getCompanyPercentage() { return companyPercentage; }

    @Override
    public String toString() {
        return String.format("%s (NIF %d), value %.2f, %.2f%%",
                organization.getName(), companyNIF, totalValueInStocks, companyPercentage);
    }
}
