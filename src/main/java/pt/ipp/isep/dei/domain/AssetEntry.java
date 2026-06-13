package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * Represents a single asset entry within a Declaration of Interests.
 * Holds the asset value and a detail object whose type depends on the AssetType:
 * {@link RealEstate} for REAL_ESTATE, {@link VehicleAsset} for VEHICLES,
 * and {@link StockAsset} for STOCKS.
 */
public class AssetEntry implements Serializable {

    private static final long serialVersionUID = 1L;

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
                if (detail.getClass() != RealEstate.class) {
                    throw new IllegalArgumentException("Detail must be a RealEstate for REAL_ESTATE type.");
                }
                this.realEstate = (RealEstate) detail;
                this.vehicleAsset = null;
                this.stockAsset = null;
                break;
            case VEHICLES:
                if (detail.getClass() != VehicleAsset.class) {
                    throw new IllegalArgumentException("Detail must be a VehicleAsset for VEHICLES type.");
                }
                this.realEstate = null;
                this.vehicleAsset = (VehicleAsset) detail;
                this.stockAsset = null;
                break;
            case STOCKS:
                if (detail.getClass() != StockAsset.class) {
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

    /**
     * Gets asset type.
     *
     * @return the category of the asset.
     */
    public AssetType getAssetType() { return assetType; }

    /**
     * Gets asset value.
     *
     * @return the declared value of the asset.
     */
    public double getAssetValue() { return assetValue; }

    /**
     * Gets real estate.
     *
     * @return the real estate detail, or {@code null} if the type is not REAL_ESTATE.
     */
    public RealEstate getRealEstate() { return realEstate; }

    /**
     * Gets vehicle asset.
     *
     * @return the vehicle detail, or {@code null} if the type is not VEHICLES.
     */
    public VehicleAsset getVehicleAsset() { return vehicleAsset; }

    /**
     * Gets stock asset.
     *
     * @return the stock detail, or {@code null} if the type is not STOCKS.
     */
    public StockAsset getStockAsset() { return stockAsset; }

    /**
     * Returns the detail object for this asset, whatever its type: a
     * {@link RealEstate}, {@link VehicleAsset} or {@link StockAsset}. Useful
     * when copying an asset entry without needing to know its concrete type.
     *
     * @return the non-null detail object matching the asset type.
     */
    public Object getDetail() {
        if (realEstate != null) {
            return realEstate;
        }
        if (vehicleAsset != null) {
            return vehicleAsset;
        }
        return stockAsset;
    }

    @Override
    public String toString() {
        return String.format("%s (%s), value %.2f", assetType, getDetail(), assetValue);
    }
}
