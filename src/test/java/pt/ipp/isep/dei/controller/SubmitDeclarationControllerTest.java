package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.OrganizationRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for SubmitDeclarationController (US06).
 * <p>
 * submitDeclaration() resolves the current agent from the auth session, which
 * cannot run in a unit test. Following the project convention, these tests cover
 * all session-independent catalogue methods, plus the Declaration construction
 * pipeline that the controller drives.
 */
class SubmitDeclarationControllerTest {

    private OrganizationRepository orgRepo;
    private DeclarationRepository declRepo;
    private PoliticalAgentRepository agentRepo;

    private Organization parliament;
    private PoliticalAgent agent;
    private static final Date NOW = new Date();

    @BeforeEach
    void setUp() {
        orgRepo = new OrganizationRepository();
        declRepo = new DeclarationRepository();
        agentRepo = new PoliticalAgentRepository();
        parliament = new Organization("Assembleia", "public", OrganizationType.POLITICAL_PARTY);
        orgRepo.add(parliament);
        agent = new PoliticalAgent("Ana Costa", "ana@gov.pt",
                "11111111", "111111111", NOW, null);
        agentRepo.save(agent);
    }

    private SubmitDeclarationController ctrl() {
        return new SubmitDeclarationController(orgRepo, declRepo, agentRepo, null);
    }

    // -------------------------------------------------------------------------
    // getDeclarationTypes
    // -------------------------------------------------------------------------

    @Test
    void ensureGetDeclarationTypesReturnsAllValues() {
        assertEquals(DeclarationType.values().length, ctrl().getDeclarationTypes().size());
    }

    @Test
    void ensureGetDeclarationTypesContainsInitial() {
        assertTrue(ctrl().getDeclarationTypes().contains(DeclarationType.INITIAL));
    }

    @Test
    void ensureGetDeclarationTypesContainsRegular() {
        assertTrue(ctrl().getDeclarationTypes().contains(DeclarationType.REGULAR));
    }

    @Test
    void ensureGetDeclarationTypesContainsExceptional() {
        assertTrue(ctrl().getDeclarationTypes().contains(DeclarationType.EXCEPTIONAL));
    }

    // -------------------------------------------------------------------------
    // getOrganizations
    // -------------------------------------------------------------------------

    @Test
    void ensureGetOrganizationsReturnsSingleRegisteredOrg() {
        assertEquals(1, ctrl().getOrganizations().size());
    }

    @Test
    void ensureGetOrganizationsReturnsEmptyWhenNoneRegistered() {
        SubmitDeclarationController c =
                new SubmitDeclarationController(new OrganizationRepository(), declRepo, agentRepo, null);
        assertTrue(c.getOrganizations().isEmpty());
    }

    @Test
    void ensureGetOrganizationsReflectsMultipleEntries() {
        orgRepo.add(new Organization("Senate", "public", OrganizationType.INSTITUTE));
        assertEquals(2, ctrl().getOrganizations().size());
    }

    // -------------------------------------------------------------------------
    // getPositionNatures
    // -------------------------------------------------------------------------

    @Test
    void ensureGetPositionNaturesReturnsAllValues() {
        assertEquals(PositionNature.values().length, ctrl().getPositionNatures().size());
    }

    @Test
    void ensureGetPositionNaturesContainsPublic() {
        assertTrue(ctrl().getPositionNatures().contains(PositionNature.PUBLIC));
    }

    @Test
    void ensureGetPositionNaturesContainsPrivate() {
        assertTrue(ctrl().getPositionNatures().contains(PositionNature.PRIVATE));
    }

    @Test
    void ensureGetPositionNaturesContainsSocial() {
        assertTrue(ctrl().getPositionNatures().contains(PositionNature.SOCIAL));
    }

    // -------------------------------------------------------------------------
    // getAssetTypes
    // -------------------------------------------------------------------------

    @Test
    void ensureGetAssetTypesReturnsAllValues() {
        assertEquals(AssetType.values().length, ctrl().getAssetTypes().size());
    }

    @Test
    void ensureGetAssetTypesContainsRealEstate() {
        assertTrue(ctrl().getAssetTypes().contains(AssetType.REAL_ESTATE));
    }

    @Test
    void ensureGetAssetTypesContainsVehicles() {
        assertTrue(ctrl().getAssetTypes().contains(AssetType.VEHICLES));
    }

    @Test
    void ensureGetAssetTypesContainsStocks() {
        assertTrue(ctrl().getAssetTypes().contains(AssetType.STOCKS));
    }

    // -------------------------------------------------------------------------
    // Declaration construction pipeline (mirrors what the controller builds)
    // -------------------------------------------------------------------------

    @Test
    void ensureNewDeclarationHasPendingStatus() {
        assertEquals(DeclarationStatus.PENDING,
                new Declaration(DeclarationType.INITIAL, agent, NOW).getStatus());
    }

    @Test
    void ensureNewDeclarationLinkedToCorrectAgent() {
        assertEquals(agent, new Declaration(DeclarationType.INITIAL, agent, NOW).getAgent());
    }

    @Test
    void ensureNewDeclarationHasCorrectType() {
        assertEquals(DeclarationType.EXCEPTIONAL,
                new Declaration(DeclarationType.EXCEPTIONAL, agent, NOW).getType());
    }

    @Test
    void ensureNewDeclarationAllSectionListsEmpty() {
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, NOW);
        assertTrue(d.getPositionEntries().isEmpty());
        assertTrue(d.getSubsidyEntries().isEmpty());
        assertTrue(d.getAssetEntries().isEmpty());
        assertTrue(d.getBusinessParticipations().isEmpty());
        assertTrue(d.getAttachments().isEmpty());
    }

    @Test
    void ensureDeclarationWithAllSectionsCanBeSaved() {
        Declaration d = new Declaration(DeclarationType.INITIAL, agent, NOW);
        d.addPositionEntry(parliament, "Deputy", PositionNature.PUBLIC, 60000, 5000, 2000, NOW, null);
        d.addSubsidyEntry(parliament, 2000.0, "Grant", NOW);
        d.addAssetEntry(AssetType.REAL_ESTATE, 300000.0, new RealEstate("Villa", "Porto"));
        d.addAssetEntry(AssetType.VEHICLES, 22000.0, new VehicleAsset("Tesla"));
        d.addAssetEntry(AssetType.STOCKS, 7000.0, new StockAsset("Galp"));
        d.addBusinessParticipation(parliament, 987654321L, 5000.0, 5.0);
        d.addAttachment("doc.pdf", NOW);
        assertTrue(declRepo.save(d));
    }

    @Test
    void ensureMultipleDeclarationsFromSameAgentCanBeSaved() {
        declRepo.save(new Declaration(DeclarationType.INITIAL, agent, NOW));
        declRepo.save(new Declaration(DeclarationType.REGULAR, agent, NOW));
        declRepo.save(new Declaration(DeclarationType.EXCEPTIONAL, agent, NOW));
        assertEquals(3, declRepo.getAll().size());
    }

    // -------------------------------------------------------------------------
    // submitDeclaration - full controller path with authenticated session
    // -------------------------------------------------------------------------

    private SubmitDeclarationController loginAndBuildCtrl(String email) {
        pt.ipp.isep.dei.repository.AuthenticationRepository authRepo =
                pt.ipp.isep.dei.repository.Repositories.getInstance().getAuthenticationRepository();
        authRepo.doLogout();
        authRepo.addUserRole(AuthenticationController.ROLE_POLITICAL_AGENT, "Political Agent");
        authRepo.addUserWithRole("Agent Test", email, "Agent123*",
                AuthenticationController.ROLE_POLITICAL_AGENT);
        authRepo.doLogin(email, "Agent123*");

        PoliticalAgent loggedAgent = new PoliticalAgent("Agent Test", email,
                "22222222", "222222222", NOW, null);
        agentRepo.save(loggedAgent);

        return new SubmitDeclarationController(orgRepo, declRepo, agentRepo, authRepo);
    }

    @Test
    void ensureSubmitDeclarationReturnsTrueWhenAgentLoggedIn() {
        SubmitDeclarationController controller = loginAndBuildCtrl("agent.submit1@test.com");
        boolean ok = controller.submitDeclaration(DeclarationType.INITIAL,
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>());
        assertTrue(ok);
    }

    @Test
    void ensureSubmitDeclarationPersistsDeclarationInRepository() {
        SubmitDeclarationController controller = loginAndBuildCtrl("agent.submit2@test.com");
        int before = declRepo.getAll().size();
        controller.submitDeclaration(DeclarationType.REGULAR,
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>());
        assertEquals(before + 1, declRepo.getAll().size());
    }

    @Test
    void ensureSubmitDeclarationWithAllSectionsPopulatesDeclaration() {
        SubmitDeclarationController controller = loginAndBuildCtrl("agent.submit3@test.com");

        java.util.List<Object[]> positions = new java.util.ArrayList<>();
        positions.add(new Object[]{parliament, "Deputy", PositionNature.PUBLIC,
                60000.0, 5000.0, 2000.0, NOW, null});

        java.util.List<Object[]> subsidies = new java.util.ArrayList<>();
        subsidies.add(new Object[]{parliament, 2000.0, "Grant", NOW});

        java.util.List<Object[]> assets = new java.util.ArrayList<>();
        assets.add(new Object[]{AssetType.REAL_ESTATE, 300000.0, new RealEstate("Villa", "Porto")});

        java.util.List<Object[]> participations = new java.util.ArrayList<>();
        participations.add(new Object[]{parliament, 987654321L, 5000.0, 5.0});

        java.util.List<Object[]> attachments = new java.util.ArrayList<>();
        attachments.add(new Object[]{"doc.pdf", NOW});

        boolean ok = controller.submitDeclaration(DeclarationType.EXCEPTIONAL,
                positions, subsidies, assets, participations, attachments);
        assertTrue(ok);

        Declaration saved = declRepo.getAll().get(declRepo.getAll().size() - 1);
        assertEquals(1, saved.getPositionEntries().size());
        assertEquals(1, saved.getSubsidyEntries().size());
        assertEquals(1, saved.getAssetEntries().size());
        assertEquals(1, saved.getBusinessParticipations().size());
        assertEquals(1, saved.getAttachments().size());
    }

    @Test
    void ensureSubmitDeclarationReturnsFalseWhenAgentNotRegistered() {
        pt.ipp.isep.dei.repository.AuthenticationRepository authRepo =
                pt.ipp.isep.dei.repository.Repositories.getInstance().getAuthenticationRepository();
        authRepo.doLogout();
        authRepo.addUserRole(AuthenticationController.ROLE_POLITICAL_AGENT, "Political Agent");
        String email = "agent.submit4.unknown@test.com";
        authRepo.addUserWithRole("Unknown Agent", email, "Agent123*",
                AuthenticationController.ROLE_POLITICAL_AGENT);
        authRepo.doLogin(email, "Agent123*");

        SubmitDeclarationController controller =
                new SubmitDeclarationController(orgRepo, declRepo, agentRepo, authRepo);

        boolean ok = controller.submitDeclaration(DeclarationType.INITIAL,
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>(), new java.util.ArrayList<>(),
                new java.util.ArrayList<>());
        assertEquals(false, ok);
    }
}
