package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockAssetTest {

    @Test
    void ensureStockAssetCreationWorks() {
        StockAsset sa = new StockAsset("EDP shares");
        assertNotNull(sa);
    }

    @Test
    void ensureStockAssetFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new StockAsset(null));
    }

    @Test
    void ensureStockAssetFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new StockAsset("   "));
    }

    @Test
    void ensureGetDescriptionReturnsCorrectValue() {
        StockAsset sa = new StockAsset("Galp Energia shares");
        assertEquals("Galp Energia shares", sa.getDescription());
    }
}
