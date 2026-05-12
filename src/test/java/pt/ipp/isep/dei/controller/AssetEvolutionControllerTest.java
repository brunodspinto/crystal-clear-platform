package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssetEvolutionControllerTest {

    private static final Organization ORG =
            new Organization("Parliament", "public", OrganizationType.POLITICAL_PARTY);

    private static Date dateOf(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }

    private PoliticalAgent createAgent(String email, String nif) {
        return new PoliticalAgent("Agent", email, "12345678", nif,
                dateOf(2020, Calendar.JANUARY, 1), null);
    }

    private Declaration createDeclaration(PoliticalAgent agent, Date date, DeclarationType type,
                                           double grossSalary, double consulting, double board,
                                           double realEstate, double vehicles, double stocks) {
        Declaration d = new Declaration(type, agent, date);
        d.addPositionEntry(ORG, "Deputy", PositionNature.PUBLIC,
                grossSalary, consulting, board, dateOf(2020, Calendar.JANUARY, 1), null);
        if (realEstate > 0) {
            d.addAssetEntry(AssetType.REAL_ESTATE, realEstate, new RealEstate("House", "Lisbon"));
        }
        if (vehicles > 0) {
            d.addAssetEntry(AssetType.VEHICLES, vehicles, new VehicleAsset("Car"));
        }
        if (stocks > 0) {
            d.addAssetEntry(AssetType.STOCKS, stocks, new StockAsset("EDP shares"));
        }
        return d;
    }

    // -------------------------------------------------------------------------
    // getPoliticalAgents
    // -------------------------------------------------------------------------

    @Test
    void ensureGetPoliticalAgentsReturnsAll() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(createAgent("a@gov.pt", "111111111"));
        agentRepo.save(createAgent("b@gov.pt", "222222222"));
        AssetEvolutionController ctrl = new AssetEvolutionController(agentRepo, new DeclarationRepository());
        assertEquals(2, ctrl.getPoliticalAgents().size());
    }

    @Test
    void ensureGetPoliticalAgentsReturnsEmptyWhenNone() {
        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), new DeclarationRepository());
        assertTrue(ctrl.getPoliticalAgents().isEmpty());
    }

    // -------------------------------------------------------------------------
    // getAllDeclarationsForAgent — filtering
    // -------------------------------------------------------------------------

    @Test
    void ensureNullAgentThrows() {
        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), new DeclarationRepository());
        assertThrows(IllegalArgumentException.class, () -> ctrl.getAllDeclarationsForAgent(null));
    }

    @Test
    void ensureReturnsOnlyDeclarationsOfSelectedAgent() {
        PoliticalAgent a1 = createAgent("a1@gov.pt", "111111111");
        PoliticalAgent a2 = createAgent("a2@gov.pt", "222222222");
        DeclarationRepository repo = new DeclarationRepository();
        repo.save(createDeclaration(a1, dateOf(2022, Calendar.MARCH, 1), DeclarationType.INITIAL,
                50000, 1000, 500, 200000, 20000, 5000));
        repo.save(createDeclaration(a2, dateOf(2022, Calendar.JUNE, 1), DeclarationType.INITIAL,
                60000, 0, 0, 300000, 0, 0));

        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), repo);
        List<Declaration> result = ctrl.getAllDeclarationsForAgent(a1);
        assertEquals(1, result.size());
        assertEquals(a1, result.get(0).getAgent());
    }

    @Test
    void ensureIncludesAllDeclarationTypes() {
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        DeclarationRepository repo = new DeclarationRepository();
        Declaration d1 = createDeclaration(agent, dateOf(2022, Calendar.JANUARY, 1),
                DeclarationType.INITIAL, 50000, 0, 0, 0, 0, 0);
        Declaration d2 = createDeclaration(agent, dateOf(2023, Calendar.JANUARY, 1),
                DeclarationType.REGULAR, 55000, 0, 0, 0, 0, 0);
        Declaration d3 = createDeclaration(agent, dateOf(2023, Calendar.JUNE, 1),
                DeclarationType.EXCEPTIONAL, 55000, 2000, 0, 0, 0, 0);
        repo.save(d1);
        repo.save(d2);
        repo.save(d3);

        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), repo);
        assertEquals(3, ctrl.getAllDeclarationsForAgent(agent).size());
    }

    @Test
    void ensureIncludesAllStatuses() {
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        DeclarationRepository repo = new DeclarationRepository();
        Declaration d1 = createDeclaration(agent, dateOf(2022, Calendar.JANUARY, 1),
                DeclarationType.INITIAL, 50000, 0, 0, 0, 0, 0);
        Declaration d2 = createDeclaration(agent, dateOf(2023, Calendar.JANUARY, 1),
                DeclarationType.REGULAR, 55000, 0, 0, 0, 0, 0);
        d1.setStatus(DeclarationStatus.VALIDATED);
        // d2 stays PENDING
        repo.save(d1);
        repo.save(d2);

        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), repo);
        assertEquals(2, ctrl.getAllDeclarationsForAgent(agent).size());
    }

    // -------------------------------------------------------------------------
    // getAllDeclarationsForAgent — chronological order
    // -------------------------------------------------------------------------

    @Test
    void ensureDeclarationsAreReturnedChronologically() {
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        DeclarationRepository repo = new DeclarationRepository();
        Date early = dateOf(2021, Calendar.JANUARY, 1);
        Date middle = dateOf(2022, Calendar.JANUARY, 1);
        Date late = dateOf(2023, Calendar.JANUARY, 1);

        // save out of order
        repo.save(createDeclaration(agent, late, DeclarationType.REGULAR, 0, 0, 0, 0, 0, 0));
        repo.save(createDeclaration(agent, early, DeclarationType.INITIAL, 0, 0, 0, 0, 0, 0));
        repo.save(createDeclaration(agent, middle, DeclarationType.REGULAR, 0, 0, 0, 0, 0, 0));

        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), repo);
        List<Declaration> result = ctrl.getAllDeclarationsForAgent(agent);

        assertTrue(result.get(0).getSubmissionDate().compareTo(result.get(1).getSubmissionDate()) <= 0);
        assertTrue(result.get(1).getSubmissionDate().compareTo(result.get(2).getSubmissionDate()) <= 0);
    }

    @Test
    void ensureEmptyListReturnedWhenNoDeclarationsForAgent() {
        PoliticalAgent agent = createAgent("agent@gov.pt", "111111111");
        AssetEvolutionController ctrl = new AssetEvolutionController(
                new PoliticalAgentRepository(), new DeclarationRepository());
        assertTrue(ctrl.getAllDeclarationsForAgent(agent).isEmpty());
    }
}
