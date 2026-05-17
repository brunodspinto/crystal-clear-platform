package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;

import java.util.Calendar;
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


    //Additional tests
    private DeclarationRepository repo;
    private PoliticalAgent agentA;
    private PoliticalAgent agentB;
    private Organization org;

    private static final Date JAN = date(2023, Calendar.JANUARY,  1);
    private static final Date MAR = date(2023, Calendar.MARCH,    1);
    private static final Date JUN = date(2023, Calendar.JUNE,     1);
    private static final Date SEP = date(2023, Calendar.SEPTEMBER,1);
    private static final Date DEC = date(2023, Calendar.DECEMBER, 1);

    @BeforeEach
    void setUp() {
        repo   = new DeclarationRepository();
        org    = new Organization("Parlamento", "public", OrganizationType.POLITICAL_PARTY);
        agentA = new PoliticalAgent("Ana Costa", "ana@gov.pt",
                "11111111", "111111111", JAN, null);
        agentB = new PoliticalAgent("Rui Pinto", "rui@gov.pt",
                "22222222", "222222222", JAN, null);
    }

    /** Creates and saves a validated declaration for the given agent at the given date. */
    private Declaration saveValidated(PoliticalAgent agent, Date submissionDate) {
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, submissionDate);
        d.addPositionEntry(org, "Deputy", PositionNature.PUBLIC,
                50000, 0, 0, JAN, null);
        d.setStatus(DeclarationStatus.VALIDATED);
        repo.save(d);
        return d;
    }

    /** Creates and saves a PENDING declaration for the given agent at the given date. */
    private Declaration savePending(PoliticalAgent agent, Date submissionDate) {
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, submissionDate);
        d.addPositionEntry(org, "Deputy", PositionNature.PUBLIC,
                50000, 0, 0, JAN, null);
        repo.save(d);
        return d;
    }

    // =========================================================================
    // getValidatedDeclarationsForAgentUpTo
    // =========================================================================

    @Test
    void ensureUpToReturnsDeclarationOnExactDate() {
        saveValidated(agentA, MAR);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, MAR);
        assertEquals(1, result.size());
    }

    @Test
    void ensureUpToReturnsDeclarationBeforeDate() {
        saveValidated(agentA, JAN);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, MAR);
        assertEquals(1, result.size());
    }

    @Test
    void ensureUpToExcludesDeclarationAfterDate() {
        saveValidated(agentA, DEC);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, MAR);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureUpToExcludesPendingDeclarations() {
        savePending(agentA, JAN);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, DEC);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureUpToExcludesOtherAgent() {
        saveValidated(agentB, JAN);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, DEC);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureUpToReturnsMultipleDeclarations() {
        saveValidated(agentA, JAN);
        saveValidated(agentA, MAR);
        saveValidated(agentA, JUN);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentUpTo(agentA, JUN);
        assertEquals(3, result.size());
    }

    @Test
    void ensureUpToNullAgentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentUpTo(null, MAR));
    }

    @Test
    void ensureUpToNullDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentUpTo(agentA, null));
    }

    // =========================================================================
    // getValidatedDeclarationsForAgentBetween
    // =========================================================================

    @Test
    void ensureBetweenReturnsDeclarationOnStartDate() {
        saveValidated(agentA, MAR);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, MAR, DEC);
        assertEquals(1, result.size());
    }

    @Test
    void ensureBetweenReturnsDeclarationOnEndDate() {
        saveValidated(agentA, DEC);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, MAR, DEC);
        assertEquals(1, result.size());
    }

    @Test
    void ensureBetweenExcludesDeclarationBeforeStartDate() {
        saveValidated(agentA, JAN);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, MAR, DEC);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureBetweenExcludesDeclarationAfterEndDate() {
        saveValidated(agentA, DEC);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, JAN, JUN);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureBetweenExcludesPendingDeclarations() {
        savePending(agentA, MAR);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, JAN, DEC);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureBetweenExcludesOtherAgent() {
        saveValidated(agentB, MAR);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, JAN, DEC);
        assertTrue(result.isEmpty());
    }

    @Test
    void ensureBetweenReturnsResultsInChronologicalOrder() {
        saveValidated(agentA, SEP);
        saveValidated(agentA, JAN);
        saveValidated(agentA, MAR);

        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, JAN, DEC);
        assertEquals(3, result.size());
        assertTrue(result.get(0).getSubmissionDate().compareTo(result.get(1).getSubmissionDate()) <= 0);
        assertTrue(result.get(1).getSubmissionDate().compareTo(result.get(2).getSubmissionDate()) <= 0);
    }

    @Test
    void ensureBetweenSameDateBoundaryWorks() {
        saveValidated(agentA, MAR);
        List<Declaration> result = repo.getValidatedDeclarationsForAgentBetween(agentA, MAR, MAR);
        assertEquals(1, result.size());
    }

    @Test
    void ensureBetweenNullAgentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentBetween(null, JAN, DEC));
    }

    @Test
    void ensureBetweenNullStartDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentBetween(agentA, null, DEC));
    }

    @Test
    void ensureBetweenNullEndDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentBetween(agentA, JAN, null));
    }

    @Test
    void ensureBetweenStartAfterEndThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                repo.getValidatedDeclarationsForAgentBetween(agentA, DEC, JAN));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Date date(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }
}
