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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalyseIncomeEvolutionControllerTest {

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

        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(agentRepo, new DeclarationRepository());

        assertEquals(2, controller.getPoliticalAgents().size());
    }

    @Test
    void ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered() {
        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertTrue(controller.getPoliticalAgents().isEmpty());
    }

    @Test
    void ensureOnlyValidatedDeclarationsOfTheRequestedAgentAreReturned() {
        PoliticalAgent joao = agentJoao();
        PoliticalAgent maria = agentMaria();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration joaoValidated = validatedDeclaration(joao, date(2024, Calendar.MARCH, 1));
        Declaration mariaValidated = validatedDeclaration(maria, date(2024, Calendar.MARCH, 1));
        Declaration joaoPending = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));

        declRepo.save(joaoValidated);
        declRepo.save(mariaValidated);
        declRepo.save(joaoPending);

        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIncomeEvolution(joao,
                date(2024, Calendar.JANUARY, 1), date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertSame(joaoValidated, result.get(0));
    }

    @Test
    void ensureOnlyDeclarationsInsideThePeriodAreReturned() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration before = validatedDeclaration(joao, date(2024, Calendar.JANUARY, 15));
        Declaration insideStart = validatedDeclaration(joao, date(2024, Calendar.MARCH, 1));
        Declaration insideMid = validatedDeclaration(joao, date(2024, Calendar.MAY, 10));
        Declaration onEndDate = validatedDeclaration(joao, date(2024, Calendar.JUNE, 30));
        Declaration after = validatedDeclaration(joao, date(2024, Calendar.JULY, 5));

        declRepo.save(before);
        declRepo.save(insideStart);
        declRepo.save(insideMid);
        declRepo.save(onEndDate);
        declRepo.save(after);

        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIncomeEvolution(joao,
                date(2024, Calendar.MARCH, 1), date(2024, Calendar.JUNE, 30));

        assertEquals(3, result.size());
    }

    @Test
    void ensureResultIsOrderedChronologicallyBySubmissionDate() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        // saved out of order on purpose
        Declaration mid = validatedDeclaration(joao, date(2024, Calendar.MAY, 10));
        Declaration last = validatedDeclaration(joao, date(2024, Calendar.JUNE, 30));
        Declaration first = validatedDeclaration(joao, date(2024, Calendar.MARCH, 1));

        declRepo.save(mid);
        declRepo.save(last);
        declRepo.save(first);

        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIncomeEvolution(joao,
                date(2024, Calendar.JANUARY, 1), date(2024, Calendar.DECEMBER, 31));

        assertEquals(3, result.size());
        assertSame(first, result.get(0));
        assertSame(mid, result.get(1));
        assertSame(last, result.get(2));
    }

    @Test
    void ensureEmptyResultWhenNoValidatedDeclarationsInPeriod() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclaration(joao, date(2025, Calendar.JANUARY, 1)));

        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), declRepo);

        List<Declaration> result = controller.getIncomeEvolution(joao,
                date(2024, Calendar.JANUARY, 1), date(2024, Calendar.DECEMBER, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureStartAfterEndIsRejected() {
        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class,
                () -> controller.getIncomeEvolution(agentJoao(),
                        date(2024, Calendar.JUNE, 30), date(2024, Calendar.JANUARY, 1)));
    }

    @Test
    void ensureNullAgentIsRejected() {
        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class,
                () -> controller.getIncomeEvolution(null, new Date(), new Date()));
    }

    @Test
    void ensureNullDatesAreRejected() {
        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class,
                () -> controller.getIncomeEvolution(agentJoao(), null, new Date()));
        assertThrows(IllegalArgumentException.class,
                () -> controller.getIncomeEvolution(agentJoao(), new Date(), null));
    }

    @Test
    void ensureControllerCanBeCreatedWithDefaultConstructor() {
        AnalyseIncomeEvolutionController controller =
                new AnalyseIncomeEvolutionController();

        assertNotNull(controller);
        assertNotNull(controller.getPoliticalAgents());
    }
}
