package pt.ipp.isep.dei.domain;

/**
 * Represents a single asset entry within a Declaration of Interests.
 * Holds the asset value and a detail object whose type depends on the AssetType:
 * {@link RealEstate} for REAL_ESTATE, {@link VehicleAsset} for VEHICLES,
 * and {@link StockAsset} for STOCKS.
 */
public class AssetEntry {

    private final AssetType assetType;
    private final double assetValue;
    private final RealEstate realEstate;
    private final VehicleAsset vehicleAsset;
    private final StockAsset stockAsset;

    /**
     * Creates a new AssetEntry.
     * The {@code detail} argument must match the {@code assetType}:
     * <ul>
     *   <li>{@link AssetType#REAL_ESTATE} → detail must be a {@link RealEstate}</li>
     *   <li>{@link AssetType#VEHICLES}    → detail must be a {@link VehicleAsset}</li>
     *   <li>{@link AssetType#STOCKS}      → detail must be a {@link StockAsset}</li>
     * </ul>
     *
     * @param assetType  the category of the asset; cannot be null.
     * @param assetValue the declared value of the asset; must be non-negative.
     * @param detail     the detail object matching the asset type; cannot be null.
     * @throws IllegalArgumentException if any argument is invalid or mismatched.
     */
    public AssetEntry(AssetType assetType, double assetValue, Object detail) {
        if (assetType == null) {
            throw new IllegalArgumentException("Asset type cannot be null.");
        }
        if (assetValue < 0) {
            throw new IllegalArgumentException("Asset value must be non-negative.");
        }
        if (detail == null) {
            throw new IllegalArgumentException("Asset detail cannot be null.");
        }
        this.assetType = assetType;
        this.assetValue = assetValue;

        switch (assetType) {
            case REAL_ESTATE:
                if (!(detail instanceof RealEstate)) {
                    throw new IllegalArgumentException("Detail must be a RealEstate for REAL_ESTATE type.");
                }
                this.realEstate = (RealEstate) detail;
                this.vehicleAsset = null;
                this.stockAsset = null;
                break;
            case VEHICLES:
                if (!(detail instanceof VehicleAsset)) {
                    throw new IllegalArgumentException("Detail must be a VehicleAsset for VEHICLES type.");
                }
                this.realEstate = null;
                this.vehicleAsset = (VehicleAsset) detail;
                this.stockAsset = null;
                break;
            case STOCKS:
                if (!(detail instanceof StockAsset)) {
                    throw new IllegalArgumentException("Detail must be a StockAsset for STOCKS type.");
                }
                this.realEstate = null;
                this.vehicleAsset = null;
                this.stockAsset = (StockAsset) detail;
                break;
            default:
                throw new IllegalArgumentException("Unknown asset type: " + assetType);
        }
    }

    /** @return the category of the asset. */
    public AssetType getAssetType() { return assetType; }

    /** @return the declared value of the asset. */
    public double getAssetValue() { return assetValue; }

    /** @return the real estate detail, or {@code null} if the type is not REAL_ESTATE. */
    public RealEstate getRealEstate() { return realEstate; }

    /** @return the vehicle detail, or {@code null} if the type is not VEHICLES. */
    public VehicleAsset getVehicleAsset() { return vehicleAsset; }

    /** @return the stock detail, or {@code null} if the type is not STOCKS. */
    public StockAsset getStockAsset() { return stockAsset; }

    @Override
    public String toString() {
        return String.format("AssetEntry{type=%s, value=%.2f}", assetType, assetValue);
    }
}
