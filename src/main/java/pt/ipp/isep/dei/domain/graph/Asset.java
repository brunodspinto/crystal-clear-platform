package pt.ipp.isep.dei.domain.graph;

/**
 * The type Asset.
 */
public class Asset extends Entity {

    private final String assetType;
    private final String country;
    private final double estimatedValue;

    /**
     * Instantiates a new Asset.
     *
     * @param id             the id
     * @param type           the type
     * @param startDate      the start date
     * @param endDate        the end date
     * @param assetType      the asset type
     * @param country        the country
     * @param estimatedValue the estimated value
     */
    public Asset(String id, String type, String startDate, String endDate,
                 String assetType, String country, double estimatedValue) {
        super(id, type, startDate, endDate);
        this.assetType = assetType == null ? "" : assetType;
        this.country = country == null ? "" : country;
        this.estimatedValue = estimatedValue;
    }

    /**
     * Gets asset type.
     *
     * @return the asset type
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Gets estimated value.
     *
     * @return the estimated value
     */
    public double getEstimatedValue() {
        return estimatedValue;
    }

    @Override
    public String getDetails() {
        StringBuilder sb = new StringBuilder("Asset type: ").append(assetType);
        if (!country.isEmpty()) sb.append(" | Country: ").append(country);
        sb.append(" | Value: ").append(estimatedValue);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Asset{id='" + getId() + "', assetType='" + assetType + "', country='" + country + "', estimatedValue=" + estimatedValue + "}";
    }
}
