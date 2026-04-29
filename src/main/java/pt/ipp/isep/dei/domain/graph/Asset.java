package pt.ipp.isep.dei.domain.graph;

public class Asset extends Entity {

    private final String assetType;
    private final String country;
    private final double estimatedValue;

    public Asset(String id, String type, String startDate, String endDate,
                 String assetType, String country, double estimatedValue) {
        super(id, type, startDate, endDate);
        this.assetType = assetType == null ? "" : assetType;
        this.country = country == null ? "" : country;
        this.estimatedValue = estimatedValue;
    }

    public String assetType() {
        return assetType;
    }

    public String country() {
        return country;
    }

    public double estimatedValue() {
        return estimatedValue;
    }
}
