package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PoliticalAgentRepositoryTest {

    private PoliticalAgent createAgent(String email, String tin) {
        return new PoliticalAgent("Agent " + tin, email, "12345678", tin, new Date(), null);
    }

    @Test
    void ensureSavePoliticalAgentWorks() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");

        assertTrue(repo.save(agent));
    }

    @Test
    void ensureSaveDuplicateAgentFails() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        repo.save(agent);

        PoliticalAgent duplicate = createAgent("other@gov.pt", "111111111");

        assertFalse(repo.save(duplicate));
    }

    @Test
    void ensureGetAllReturnsAllSavedAgents() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        repo.save(createAgent("a@gov.pt", "111111111"));
        repo.save(createAgent("b@gov.pt", "222222222"));

        List<PoliticalAgent> all = repo.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void ensureGetAllReturnsEmptyListForEmptyRepository() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();

        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void ensureGetAllReturnsDefensiveCopy() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        repo.save(createAgent("agent@gov.pt", "111111111"));

        List<PoliticalAgent> first = repo.getAll();
        List<PoliticalAgent> second = repo.getAll();

        assertNotSame(first, second);
    }

    @Test
    void ensureGetByEmailReturnsTheAgent() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        repo.save(agent);

        PoliticalAgent found = repo.getByEmail("agent@gov.pt");

        assertNotNull(found);
        assertEquals(agent, found);
    }

    @Test
    void ensureGetByEmailIsCaseInsensitive() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        repo.save(createAgent("agent@gov.pt", "111111111"));

        PoliticalAgent found = repo.getByEmail("AGENT@GOV.PT");

        assertNotNull(found);
    }

    @Test
    void ensureGetByEmailReturnsNullWhenNotFound() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        repo.save(createAgent("agent@gov.pt", "111111111"));

        PoliticalAgent found = repo.getByEmail("unknown@gov.pt");

        assertNull(found);
    }

    @Test
    void ensureGetByEmailReturnsNullForEmptyRepository() {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();

        assertNull(repo.getByEmail("agent@gov.pt"));
    }
}
