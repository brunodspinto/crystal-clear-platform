package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleAssetTest {

    @Test
    void ensureVehicleAssetCreationWorks() {
        VehicleAsset va = new VehicleAsset("Toyota Corolla 2020");
        assertNotNull(va);
    }

    @Test
    void ensureVehicleAssetFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleAsset(null));
    }

    @Test
    void ensureVehicleAssetFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new VehicleAsset("   "));
    }

    @Test
    void ensureGetDescriptionReturnsCorrectValue() {
        VehicleAsset va = new VehicleAsset("BMW 320d");
        assertEquals("BMW 320d", va.getDescription());
    }
}
