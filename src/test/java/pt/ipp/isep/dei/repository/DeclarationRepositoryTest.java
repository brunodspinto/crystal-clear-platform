package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeclarationRepositoryTest {

    private Declaration createTestDeclaration(DeclarationType type) {
        PoliticalAgent agent = new PoliticalAgent("Agent Name", "agent@gov.pt",
                "12345678", "123456789", new Date(), null);
        Declaration d = new Declaration(type, agent, new Date());
        d.addPositionEntry(
                new Organization("TechCorp", "private", OrganizationType.COMPANY),
                "Director",
                PositionNature.PUBLIC,
                50000, 0, 0, new Date(), null);
        return d;
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    void ensureSaveDeclarationWorks() {
        DeclarationRepository repo = new DeclarationRepository();
        Declaration d = createTestDeclaration(DeclarationType.INITIAL);
        assertTrue(repo.save(d));
    }

    @Test
    void ensureSaveNullDeclarationFails() {
        DeclarationRepository repo = new DeclarationRepository();
        assertThrows(IllegalArgumentException.class, () -> repo.save(null));
    }

    // -------------------------------------------------------------------------
    // getAll
    // -------------------------------------------------------------------------

    @Test
    void ensureGetAllReturnsAllSaved() {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createTestDeclaration(DeclarationType.INITIAL));
        repo.save(createTestDeclaration(DeclarationType.REGULAR));
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void ensureEmptyRepositoryReturnsEmptyList() {
        DeclarationRepository repo = new DeclarationRepository();
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void ensureGetAllReturnsDefensiveCopy() {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createTestDeclaration(DeclarationType.INITIAL));
        assertNotSame(repo.getAll(), repo.getAll());
    }

    // -------------------------------------------------------------------------
    // getDeclarationsByStatus
    // -------------------------------------------------------------------------

    @Test
    void ensureGetDeclarationsByStatusReturnsPendingOnly() {
        DeclarationRepository repo = new DeclarationRepository();
        Declaration d1 = createTestDeclaration(DeclarationType.INITIAL);
        Declaration d2 = createTestDeclaration(DeclarationType.REGULAR);
        d2.setStatus(DeclarationStatus.VALIDATED);
        repo.save(d1);
        repo.save(d2);

        List<Declaration> pending = repo.getDeclarationsByStatus(DeclarationStatus.PENDING);
        assertEquals(1, pending.size());
        assertEquals(DeclarationStatus.PENDING, pending.get(0).getStatus());
    }

    @Test
    void ensureGetDeclarationsByStatusReturnsValidatedOnly() {
        DeclarationRepository repo = new DeclarationRepository();
        Declaration d1 = createTestDeclaration(DeclarationType.INITIAL);
        Declaration d2 = createTestDeclaration(DeclarationType.REGULAR);
        d1.setStatus(DeclarationStatus.VALIDATED);
        d2.setStatus(DeclarationStatus.REJECTED);
        repo.save(d1);
        repo.save(d2);

        List<Declaration> validated = repo.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        assertEquals(1, validated.size());
        assertEquals(DeclarationStatus.VALIDATED, validated.get(0).getStatus());
    }

    @Test
    void ensureGetDeclarationsByStatusReturnsEmptyIfNoneMatch() {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createTestDeclaration(DeclarationType.INITIAL));

        List<Declaration> validated = repo.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        assertTrue(validated.isEmpty());
    }

    @Test
    void ensureGetDeclarationsByStatusReturnsDefensiveCopy() {
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createTestDeclaration(DeclarationType.INITIAL));
        assertNotSame(
                repo.getDeclarationsByStatus(DeclarationStatus.PENDING),
                repo.getDeclarationsByStatus(DeclarationStatus.PENDING));
    }
}
