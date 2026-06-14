package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.AssetType;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.RealEstate;
import pt.ipp.isep.dei.domain.StockAsset;
import pt.ipp.isep.dei.domain.VehicleAsset;
import pt.ipp.isep.dei.dto.AssetEntryDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.mapper.PoliticalAgentMapper;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;
class ConsultAssetsControllerTest {

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

    private Declaration validatedDeclarationWithRealEstate(PoliticalAgent agent, Date submissionDate,
                                                            double value) {
        Declaration d = new Declaration(DeclarationType.REGULAR, agent, submissionDate);
        d.addAssetEntry(AssetType.REAL_ESTATE, value, new RealEstate("House", "Porto"));
        d.setStatus(DeclarationStatus.VALIDATED);
        return d;
    }

    @Test
    void ensureGetPoliticalAgentsReturnsAll() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(agentJoao());
        agentRepo.save(agentMaria());

        ConsultAssetsController controller =
                new ConsultAssetsController(agentRepo, new DeclarationRepository(), null);

        assertEquals(2, controller.getPoliticalAgents().size());
    }

    @Test
    void ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertTrue(controller.getPoliticalAgents().isEmpty());
    }

    @Test
    void ensureAgentsWithDataAtDateAreReturned() {
        PoliticalAgent joao = agentJoao();
        PoliticalAgent maria = agentMaria();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2023, Calendar.MARCH, 1), 100000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao, maria), declRepo, null);

        List<PoliticalAgentDTO> result =
                controller.getPoliticalAgentsWithDataAt(date(2024, Calendar.JANUARY, 1));

        assertEquals(1, result.size());
        assertEquals("joao@gov.pt", result.get(0).getEmail());
    }

    @Test
    void ensureAgentsWithOnlyLaterDeclarationsAreFilteredOut() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2024, Calendar.JUNE, 15), 100000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        assertTrue(controller.getPoliticalAgentsWithDataAt(date(2023, Calendar.JANUARY, 1)).isEmpty());
    }

    @Test
    void ensureAgentWithDeclarationOnTheReferenceDateIsIncluded() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2024, Calendar.JUNE, 15), 100000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        assertEquals(1, controller.getPoliticalAgentsWithDataAt(date(2024, Calendar.JUNE, 15)).size());
    }

    @Test
    void ensureAgentsWithDataAtNullDateIsRejected() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.getPoliticalAgentsWithDataAt(null);
            }
        });
    }

    @Test
    void ensureAssetsFromAllValidatedDeclarationsUpToDateAreReturned() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2023, Calendar.MARCH, 1), 100000.0));
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2024, Calendar.JUNE, 15), 150000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(2, result.size());
    }

    @Test
    void ensureDeclarationsAfterReferenceDateAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2023, Calendar.MARCH, 1), 100000.0));
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2025, Calendar.MARCH, 1), 200000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertEquals(100000.0, result.get(0).getValue());
    }

    @Test
    void ensureNonValidatedDeclarationsAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration pending = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        pending.addAssetEntry(AssetType.VEHICLES, 25000.0, new VehicleAsset("Car"));
        declRepo.save(pending);

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureEmptyResultWhenNoDeclarationsExistForAgent() {
        PoliticalAgent joao = agentJoao();
        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), new DeclarationRepository(), null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), new Date());

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureNullAgentIsRejected() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.getAssetsAt(null, new Date());
            }
        });
    }

    @Test
    void ensureNullDateIsRejected() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.getAssetsAt(dto(agentJoao()), null);
            }
        });
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsFalseWhenNoAuthRepository() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertFalse(controller.isCurrentUserJournalist());
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsTrueWhenJournalistLoggedIn() {
        AuthenticationRepository authRepo = new AuthenticationRepository();
        authRepo.addUserRole(AuthenticationController.ROLE_JOURNALIST,
                AuthenticationController.ROLE_JOURNALIST);
        authRepo.addUserWithRole("J. Reporter", "journ@news.pt", "JRN11jr",
                AuthenticationController.ROLE_JOURNALIST);
        authRepo.doLogin("journ@news.pt", "JRN11jr");

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), authRepo);

        assertTrue(controller.isCurrentUserJournalist());
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsFalseWhenCitizenLoggedIn() {
        AuthenticationRepository authRepo = new AuthenticationRepository();
        authRepo.addUserRole(AuthenticationController.ROLE_CITIZEN,
                AuthenticationController.ROLE_CITIZEN);
        authRepo.addUserWithRole("C. Public", "cit@mail.pt", "CIT11ci",
                AuthenticationController.ROLE_CITIZEN);
        authRepo.doLogin("cit@mail.pt", "CIT11ci");

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), authRepo);

        assertFalse(controller.isCurrentUserJournalist());
    }

    @Test
    void ensureRejectedDeclarationsAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration rejected = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        rejected.addAssetEntry(AssetType.REAL_ESTATE, 50000.0, new RealEstate("Flat", "Lisboa"));
        rejected.setStatus(DeclarationStatus.REJECTED);
        declRepo.save(rejected);

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureAllAssetTypesAreReturnedFromSameDeclaration() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration d = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        d.addAssetEntry(AssetType.REAL_ESTATE, 250000.0, new RealEstate("House", "Porto"));
        d.addAssetEntry(AssetType.VEHICLES, 18000.0, new VehicleAsset("Renault Clio"));
        d.addAssetEntry(AssetType.STOCKS, 75000.0, new StockAsset("Galp"));
        d.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(d);

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), date(2024, Calendar.DECEMBER, 31));

        assertEquals(3, result.size());
    }

    @Test
    void ensureControllerCanBeCreatedWithDefaultConstructor() {
        ConsultAssetsController controller = new ConsultAssetsController();

        assertNotNull(controller);
        assertNotNull(controller.getPoliticalAgents());
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsFalseAfterLogout() {
        AuthenticationRepository authRepo = new AuthenticationRepository();
        authRepo.addUserRole(AuthenticationController.ROLE_JOURNALIST,
                AuthenticationController.ROLE_JOURNALIST);
        authRepo.addUserWithRole("J. Out", "out@news.pt", "JRN22jr",
                AuthenticationController.ROLE_JOURNALIST);
        authRepo.doLogin("out@news.pt", "JRN22jr");
        authRepo.doLogout();

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), authRepo);

        assertFalse(controller.isCurrentUserJournalist());
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsFalseWhenNoSession() {
        AuthenticationRepository authRepo = new AuthenticationRepository();

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), authRepo);

        assertFalse(controller.isCurrentUserJournalist());
    }

    @Test
    void ensureDeclarationOnTheReferenceDateIsIncluded() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Date referenceDate = date(2024, Calendar.JUNE, 30);
        declRepo.save(validatedDeclarationWithRealEstate(joao, referenceDate, 100000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(repoWith(joao), declRepo, null);

        List<AssetEntryDTO> result = controller.getAssetsAt(dto(joao), referenceDate);

        assertEquals(1, result.size());
    }
}
