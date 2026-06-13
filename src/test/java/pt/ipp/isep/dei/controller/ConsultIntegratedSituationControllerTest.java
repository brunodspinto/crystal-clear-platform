package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PositionNature;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.mapper.PoliticalAgentMapper;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;
class ConsultIntegratedSituationControllerTest {

    private PoliticalAgent agentJoao() {
        return new PoliticalAgent("João Silva", "joao@gov.pt", "11111111", "100000001",
                date(2020, Calendar.JANUARY, 1), null);
    }

    private PoliticalAgent agentMaria() {
        return new PoliticalAgent("Maria Costa", "maria@gov.pt", "22222222", "100000002",
                date(2020, Calendar.JANUARY, 1), null);
    }

    private PoliticalAgentDTO dto(PoliticalAgent agent) {
        return new PoliticalAgentMapper().toDTO(agent);
    }

    private PoliticalAgentRepository repoWith(PoliticalAgent... agents) {
        PoliticalAgentRepository repo = new PoliticalAgentRepository();
        for (PoliticalAgent a : agents) {
            repo.save(a);
        }
        return repo;
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
                new ConsultIntegratedSituationController(repoWith(joao, maria), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertEquals(joaoValidated.getId(), result.get(0).getId());
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
                new ConsultIntegratedSituationController(repoWith(joao), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.JUNE, 30));

        assertEquals(2, result.size());
    }

    @Test
    void ensureIntegratedSituationReturnsEmptyWhenNoMatchingDeclarations() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclaration(joao, date(2025, Calendar.JANUARY, 1)));

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(repoWith(joao), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.JANUARY, 1));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureNullAgentIsRejected() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.getIntegratedSituation(null, new Date());
            }
        });
    }

    @Test
    void ensureNullReferenceDateIsRejected() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(new PoliticalAgentRepository(), new DeclarationRepository());

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.getIntegratedSituation(dto(agentJoao()), null);
            }
        });
    }

    @Test
    void ensureRejectedDeclarationsAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration rejected = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        rejected.setStatus(DeclarationStatus.REJECTED);
        declRepo.save(rejected);

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(repoWith(joao), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureValidatedDeclarationsOfAllTypesAreReturned() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration initial = new Declaration(DeclarationType.INITIAL, joao, date(2024, Calendar.JANUARY, 10));
        initial.setStatus(DeclarationStatus.VALIDATED);
        Declaration regular = validatedDeclaration(joao, date(2024, Calendar.MAY, 10));
        Declaration exceptional = new Declaration(DeclarationType.EXCEPTIONAL, joao, date(2024, Calendar.JULY, 10),
                "DECL-1", "Correcting an omission");
        exceptional.setStatus(DeclarationStatus.VALIDATED);

        declRepo.save(initial);
        declRepo.save(regular);
        declRepo.save(exceptional);

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(repoWith(joao), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(3, result.size());
    }

    @Test
    void ensureControllerCanBeCreatedWithDefaultConstructor() {
        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController();

        assertNotNull(controller);
        assertNotNull(controller.getPoliticalAgents());
    }

    @Test
    void ensureReturnedDeclarationsExposeTheirEntries() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration d = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        Organization org = new Organization("Acme Lda", OrganizationNature.PRIVATE,
                OrganizationType.COMPANY);
        d.addPositionEntry(org, "Director", PositionNature.PUBLIC,
                50000.0, 0.0, 0.0,
                date(2023, Calendar.JANUARY, 1), null);
        d.addIncome(org, 1500.0, "Royalties", date(2024, Calendar.FEBRUARY, 1));
        d.addSubsidyEntry(org, 200.0, "Travel subsidy", date(2024, Calendar.FEBRUARY, 15));
        d.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(d);

        ConsultIntegratedSituationController controller =
                new ConsultIntegratedSituationController(repoWith(joao), declRepo);

        List<DeclarationDTO> result = controller.getIntegratedSituation(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPositions().size());
        // incomes now include the position salary as income plus the standalone
        // Income entry, so a declaration with 1 position + 1 income exposes 2
        assertEquals(2, result.get(0).getIncomes().size());
        assertEquals(1, result.get(0).getSubsidies().size());
    }
}
