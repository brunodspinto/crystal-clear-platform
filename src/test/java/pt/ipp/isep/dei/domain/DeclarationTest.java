package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DeclarationTest {

    private static final Date NOW = new Date();

    private PoliticalAgent createAgent() {
        return new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678", "123456789", NOW, null);
    }

    private Organization createOrg() {
        return new Organization("TechCorp", "private", OrganizationType.COMPANY);
    }

    private Function createFunction() {
        return new Function("Director");
    }

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    @Test
    void ensureDeclarationCreationWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertNotNull(d);
    }

    @Test
    void ensureDeclarationFailsWithNullType() {
        assertThrows(IllegalArgumentException.class, () ->
                new Declaration(null, createAgent(), NOW));
    }

    @Test
    void ensureDeclarationFailsWithNullAgent() {
        assertThrows(IllegalArgumentException.class, () ->
                new Declaration(DeclarationType.INITIAL, null, NOW));
    }

    @Test
    void ensureDeclarationFailsWithNullSubmissionDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new Declaration(DeclarationType.INITIAL, createAgent(), null));
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    void ensureInitialStatusIsPending() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertEquals(DeclarationStatus.PENDING, d.getStatus());
    }

    @Test
    void ensurePositionEntriesStartEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getPositionEntries().isEmpty());
    }

    @Test
    void ensureSubsidyEntriesStartEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getSubsidyEntries().isEmpty());
    }

    @Test
    void ensureAssetEntriesStartEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getAssetEntries().isEmpty());
    }

    @Test
    void ensureBusinessParticipationsStartEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getBusinessParticipations().isEmpty());
    }

    @Test
    void ensureAttachmentsStartEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getAttachments().isEmpty());
    }

    // -------------------------------------------------------------------------
    // addPositionEntry
    // -------------------------------------------------------------------------

    @Test
    void ensureAddPositionEntryWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC,
                50000, 0, NOW, null);
        assertEquals(1, d.getPositionEntries().size());
    }

    @Test
    void ensureMultiplePositionEntriesCanBeAdded() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, NOW, null);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PRIVATE, 30000, 5000, NOW, null);
        assertEquals(2, d.getPositionEntries().size());
    }

    // -------------------------------------------------------------------------
    // addSubsidyEntry
    // -------------------------------------------------------------------------

    @Test
    void ensureAddSubsidyEntryWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addSubsidyEntry(createOrg(), 1000.0, "Research grant", NOW);
        assertEquals(1, d.getSubsidyEntries().size());
    }

    // -------------------------------------------------------------------------
    // addAssetEntry
    // -------------------------------------------------------------------------

    @Test
    void ensureAddRealEstateAssetEntryWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addAssetEntry(AssetType.REAL_ESTATE, 250000.0, new RealEstate("House", "Leiria"));
        assertEquals(1, d.getAssetEntries().size());
    }

    @Test
    void ensureAddVehicleAssetEntryWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addAssetEntry(AssetType.VEHICLES, 20000.0, new VehicleAsset("Toyota Corolla"));
        assertEquals(1, d.getAssetEntries().size());
    }

    @Test
    void ensureAddStockAssetEntryWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addAssetEntry(AssetType.STOCKS, 5000.0, new StockAsset("EDP shares"));
        assertEquals(1, d.getAssetEntries().size());
    }

    // -------------------------------------------------------------------------
    // addBusinessParticipation
    // -------------------------------------------------------------------------

    @Test
    void ensureAddBusinessParticipationWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addBusinessParticipation(createOrg(), 123456789L, 10000.0, 5.0);
        assertEquals(1, d.getBusinessParticipations().size());
    }

    // -------------------------------------------------------------------------
    // addAttachment
    // -------------------------------------------------------------------------

    @Test
    void ensureAddAttachmentWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addAttachment("document.pdf", NOW);
        assertEquals(1, d.getAttachments().size());
    }

    // -------------------------------------------------------------------------
    // setStatus
    // -------------------------------------------------------------------------

    @Test
    void ensureSetStatusToValidatedWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.setStatus(DeclarationStatus.VALIDATED);
        assertEquals(DeclarationStatus.VALIDATED, d.getStatus());
    }

    @Test
    void ensureSetStatusToRejectedWorks() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.setStatus(DeclarationStatus.REJECTED);
        assertEquals(DeclarationStatus.REJECTED, d.getStatus());
    }

    @Test
    void ensureSetStatusFailsWithNull() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertThrows(IllegalArgumentException.class, () -> d.setStatus(null));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Test
    void ensureGettersReturnCorrectValues() {
        PoliticalAgent agent = createAgent();
        Declaration d = new Declaration(DeclarationType.REGULAR, agent, NOW);
        assertEquals(DeclarationType.REGULAR, d.getType());
        assertEquals(agent, d.getAgent());
        assertEquals(NOW, d.getSubmissionDate());
    }

    // -------------------------------------------------------------------------
    // Defensive copies
    // -------------------------------------------------------------------------

    @Test
    void ensureGetPositionEntriesReturnsDefensiveCopy() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, NOW, null);
        assertNotSame(d.getPositionEntries(), d.getPositionEntries());
    }
}
