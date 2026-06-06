package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationRecordTest {

    private static final Date NOW = new Date();

    private EthicsCommitteeMember createMember() {
        return new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
    }

    private Declaration createDeclaration() {
        PoliticalAgent agent = new PoliticalAgent("Agent", "agent@gov.pt",
                "12345678", "123456789", NOW, null);
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, NOW);
        d.addPositionEntry(
                new Organization("TechCorp", OrganizationNature.PRIVATE, OrganizationType.COMPANY),
                "Director", PositionNature.PUBLIC,
                50000, 0, 0, NOW, null);
        return d;
    }

    @Test
    void ensureCreationWorks() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.VALIDATED);
        assertNotNull(vr);
    }

    @Test
    void ensureCreationFailsWithNullMember() {
        assertThrows(IllegalArgumentException.class, () ->
                new ValidationRecord(null, createDeclaration(), NOW, ValidationOutcome.VALIDATED));
    }

    @Test
    void ensureCreationFailsWithNullDeclaration() {
        assertThrows(IllegalArgumentException.class, () ->
                new ValidationRecord(createMember(), null, NOW, ValidationOutcome.VALIDATED));
    }

    @Test
    void ensureCreationFailsWithNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new ValidationRecord(createMember(), createDeclaration(), null, ValidationOutcome.VALIDATED));
    }

    @Test
    void ensureCreationFailsWithNullOutcome() {
        assertThrows(IllegalArgumentException.class, () ->
                new ValidationRecord(createMember(), createDeclaration(), NOW, null));
    }

    @Test
    void ensureCommentsListStartsEmpty() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.VALIDATED);
        assertTrue(vr.getComments().isEmpty());
    }

    @Test
    void ensureAddCommentWorks() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.RETURNED_FOR_CORRECTION);
        vr.addComment("Assets", "Missing vehicle description.");
        assertEquals(1, vr.getComments().size());
    }

    @Test
    void ensureMultipleCommentsCanBeAdded() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.RETURNED_FOR_CORRECTION);
        vr.addComment("Assets", "Missing vehicle description.");
        vr.addComment("Positions", "Incorrect start date.");
        vr.addComment("Subsidies", "Amount does not match.");
        assertEquals(3, vr.getComments().size());
    }

    @Test
    void ensureAddCommentFailsWithNullSection() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.RETURNED_FOR_CORRECTION);
        assertThrows(IllegalArgumentException.class, () ->
                vr.addComment(null, "Some comment."));
    }

    @Test
    void ensureAddCommentFailsWithBlankComment() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.RETURNED_FOR_CORRECTION);
        assertThrows(IllegalArgumentException.class, () ->
                vr.addComment("Assets", "   "));
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        EthicsCommitteeMember member = createMember();
        Declaration declaration = createDeclaration();
        ValidationRecord vr = new ValidationRecord(member, declaration, NOW, ValidationOutcome.VALIDATED);
        assertEquals(member, vr.getMember());
        assertEquals(declaration, vr.getDeclaration());
        assertEquals(NOW, vr.getValidationDate());
        assertEquals(ValidationOutcome.VALIDATED, vr.getOutcome());
    }

    @Test
    void ensureGetCommentsReturnsDefensiveCopy() {
        ValidationRecord vr = new ValidationRecord(createMember(), createDeclaration(),
                NOW, ValidationOutcome.RETURNED_FOR_CORRECTION);
        vr.addComment("Assets", "Missing data.");
        List<ValidationComment> c1 = vr.getComments();
        List<ValidationComment> c2 = vr.getComments();
        assertNotSame(c1, c2);
    }
}
