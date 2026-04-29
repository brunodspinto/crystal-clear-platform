package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    @Test
    void ensureFieldsAreKept() {
        Asset asset = new Asset("A-001", "asset", "2021-01-01", "2023-01-01",
                "real_estate", "Portugal", 250000.0);
        assertEquals("A-001", asset.id());
        assertEquals("asset", asset.type());
        assertEquals("2021-01-01", asset.startDate());
        assertEquals("2023-01-01", asset.endDate());
        assertEquals("real_estate", asset.assetType());
        assertEquals("Portugal", asset.country());
        assertEquals(250000.0, asset.estimatedValue());
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
        assertEquals("", asset.startDate());
        assertEquals("", asset.endDate());
        assertEquals("", asset.assetType());
        assertEquals("", asset.country());
    }

    @Test
    void ensureEqualityIsByIdOnly() {
        Asset a1 = new Asset("A-001", "asset", "2021-01-01", "", "real_estate", "Portugal", 250000.0);
        Asset a2 = new Asset("A-001", "asset", "2019-01-01", "", "vehicle", "Spain", 5000.0);
        assertNotEquals(a1, new Asset("A-002", "asset", "", "", "real_estate", "Portugal", 0.0));
        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }
}
