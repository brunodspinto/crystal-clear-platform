package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubmitComplaintControllerTest {

    private static final Date PAST_DATE = new Date(0);

    private PoliticalAgent createTestAgent() {
        return new PoliticalAgent("Agent Name", "agent@gov.pt", "12345678", "123456789",
                new Date(), null);
    }

    private AuthenticationRepository createAuthRepoLoggedInAs(String email, String name) {
        AuthenticationRepository authRepo = new AuthenticationRepository();
        authRepo.addUserRole("CITIZEN", "Citizen");
        authRepo.addUserWithRole(name, email, "pass", "CITIZEN");
        authRepo.doLogin(email, "pass");
        return authRepo;
    }

    @Test
    void ensureGetPoliticalAgentsReturnsAll() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(createTestAgent());

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, new CitizenRepository(),
                new ComplaintRepository(), new AuthenticationRepository());

        assertEquals(1, controller.getPoliticalAgents().size());
    }

    @Test
    void ensureGetPoliticalAgentsReturnsEmptyWhenNoneRegistered() {
        SubmitComplaintController controller = new SubmitComplaintController(
                new PoliticalAgentRepository(), new CitizenRepository(),
                new ComplaintRepository(), new AuthenticationRepository());

        assertTrue(controller.getPoliticalAgents().isEmpty());
    }

    @Test
    void ensureGetPoliticalFunctionsReturnsAllValues() {
        SubmitComplaintController controller = new SubmitComplaintController(
                new PoliticalAgentRepository(), new CitizenRepository(),
                new ComplaintRepository(), new AuthenticationRepository());

        List<PoliticalFunction> functions = controller.getPoliticalFunctions();
        assertEquals(PoliticalFunction.values().length, functions.size());
    }

    @Test
    void ensureSubmitComplaintWorksWhenCitizenIsAuthenticated() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        PoliticalAgent agent = createTestAgent();
        agentRepo.save(agent);

        CitizenRepository citizenRepo = new CitizenRepository();
        citizenRepo.save(new Citizen("citizen@test.com", "Test Citizen", "CC222222222"));

        ComplaintRepository complaintRepo = new ComplaintRepository();
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("citizen@test.com", "Test Citizen");

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, citizenRepo, complaintRepo, authRepo);

        boolean result = controller.submitComplaint("Valid complaint", PAST_DATE, agent, PoliticalFunction.MAYOR);

        assertTrue(result);
    }

    @Test
    void ensureSubmitComplaintFailsWhenCitizenNotInRepository() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        PoliticalAgent agent = createTestAgent();
        agentRepo.save(agent);

        CitizenRepository citizenRepo = new CitizenRepository();
        // citizen authenticated but not saved in the citizen repository

        ComplaintRepository complaintRepo = new ComplaintRepository();
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("unknown@test.com", "Unknown");

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, citizenRepo, complaintRepo, authRepo);

        boolean result = controller.submitComplaint("Valid complaint", PAST_DATE, agent, PoliticalFunction.MAYOR);

        assertFalse(result);
    }

    @Test
    void ensureSubmitComplaintSavesToRepository() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        PoliticalAgent agent = createTestAgent();
        agentRepo.save(agent);

        CitizenRepository citizenRepo = new CitizenRepository();
        citizenRepo.save(new Citizen("citizen@test.com", "Test Citizen", "CC222222222"));

        ComplaintRepository complaintRepo = new ComplaintRepository();
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("citizen@test.com", "Test Citizen");

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, citizenRepo, complaintRepo, authRepo);

        controller.submitComplaint("First complaint", PAST_DATE, agent, PoliticalFunction.DEPUTY);
        controller.submitComplaint("Second complaint", PAST_DATE, agent, PoliticalFunction.MINISTER);

        assertEquals(2, complaintRepo.getComplaints().size());
    }
}
