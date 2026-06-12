package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.dto.ComplaintItemDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.repository.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
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

    private SubmitComplaintController controllerLoggedInWithCitizen(
            PoliticalAgent agent, ComplaintRepository complaintRepo) {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(agent);
        CitizenRepository citizenRepo = new CitizenRepository();
        citizenRepo.save(new Citizen("citizen@test.com", "Test Citizen", "CC222222222"));
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("citizen@test.com", "Test Citizen");
        return new SubmitComplaintController(agentRepo, citizenRepo, complaintRepo, authRepo);
    }

    // --- lists (Domain -> UI via DTO) -----------------------------------------

    @Test
    void ensureGetPoliticalAgentsReturnsDTOsForAllAgents() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(createTestAgent());

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, new CitizenRepository(),
                new ComplaintRepository(), new AuthenticationRepository());

        List<PoliticalAgentDTO> agents = controller.getPoliticalAgents();
        assertEquals(1, agents.size());
        assertEquals("Agent Name", agents.get(0).getName());
        assertEquals("agent@gov.pt", agents.get(0).getEmail());
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

        assertEquals(PoliticalFunction.values().length, controller.getPoliticalFunctions().size());
    }

    // --- startComplaint (UI -> Domain via DTO) --------------------------------

    @Test
    void ensureStartComplaintWorksForLoggedInCitizen() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());

        PoliticalAgentDTO agentDto = controller.getPoliticalAgents().get(0);
        assertTrue(controller.startComplaint(agentDto));
        assertEquals(0, controller.getCurrentGrievanceCount());
    }

    @Test
    void ensureStartComplaintFailsWhenCitizenNotInRepository() {
        PoliticalAgentRepository agentRepo = new PoliticalAgentRepository();
        agentRepo.save(createTestAgent());
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("unknown@test.com", "Unknown");

        SubmitComplaintController controller = new SubmitComplaintController(
                agentRepo, new CitizenRepository(), new ComplaintRepository(), authRepo);

        assertFalse(controller.startComplaint(controller.getPoliticalAgents().get(0)));
    }

    @Test
    void ensureStartComplaintFailsWhenAgentNotFound() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());

        PoliticalAgentDTO unknown = new PoliticalAgentDTO("Ghost", "ghost@gov.pt");
        assertFalse(controller.startComplaint(unknown));
    }

    @Test
    void ensureStartComplaintFailsWithNullDto() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());

        assertFalse(controller.startComplaint(null));
    }

    // --- grievances -----------------------------------------------------------

    @Test
    void ensureAddGrievanceGrowsTheComplaintAndExposesDTOs() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());
        controller.startComplaint(controller.getPoliticalAgents().get(0));

        controller.addGrievance("First", PAST_DATE, PoliticalFunction.MAYOR);
        controller.addGrievance("Second", PAST_DATE, PoliticalFunction.DEPUTY);

        assertEquals(2, controller.getCurrentGrievanceCount());
        List<ComplaintItemDTO> grievances = controller.getCurrentGrievances();
        assertEquals(2, grievances.size());
        assertEquals("First", grievances.get(0).getDescription());
        assertEquals(PoliticalFunction.DEPUTY, grievances.get(1).getPoliticalFunction());
    }

    @Test
    void ensureAddGrievanceWithoutStartFails() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());

        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.addGrievance("First", PAST_DATE, PoliticalFunction.MAYOR);
            }
        });
    }

    // --- submit ---------------------------------------------------------------

    @Test
    void ensureSubmitComplaintStoresOneComplaintWithSeveralGrievances() {
        PoliticalAgent agent = createTestAgent();
        ComplaintRepository complaintRepo = new ComplaintRepository();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, complaintRepo);

        controller.startComplaint(controller.getPoliticalAgents().get(0));
        controller.addGrievance("First", PAST_DATE, PoliticalFunction.MAYOR);
        controller.addGrievance("Second", PAST_DATE, PoliticalFunction.DEPUTY);

        assertTrue(controller.submitComplaint());
        assertEquals(1, complaintRepo.getComplaints().size());
        assertEquals(2, complaintRepo.getComplaints().get(0).getItemCount());
        // after submit, the in-progress complaint is cleared
        assertEquals(0, controller.getCurrentGrievanceCount());
    }

    @Test
    void ensureSubmitComplaintFailsWhenNoGrievances() {
        PoliticalAgent agent = createTestAgent();
        ComplaintRepository complaintRepo = new ComplaintRepository();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, complaintRepo);

        controller.startComplaint(controller.getPoliticalAgents().get(0));

        assertFalse(controller.submitComplaint());
        assertTrue(complaintRepo.getComplaints().isEmpty());
    }

    @Test
    void ensureSubmitComplaintFailsWhenNothingStarted() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());

        assertFalse(controller.submitComplaint());
    }

    @Test
    void ensureCancelComplaintResetsTheInProgressComplaint() {
        PoliticalAgent agent = createTestAgent();
        SubmitComplaintController controller = controllerLoggedInWithCitizen(agent, new ComplaintRepository());
        controller.startComplaint(controller.getPoliticalAgents().get(0));
        controller.addGrievance("First", PAST_DATE, PoliticalFunction.MAYOR);

        controller.cancelComplaint();

        assertEquals(0, controller.getCurrentGrievanceCount());
        assertTrue(controller.getCurrentGrievances().isEmpty());
    }
}
