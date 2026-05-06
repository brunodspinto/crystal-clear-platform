package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetEntryTest {

    // -------------------------------------------------------------------------
    // Construction — valid cases
    // -------------------------------------------------------------------------

    @Test
    void ensureAssetEntryWithRealEstateWorks() {
        AssetEntry ae = new AssetEntry(AssetType.REAL_ESTATE, 200000.0,
                new RealEstate("Apartment", "Lisbon"));
        assertNotNull(ae);
        assertEquals(AssetType.REAL_ESTATE, ae.getAssetType());
        assertEquals(200000.0, ae.getAssetValue());
        assertNotNull(ae.getRealEstate());
        assertNull(ae.getVehicleAsset());
        assertNull(ae.getStockAsset());
    }

    @Test
    void ensureAssetEntryWithVehicleWorks() {
        AssetEntry ae = new AssetEntry(AssetType.VEHICLES, 25000.0,
                new VehicleAsset("BMW 320d"));
        assertNotNull(ae);
        assertEquals(AssetType.VEHICLES, ae.getAssetType());
        assertNotNull(ae.getVehicleAsset());
        assertNull(ae.getRealEstate());
        assertNull(ae.getStockAsset());
    }

    @Test
    void ensureAssetEntryWithStocksWorks() {
        AssetEntry ae = new AssetEntry(AssetType.STOCKS, 5000.0,
                new StockAsset("EDP shares"));
        assertNotNull(ae);
        assertEquals(AssetType.STOCKS, ae.getAssetType());
        assertNotNull(ae.getStockAsset());
        assertNull(ae.getRealEstate());
        assertNull(ae.getVehicleAsset());
    }

    @Test
    void ensureZeroAssetValueIsValid() {
        AssetEntry ae = new AssetEntry(AssetType.VEHICLES, 0.0,
                new VehicleAsset("Old car"));
        assertEquals(0.0, ae.getAssetValue());
    }

    // -------------------------------------------------------------------------
    // Construction — invalid arguments
    // -------------------------------------------------------------------------

    @Test
    void ensureAssetEntryFailsWithNullType() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(null, 1000.0, new RealEstate("House", "Porto")));
    }

    @Test
    void ensureAssetEntryFailsWithNegativeValue() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, -1.0, new RealEstate("House", "Porto")));
    }

    @Test
    void ensureAssetEntryFailsWithNullDetail() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, 1000.0, null));
    }

    // -------------------------------------------------------------------------
    // Type/detail mismatch
    // -------------------------------------------------------------------------

    @Test
    void ensureRealEstateTypeMismatchFails() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, 1000.0, new VehicleAsset("Car")));
    }

    @Test
    void ensureVehiclesTypeMismatchFails() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.VEHICLES, 1000.0, new RealEstate("House", "Porto")));
    }

    @Test
    void ensureStocksTypeMismatchFails() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.STOCKS, 1000.0, new VehicleAsset("Car")));
    }
}
