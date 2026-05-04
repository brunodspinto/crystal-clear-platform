package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultIntegratedSituationControllerTest {

    private PoliticalAgent agentJoao() {
        return new PoliticalAgent("João Silva", "joao@gov.pt", "11111111", "100000001",
                date(2020, Calendar.JANUARY, 1), null);
    }

    private PoliticalAgent agentMaria() {
        return new PoliticalAgent("Maria Costa", "maria@gov.pt", "22222222", "100000002",
                date(2020, Calendar.JANUARY, 1), null);
    }

    private Date date(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }

    private Declaration validatedDeclaration(PoliticalAgent agent, Date submissionDate) {
        Declaration d = new Declaration(DeclarationType.REGULAR, agent, submissionDate);
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    @Test
    void ensureGetPoliticalAgentsReturnsAll() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(agentJoao());
        agentRepo.save(agentMaria());

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(agentRepo, new DeclarationRepository());

        assertEquals(2, controller.getPoliticalAgents().size());
    }

    @Test
    void ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertTrue(controller.getPoliticalAgents().isEmpty());
    }

    @Test
    void ensureIntegratedSituationOnlyIncludesValidatedDeclarationsOfRequestedAgent() {
        PoliticalAgent joao = agentJoao();
        PoliticalAgent maria = agentMaria();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration joaoValidated = validatedDeclaration(joao, date(2024, Calendar.MARCH, 1));
        Declaration mariaValidated = validatedDeclaration(maria, date(2024, Calendar.MARCH, 1));
        Declaration joaoPending = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));

        declRepo.save(joaoValidated);
        declRepo.save(mariaValidated);
        declRepo.save(joaoPending);

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIntegratedSituation(joao, date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertSame(joaoValidated, result.get(0));
    }

    @Test
    void ensureIntegratedSituationOnlyIncludesDeclarationsUpToReferenceDate() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration before = validatedDeclaration(joao, date(2024, Calendar.JANUARY, 15));
        Declaration onTheDate = validatedDeclaration(joao, date(2024, Calendar.JUNE, 30));
        Declaration after = validatedDeclaration(joao, date(2024, Calendar.JULY, 1));

        declRepo.save(before);
        declRepo.save(onTheDate);
        declRepo.save(after);

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIntegratedSituation(joao, date(2024, Calendar.JUNE, 30));

        assertEquals(2, result.size());
    }

    @Test
    void ensureIntegratedSituationReturnsEmptyWhenNoMatchingDeclarations() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclaration(joao, date(2025, Calendar.JANUARY, 1)));

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIntegratedSituation(joao, date(2024, Calendar.JANUARY, 1));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureNullAgentIsRejected() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class,
                () -> controller.getIntegratedSituation(null, new Date()));
    }

    @Test
    void ensureNullReferenceDateIsRejected() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class,
                () -> controller.getIntegratedSituation(agentJoao(), null));
    }
}
