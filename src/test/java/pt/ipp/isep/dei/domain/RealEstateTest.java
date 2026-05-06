package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealEstateTest {

    @Test
    void ensureRealEstateCreationWorks() {
        RealEstate re = new RealEstate("Apartment", "Lisbon");
        assertNotNull(re);
    }

    @Test
    void ensureRealEstateFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new RealEstate(null, "Lisbon"));
    }

    @Test
    void ensureRealEstateFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new RealEstate("   ", "Lisbon"));
    }

    @Test
    void ensureRealEstateFailsWithNullMunicipality() {
        assertThrows(IllegalArgumentException.class, () ->
                new RealEstate("Apartment", null));
    }

    @Test
    void ensureRealEstateFailsWithBlankMunicipality() {
        assertThrows(IllegalArgumentException.class, () ->
                new RealEstate("Apartment", "   "));
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        RealEstate re = new RealEstate("Villa", "Porto");
        assertEquals("Villa", re.getDescription());
        assertEquals("Porto", re.getMunicipality());
    }
}
