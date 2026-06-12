package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class ValidationRecordRepositoryTest {

    private static final Date NOW = new Date();

    private ValidationRecord createRecord(ValidationOutcome outcome) {
        EthicsCommitteeMember member = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        PoliticalAgent agent = new PoliticalAgent("Agent", "agent@gov.pt",
                "12345678", "123456789", NOW, null);
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, NOW);
        d.addPositionEntry(
                new Organization("TechCorp", OrganizationNature.PRIVATE, OrganizationType.COMPANY),
                "Director", PositionNature.PUBLIC,
                50000, 0, 0, NOW, null);
        return new ValidationRecord(member, d, NOW, outcome);
    }

    @Test
    void ensureSaveWorks() {
        ValidationRecordRepository repo = new ValidationRecordRepository();
        assertTrue(repo.save(createRecord(ValidationOutcome.VALIDATED)));
    }

    @Test
    void ensureSaveNullFails() {
        ValidationRecordRepository repo = new ValidationRecordRepository();
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                repo.save(null);
            }
        });
    }

    @Test
    void ensureGetAllReturnsAllSaved() {
        ValidationRecordRepository repo = new ValidationRecordRepository();
        repo.save(createRecord(ValidationOutcome.VALIDATED));
        repo.save(createRecord(ValidationOutcome.RETURNED_FOR_CORRECTION));
        assertEquals(2, repo.getAll().size());
    }

    @Test
    void ensureEmptyRepositoryReturnsEmptyList() {
        ValidationRecordRepository repo = new ValidationRecordRepository();
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void ensureGetAllReturnsDefensiveCopy() {
        ValidationRecordRepository repo = new ValidationRecordRepository();
        repo.save(createRecord(ValidationOutcome.VALIDATED));
        assertNotSame(repo.getAll(), repo.getAll());
    }
}
