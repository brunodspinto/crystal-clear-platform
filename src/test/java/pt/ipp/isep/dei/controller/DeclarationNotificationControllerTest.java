package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for DeclarationNotificationController: counting the declarations of a
 * political agent that were returned for correction (status REJECTED).
 */
class DeclarationNotificationControllerTest {

    private DeclarationRepository declRepo;
    private PoliticalAgentRepository agentRepo;
    private PoliticalAgent agent;
    private static final Date NOW = new Date();

    @BeforeEach
    void setUp() {
        declRepo = new DeclarationRepository();
        agentRepo = new PoliticalAgentRepository();
        agent = new PoliticalAgent("Ana Costa", "ana.notif@gov.pt",
                "11111111", "111111111", NOW, null);
        agentRepo.save(agent);
    }

    private DeclarationNotificationController ctrl() {
        return new DeclarationNotificationController(declRepo, agentRepo);
    }

    private Declaration declarationWithStatus(DeclarationStatus status) {
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, NOW);
        d.setStatus(status);
        declRepo.save(d);
        return d;
    }

    @Test
    void ensureCountIsZeroWhenAgentHasNoRejectedDeclarations() {
        declarationWithStatus(DeclarationStatus.PENDING);
        declarationWithStatus(DeclarationStatus.VALIDATED);
        assertEquals(0, ctrl().countReturnedForCorrection("ana.notif@gov.pt"));
    }

    @Test
    void ensureCountReflectsRejectedDeclarations() {
        declarationWithStatus(DeclarationStatus.REJECTED);
        declarationWithStatus(DeclarationStatus.REJECTED);
        declarationWithStatus(DeclarationStatus.VALIDATED);
        assertEquals(2, ctrl().countReturnedForCorrection("ana.notif@gov.pt"));
    }

    @Test
    void ensureCountIsZeroForUnknownEmail() {
        declarationWithStatus(DeclarationStatus.REJECTED);
        assertEquals(0, ctrl().countReturnedForCorrection("unknown@gov.pt"));
    }

    @Test
    void ensureCountDoesNotIncludeOtherAgentsRejectedDeclarations() {
        PoliticalAgent other = new PoliticalAgent("Bruno Lima", "bruno.notif@gov.pt",
                "22222222", "222222222", NOW, null);
        agentRepo.save(other);
        Declaration d = new Declaration(DeclarationType.INITIAL, other, NOW);
        d.setStatus(DeclarationStatus.REJECTED);
        declRepo.save(d);

        assertEquals(0, ctrl().countReturnedForCorrection("ana.notif@gov.pt"));
    }
}
