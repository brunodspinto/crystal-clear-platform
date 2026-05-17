package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetEntryTest {

    // -------------------------------------------------------------------------
    // RealEstate detail
    // -------------------------------------------------------------------------

    @Test
    void ensureRealEstateCreationWorks() {
        assertDoesNotThrow(() -> new AssetEntry(AssetType.REAL_ESTATE, 250000.0,
                new RealEstate("House", "Lisbon")));
    }

    @Test
    void ensureRealEstateGetterReturnsDetail() {
        RealEstate re = new RealEstate("House", "Lisbon");
        AssetEntry ae = new AssetEntry(AssetType.REAL_ESTATE, 250000.0, re);
        assertEquals(re, ae.getRealEstate());
    }

    @Test
    void ensureVehicleAndStockNullForRealEstateEntry() {
        AssetEntry ae = new AssetEntry(AssetType.REAL_ESTATE, 250000.0,
                new RealEstate("House", "Lisbon"));
        assertNull(ae.getVehicleAsset());
        assertNull(ae.getStockAsset());
    }

    // -------------------------------------------------------------------------
    // VehicleAsset detail
    // -------------------------------------------------------------------------

    @Test
    void ensureVehicleCreationWorks() {
        assertDoesNotThrow(() -> new AssetEntry(AssetType.VEHICLES, 30000.0,
                new VehicleAsset("BMW 3 Series")));
    }

    @Test
    void ensureVehicleGetterReturnsDetail() {
        VehicleAsset va = new VehicleAsset("Tesla Model 3");
        AssetEntry ae = new AssetEntry(AssetType.VEHICLES, 45000.0, va);
        assertEquals(va, ae.getVehicleAsset());
    }

    @Test
    void ensureRealEstateAndStockNullForVehicleEntry() {
        AssetEntry ae = new AssetEntry(AssetType.VEHICLES, 20000.0, new VehicleAsset("Ford"));
        assertNull(ae.getRealEstate());
        assertNull(ae.getStockAsset());
    }

    // -------------------------------------------------------------------------
    // StockAsset detail
    // -------------------------------------------------------------------------

    @Test
    void ensureStockCreationWorks() {
        assertDoesNotThrow(() -> new AssetEntry(AssetType.STOCKS, 8000.0,
                new StockAsset("EDP shares")));
    }

    @Test
    void ensureStockGetterReturnsDetail() {
        StockAsset sa = new StockAsset("BCP shares");
        AssetEntry ae = new AssetEntry(AssetType.STOCKS, 5000.0, sa);
        assertEquals(sa, ae.getStockAsset());
    }

    @Test
    void ensureRealEstateAndVehicleNullForStockEntry() {
        AssetEntry ae = new AssetEntry(AssetType.STOCKS, 5000.0, new StockAsset("Galp"));
        assertNull(ae.getRealEstate());
        assertNull(ae.getVehicleAsset());
    }

    // -------------------------------------------------------------------------
    // Type/detail mismatch guards
    // -------------------------------------------------------------------------

    @Test
    void ensureVehicleDetailForRealEstateTypeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, 250000.0, new VehicleAsset("BMW")));
    }

    @Test
    void ensureStockDetailForVehiclesTypeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.VEHICLES, 30000.0, new StockAsset("EDP")));
    }

    @Test
    void ensureRealEstateDetailForStocksTypeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.STOCKS, 5000.0, new RealEstate("House", "Lisbon")));
    }

    // -------------------------------------------------------------------------
    // Null / invalid guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullTypeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(null, 100.0, new RealEstate("House", "Lisbon")));
    }

    @Test
    void ensureNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, -1.0, new RealEstate("House", "Lisbon")));
    }

    @Test
    void ensureZeroValueWorks() {
        assertDoesNotThrow(() -> new AssetEntry(AssetType.REAL_ESTATE, 0.0,
                new RealEstate("Land", "Alentejo")));
    }

    @Test
    void ensureNullDetailThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new AssetEntry(AssetType.REAL_ESTATE, 100.0, null));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGetAssetTypeReturnsCorrectValue() {
        AssetEntry ae = new AssetEntry(AssetType.VEHICLES, 20000.0, new VehicleAsset("Honda"));
        assertEquals(AssetType.VEHICLES, ae.getAssetType());
    }

    @Test
    void ensureGetAssetValueReturnsCorrectValue() {
        AssetEntry ae = new AssetEntry(AssetType.STOCKS, 12500.0, new StockAsset("NOS shares"));
        assertEquals(12500.0, ae.getAssetValue(), 0.001);
    }
}
