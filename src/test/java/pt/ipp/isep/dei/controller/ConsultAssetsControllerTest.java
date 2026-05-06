package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.AssetType;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.DeclarationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.RealEstate;
import pt.ipp.isep.dei.domain.VehicleAsset;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsultAssetsControllerTest {

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
    void ensureAssetsFromAllValidatedDeclarationsUpToDateAreReturned() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2023, Calendar.MARCH, 1), 100000.0));
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2024, Calendar.JUNE, 15), 150000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), declRepo, null);

        List<AssetEntry> result = controller.getAssetsAt(joao, date(2024, Calendar.DECEMBER, 31));

        assertEquals(2, result.size());
    }

    @Test
    void ensureDeclarationsAfterReferenceDateAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2023, Calendar.MARCH, 1), 100000.0));
        declRepo.save(validatedDeclarationWithRealEstate(joao, date(2025, Calendar.MARCH, 1), 200000.0));

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), declRepo, null);

        List<AssetEntry> result = controller.getAssetsAt(joao, date(2024, Calendar.DECEMBER, 31));

        assertEquals(1, result.size());
        assertEquals(100000.0, result.get(0).getAssetValue());
    }

    @Test
    void ensureNonValidatedDeclarationsAreIgnored() {
        PoliticalAgent joao = agentJoao();
        DeclarationRepository declRepo = new DeclarationRepository();

        Declaration pending = new Declaration(DeclarationType.REGULAR, joao, date(2024, Calendar.MARCH, 1));
        pending.addAssetEntry(AssetType.VEHICLES, 25000.0, new VehicleAsset("Car"));
        declRepo.save(pending);

        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), declRepo, null);

        List<AssetEntry> result = controller.getAssetsAt(joao, date(2024, Calendar.DECEMBER, 31));

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureEmptyResultWhenNoDeclarationsExistForAgent() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        List<AssetEntry> result = controller.getAssetsAt(agentJoao(), new Date());

        assertTrue(result.isEmpty());
    }

    @Test
    void ensureNullAgentIsRejected() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertThrows(IllegalArgumentException.class,
                () -> controller.getAssetsAt(null, new Date()));
    }

    @Test
    void ensureNullDateIsRejected() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertThrows(IllegalArgumentException.class,
                () -> controller.getAssetsAt(agentJoao(), null));
    }

    @Test
    void ensureIsCurrentUserJournalistReturnsFalseWhenNoAuthRepository() {
        ConsultAssetsController controller =
                new ConsultAssetsController(new PoliticalAgentRepository(), new DeclarationRepository(), null);

        assertFalse(controller.isCurrentUserJournalist());
    }
}
