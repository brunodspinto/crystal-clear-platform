package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void ensureFieldsAreKept() {
        Asset asset = new Asset("A-001", "asset", "2021-01-01", "2023-01-01",
                "real_estate", "Portugal", 250000.0);
        assertEquals("A-001", asset.getId());
        assertEquals("asset", asset.getType());
        assertEquals("2021-01-01", asset.getStartDate());
        assertEquals("2023-01-01", asset.getEndDate());
        assertEquals("real_estate", asset.getAssetType());
        assertEquals("Portugal", asset.getCountry());
        assertEquals(250000.0, asset.getEstimatedValue());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("", "asset", "", "", "real_estate", "Portugal", 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Asset("   ", "asset", "", "", "real_estate", "Portugal", 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Asset(null, "asset", "", "", "real_estate", "Portugal", 0.0));
    }

    @Test
    void ensureNullStringsDefaultToEmpty() {
        Asset asset = new Asset("A-002", "asset", null, null, null, null, 0.0);
        assertEquals("", asset.getStartDate());
        assertEquals("", asset.getEndDate());
        assertEquals("", asset.getAssetType());
        assertEquals("", asset.getCountry());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Asset a1 = new Asset("A-001", "asset", "2021-01-01", "", "real_estate", "Portugal", 250000.0);
        Asset a2 = new Asset("A-001", "asset", "2019-01-01", "", "vehicle", "Spain", 5000.0);
        assertNotEquals(a1, new Asset("A-002", "asset", "", "", "real_estate", "Portugal", 0.0));
        assertEquals(a1, a2);
    }
}
