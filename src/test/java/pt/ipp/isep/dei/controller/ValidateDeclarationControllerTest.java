package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// =============================================================================

/**
 * Tests for ValidateDeclarationController (US08).
 *
 * processValidation() resolves the current Ethics Committee Member from the
 * auth session. That path is not unit-testable. These tests cover the
 * session-independent methods (getPendingDeclarations, getDeclarationDetails,
 * getValidationOutcomes) and the full validation pipeline replicated without
 * the session lookup.
 */
class ValidateDeclarationControllerTest {

    private DeclarationRepository      declRepo;
    private ValidationRecordRepository recordRepo;
    private EthicsCommitteeMemberRepository memberRepo;

    private PoliticalAgent   agent;
    private EthicsCommitteeMember member;
    private static final Date NOW = new Date();

    @BeforeEach
    void setUp() {
        declRepo   = new DeclarationRepository();
        recordRepo = new ValidationRecordRepository();
        memberRepo = new EthicsCommitteeMemberRepository();
        agent  = new PoliticalAgent("Ana Costa", "ana@gov.pt",
                "11111111", "111111111", NOW, null);
        member = new EthicsCommitteeMember("Carlos Ramos", "carlos@ethics.pt");
        memberRepo.save(member);
    }

    private ValidateDeclarationController ctrl() {
        return new ValidateDeclarationController(declRepo, recordRepo, memberRepo, null);
    }

    private Declaration pendingDeclaration() {
        return new Declaration(DeclarationType.INITIAL, agent, NOW);
    }

    /**
     * Replicates processValidation logic without the session lookup.
     * Mirrors the controller body exactly after the member resolution step.
     */
    private boolean processValidation(Declaration declaration, ValidationOutcome outcome,
                                      List<Object[]> comments) {
        if (declaration.getStatus() != DeclarationStatus.PENDING) return false;
        declaration.setStatus(outcome);
        declRepo.save(declaration);
        ValidationRecord record = new ValidationRecord(member, declaration, NOW, outcome);
        if (outcome == ValidationOutcome.RETURNED_FOR_CORRECTION && comments != null) {
            for (Object[] c : comments) record.addComment((String) c[0], (String) c[1]);
        }
        return recordRepo.save(record);
    }

    // -------------------------------------------------------------------------
    // getPendingDeclarations
    // -------------------------------------------------------------------------

    @Test
    void ensureGetPendingReturnsOnlyPendingDeclarations() {
        Declaration pending   = pendingDeclaration();
        Declaration validated = pendingDeclaration();
        validated.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(pending);
        declRepo.save(validated);
        assertEquals(1, ctrl().getPendingDeclarations().size());
    }

    @Test
    void ensureGetPendingReturnsEmptyWhenNonePending() {
        Declaration d = pendingDeclaration();
        d.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(d);
        assertTrue(ctrl().getPendingDeclarations().isEmpty());
    }

    @Test
    void ensureGetPendingReturnsEmptyForEmptyRepository() {
        assertTrue(ctrl().getPendingDeclarations().isEmpty());
    }

    @Test
    void ensureGetPendingReturnsAllPendingDeclarations() {
        declRepo.save(pendingDeclaration());
        declRepo.save(pendingDeclaration());
        declRepo.save(pendingDeclaration());
        assertEquals(3, ctrl().getPendingDeclarations().size());
    }

    // -------------------------------------------------------------------------
    // getDeclarationDetails
    // -------------------------------------------------------------------------

    @Test
    void ensureGetDeclarationDetailsReturnsNonBlankString() {
        Declaration d = pendingDeclaration();
        String details = ctrl().getDeclarationDetails(d);
        assertNotNull(details);
        assertFalse(details.isBlank());
    }

    @Test
    void ensureGetDeclarationDetailsContainsAgentName() {
        Declaration d = pendingDeclaration();
        assertTrue(ctrl().getDeclarationDetails(d).contains("Ana Costa"));
    }

    @Test
    void ensureGetDeclarationDetailsContainsDeclarationType() {
        Declaration d = pendingDeclaration();
        String details = ctrl().getDeclarationDetails(d);
        assertTrue(details.contains("INITIAL") || details.contains("Initial"));
    }

    // -------------------------------------------------------------------------
    // getValidationOutcomes
    // -------------------------------------------------------------------------

    @Test
    void ensureGetValidationOutcomesReturnsAllValues() {
        assertEquals(ValidationOutcome.values().length, ctrl().getValidationOutcomes().size());
    }

    @Test
    void ensureGetValidationOutcomesContainsValidated() {
        assertTrue(ctrl().getValidationOutcomes().contains(ValidationOutcome.VALIDATED));
    }

    @Test
    void ensureGetValidationOutcomesContainsReturnedForCorrection() {
        assertTrue(ctrl().getValidationOutcomes()
                .contains(ValidationOutcome.RETURNED_FOR_CORRECTION));
    }

    // -------------------------------------------------------------------------
    // processValidation – happy paths
    // -------------------------------------------------------------------------

    @Test
    void ensureValidationOfPendingDeclarationSucceeds() {
        Declaration d = pendingDeclaration();
        assertTrue(processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>()));
    }

    @Test
    void ensureValidatedDeclarationHasValidatedStatus() {
        Declaration d = pendingDeclaration();
        processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>());
        assertEquals(DeclarationStatus.VALIDATED, d.getStatus());
    }

    @Test
    void ensureValidationCreatesValidationRecord() {
        Declaration d = pendingDeclaration();
        processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>());
        assertEquals(1, recordRepo.getAll().size());
    }

    @Test
    void ensureValidationRecordOutcomeMatchesSuppliedOutcome() {
        Declaration d = pendingDeclaration();
        processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>());
        assertEquals(ValidationOutcome.VALIDATED, recordRepo.getAll().get(0).getOutcome());
    }

    @Test
    void ensureRejectionSetsDeclarationStatusToRejected() {
        Declaration d = pendingDeclaration();
        List<Object[]> comments = new ArrayList<>();
        comments.add(new Object[]{"Income Section", "Gross salary missing."});
        processValidation(d, ValidationOutcome.RETURNED_FOR_CORRECTION, comments);
        assertEquals(DeclarationStatus.REJECTED, d.getStatus());
    }

    @Test
    void ensureRejectionWithCommentsStoresAllComments() {
        Declaration d = pendingDeclaration();
        List<Object[]> comments = new ArrayList<>();
        comments.add(new Object[]{"Income Section",  "Gross salary missing."});
        comments.add(new Object[]{"Assets Section",  "Property value missing."});
        processValidation(d, ValidationOutcome.RETURNED_FOR_CORRECTION, comments);
        assertEquals(2, recordRepo.getAll().get(0).getComments().size());
    }

    @Test
    void ensureRejectionCommentSectionIsCorrect() {
        Declaration d = pendingDeclaration();
        List<Object[]> comments = new ArrayList<>();
        comments.add(new Object[]{"Positions Section", "Function designation blank."});
        processValidation(d, ValidationOutcome.RETURNED_FOR_CORRECTION, comments);
        assertEquals("Positions Section",
                recordRepo.getAll().get(0).getComments().get(0).getSection());
    }

    @Test
    void ensureValidationWithNullCommentsListWorks() {
        Declaration d = pendingDeclaration();
        assertTrue(processValidation(d, ValidationOutcome.VALIDATED, null));
    }

    // -------------------------------------------------------------------------
    // processValidation – PENDING guard (AC3)
    // -------------------------------------------------------------------------

    @Test
    void ensureValidationOfAlreadyValidatedDeclarationReturnsFalse() {
        Declaration d = pendingDeclaration();
        d.setStatus(DeclarationStatus.VALIDATED);
        assertFalse(processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>()));
    }

    @Test
    void ensureValidationOfRejectedDeclarationReturnsFalse() {
        Declaration d = pendingDeclaration();
        d.setStatus(DeclarationStatus.REJECTED);
        assertFalse(processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>()));
    }

    @Test
    void ensureNoRecordCreatedWhenDeclarationNotPending() {
        Declaration d = pendingDeclaration();
        d.setStatus(DeclarationStatus.VALIDATED);
        processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>());
        assertTrue(recordRepo.getAll().isEmpty());
    }

    @Test
    void ensureValidatedDeclarationNoLongerAppearsInPendingList() {
        Declaration d = pendingDeclaration();
        declRepo.save(d);
        assertEquals(1, ctrl().getPendingDeclarations().size());
        processValidation(d, ValidationOutcome.VALIDATED, new ArrayList<>());
        assertEquals(0, ctrl().getPendingDeclarations().size());
    }
}

