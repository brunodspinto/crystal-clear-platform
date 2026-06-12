package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class VehicleAssetTest {

    @Test
    void ensureVehicleAssetCreationWorks() {
        VehicleAsset va = new VehicleAsset("Toyota Corolla 2020");
        assertNotNull(va);
    }

    @Test
    void ensureVehicleAssetFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new VehicleAsset(null);
            }
        });
    }

    @Test
    void ensureVehicleAssetFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new VehicleAsset("   ");
            }
        });
    }

    @Test
    void ensureGetDescriptionReturnsCorrectValue() {
        VehicleAsset va = new VehicleAsset("BMW 320d");
        assertEquals("BMW 320d", va.getDescription());
    }
}
