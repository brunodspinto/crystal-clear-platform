package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class DeclarationTest {

    private static final Date NOW = new Date();

    private PoliticalAgent createAgent() {
        return new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678", "123456789", NOW, null);
    }

    private Organization createOrg() {
        return new Organization("TechCorp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
    }

    private String createFunction() {
        return "Director";
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
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Declaration(null, createAgent(), NOW);
            }
        });
    }

    @Test
    void ensureDeclarationFailsWithNullAgent() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Declaration(DeclarationType.INITIAL, null, NOW);
            }
        });
    }

    @Test
    void ensureDeclarationFailsWithNullSubmissionDate() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Declaration(DeclarationType.INITIAL, createAgent(), null);
            }
        });
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
                50000, 0, 0, NOW, null);
        assertEquals(1, d.getPositionEntries().size());
    }

    @Test
    void ensureMultiplePositionEntriesCanBeAdded() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, 0, NOW, null);
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PRIVATE, 30000, 5000, 0, NOW, null);
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
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                d.setStatus((DeclarationStatus) null);
            }
        });
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
        d.addPositionEntry(createOrg(), createFunction(), PositionNature.PUBLIC, 50000, 0, 0, NOW, null);
        assertNotSame(d.getPositionEntries(), d.getPositionEntries());
    }

    // -------------------------------------------------------------------------
    // setStatus(ValidationOutcome)
    // -------------------------------------------------------------------------

    @Test
    void ensureSetStatusWithValidatedOutcomeSetsValidated() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.setStatus(ValidationOutcome.VALIDATED);
        assertEquals(DeclarationStatus.VALIDATED, d.getStatus());
    }

    @Test
    void ensureSetStatusWithReturnedForCorrectionSetsRejected() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        d.setStatus(ValidationOutcome.RETURNED_FOR_CORRECTION);
        assertEquals(DeclarationStatus.REJECTED, d.getStatus());
    }

    @Test
    void ensureSetStatusWithNullOutcomeFails() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                d.setStatus((ValidationOutcome) null);
            }
        });
    }

    // -------------------------------------------------------------------------
    // getDetails
    // -------------------------------------------------------------------------

    @Test
    void ensureGetDetailsReturnsNonEmptyString() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        String details = d.getDetails();
        assertNotNull(details);
        assertFalse(details.isBlank());
    }

    @Test
    void ensureGetDetailsContainsAgentName() {
        Declaration d = new Declaration(DeclarationType.INITIAL, createAgent(), NOW);
        assertTrue(d.getDetails().contains("Agent Name"));
    }

    // -------------------------------------------------------------------------
    // Defensive copies for all section lists
    // -------------------------------------------------------------------------
    private PoliticalAgent agent() {
        return new PoliticalAgent("Agent", "agent@gov.pt",
                "12345678", "123456789", NOW, null);
    }

    private Organization org() {
        return new Organization("TechCorp", OrganizationNature.PRIVATE, OrganizationType.COMPANY);
    }

    private Declaration declaration() {
        return new Declaration(DeclarationType.INITIAL, agent(), NOW);
    }


    @Test
    void ensureGetSubsidyEntriesReturnsDefensiveCopy() {
        Declaration d = declaration();
        d.addSubsidyEntry(org(), 500.0, "Grant", NOW);
        assertNotSame(d.getSubsidyEntries(), d.getSubsidyEntries());
    }

    @Test
    void ensureGetAssetEntriesReturnsDefensiveCopy() {
        Declaration d = declaration();
        d.addAssetEntry(AssetType.REAL_ESTATE, 100000.0, new RealEstate("Flat", "Braga"));
        assertNotSame(d.getAssetEntries(), d.getAssetEntries());
    }

    @Test
    void ensureGetBusinessParticipationsReturnsDefensiveCopy() {
        Declaration d = declaration();
        d.addBusinessParticipation(org(), 123456789L, 5000.0, 5.0);
        assertNotSame(d.getBusinessParticipations(), d.getBusinessParticipations());
    }

    @Test
    void ensureGetAttachmentsReturnsDefensiveCopy() {
        Declaration d = declaration();
        d.addAttachment("doc.pdf", NOW);
        assertNotSame(d.getAttachments(), d.getAttachments());
    }

    @Test
    void ensureGetIncomesReturnsDefensiveCopy() {
        Declaration d = declaration();
        d.addIncome(org(), 2000.0, "Consulting", NOW);
        assertNotSame(d.getIncomes(), d.getIncomes());
    }

    // -------------------------------------------------------------------------
    // addIncome
    // -------------------------------------------------------------------------

    @Test
    void ensureAddIncomeStoresEntry() {
        Declaration d = declaration();
        d.addIncome(org(), 2000.0, "Consulting", NOW);
        assertEquals(1, d.getIncomes().size());
    }

    @Test
    void ensureAddMultipleIncomesStoresAll() {
        Declaration d = declaration();
        d.addIncome(org(), 1000.0, "Consulting", NOW);
        d.addIncome(org(), 2000.0, "Board fee", NOW);
        assertEquals(2, d.getIncomes().size());
    }

    @Test
    void ensureIncomesListStartsEmpty() {
        Declaration d = declaration();
        assertTrue(d.getIncomes().isEmpty());
    }

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    void ensureToStringIsNonBlank() {
        Declaration d = declaration();
        assertFalse(d.toString().isBlank());
    }

    @Test
    void ensureToStringContainsAgentName() {
        Declaration d = declaration();
        assertTrue(d.toString().contains("Agent"));
    }

    @Test
    void ensureToStringContainsDeclarationType() {
        Declaration d = declaration();
        assertTrue(d.toString().contains("INITIAL") || d.toString().contains("Initial"));
    }

    // -------------------------------------------------------------------------
    // getId
    // -------------------------------------------------------------------------

    @Test
    void ensureGetIdIsNotNull() {
        assertNotNull(declaration().getId());
    }

    @Test
    void ensureTwoDeclarationsHaveDifferentIds() {
        assertNotEquals(declaration().getId(), declaration().getId());
    }
}
