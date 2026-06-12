package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.RegistrationStatus;
import pt.ipp.isep.dei.domain.UserRole;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.RegistrationRequestRepository;
import pt.ipp.isep.dei.service.EmailService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class ReviewRegistrationControllerTest {

    private RegistrationRequestRepository repository;
    private RecordingEmailService emailService;
    private ReviewRegistrationController controller;

    @BeforeEach
    void setUp() {
        repository = new RegistrationRequestRepository();
        AuthenticationRepository authRepo = new AuthenticationRepository();
        emailService = new RecordingEmailService();
        controller = new ReviewRegistrationController(repository, authRepo, emailService);
    }

    private RegistrationRequest makeRequest(String email, UserRole role) {
        String doc = (role == UserRole.JOURNALIST || role == UserRole.CITIZEN) ? "DOC123" : null;
        return new RegistrationRequest("Test User", email, "ABCde12", role, doc);
    }

    @Test
    void ensureGetPendingRequestsReturnsEmptyWhenNone() {
        assertTrue(controller.getPendingRequests().isEmpty());
    }

    @Test
    void ensureGetPendingRequestsReturnsSavedRequest() {
        repository.save(makeRequest("a@gov.pt", UserRole.POLITICAL_AGENT));
        assertEquals(1, controller.getPendingRequests().size());
    }

    @Test
    void ensureGetPendingRequestsExcludesApprovedRequests() {
        RegistrationRequest r = makeRequest("b@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        r.approve();
        assertTrue(controller.getPendingRequests().isEmpty());
    }

    @Test
    void ensureGetPendingRequestsExcludesRejectedRequests() {
        RegistrationRequest r = makeRequest("c@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        r.reject("bad data");
        assertTrue(controller.getPendingRequests().isEmpty());
    }

    @Test
    void ensureApproveRequestSetsStatusApproved() {
        RegistrationRequest r = makeRequest("d@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(RegistrationStatus.APPROVED, r.getStatus());
    }

    @Test
    void ensureRejectRequestSetsStatusRejected() {
        RegistrationRequest r = makeRequest("e@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.rejectRequest(r, "Missing documents");
        assertEquals(RegistrationStatus.REJECTED, r.getStatus());
    }

    @Test
    void ensureRejectRequestStoresReason() {
        RegistrationRequest r = makeRequest("f@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.rejectRequest(r, "Invalid ID");
        assertEquals("Invalid ID", r.getRejectionReason());
    }

    @Test
    void ensureRejectRequestWithBlankReasonThrows() {
        RegistrationRequest r = makeRequest("g@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.rejectRequest(r, "");
            }
        });
    }

    @Test
    void ensureApproveAlreadyApprovedThrows() {
        RegistrationRequest r = makeRequest("h@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.approveRequest(r);
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.approveRequest(r);
            }
        });
    }

    @Test
    void ensureRejectAlreadyRejectedThrows() {
        RegistrationRequest r = makeRequest("i@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.rejectRequest(r, "reason");
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                controller.rejectRequest(r, "again");
            }
        });
    }

    @Test
    void ensureMultiplePendingRequestsAllReturned() {
        repository.save(makeRequest("j@gov.pt", UserRole.POLITICAL_AGENT));
        repository.save(makeRequest("k@gov.pt", UserRole.ETHICS_COMMITTEE));
        List<RegistrationRequest> pending = controller.getPendingRequests();
        assertEquals(2, pending.size());
    }

    @Test
    void ensureApproveCitizenRequestSetsStatusApproved() {
        RegistrationRequest r = makeRequest("citizen.review@gov.pt", UserRole.CITIZEN);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(RegistrationStatus.APPROVED, r.getStatus());
    }

    @Test
    void ensureApproveJournalistRequestSetsStatusApproved() {
        RegistrationRequest r = makeRequest("journalist.review@gov.pt", UserRole.JOURNALIST);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(RegistrationStatus.APPROVED, r.getStatus());
    }

    @Test
    void ensureApproveEthicsCommitteeRequestSetsStatusApproved() {
        RegistrationRequest r = makeRequest("ethics.review@gov.pt", UserRole.ETHICS_COMMITTEE);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(RegistrationStatus.APPROVED, r.getStatus());
    }

    @Test
    void ensureApproveAdministratorRequestSetsStatusApproved() {
        RegistrationRequest r = makeRequest("admin.review@gov.pt", UserRole.ADMINISTRATOR);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(RegistrationStatus.APPROVED, r.getStatus());
    }

    @Test
    void ensureDefaultConstructorInitialisesRepositoriesFromSingleton() {
        ReviewRegistrationController defaultController = new ReviewRegistrationController();
        assertNotNull(defaultController.getPendingRequests());
    }

    @Test
    void ensureApproveRequestSendsNotificationToCorrectEmail() {
        RegistrationRequest r = makeRequest("notify@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.approveRequest(r);
        assertEquals(1, emailService.getSentEmails().size());
        assertEquals("notify@gov.pt", emailService.getSentEmails().get(0));
    }

    @Test
    void ensureRejectRequestSendsNotificationToCorrectEmail() {
        RegistrationRequest r = makeRequest("reject@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.rejectRequest(r, "Incomplete data");
        assertEquals(1, emailService.getSentEmails().size());
        assertEquals("reject@gov.pt", emailService.getSentEmails().get(0));
    }

    @Test
    void ensureApproveNotificationSubjectIndicatesApproval() {
        RegistrationRequest r = makeRequest("approved@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.approveRequest(r);
        assertTrue(emailService.getLastSubject().contains("Approved"));
    }

    @Test
    void ensureRejectNotificationBodyContainsReason() {
        RegistrationRequest r = makeRequest("rejected@gov.pt", UserRole.POLITICAL_AGENT);
        repository.save(r);
        controller.rejectRequest(r, "Wrong role");
        assertTrue(emailService.getLastBody().contains("Wrong role"));
    }

    /**
     * Test double that records sent emails for assertion.
     */
    private static class RecordingEmailService implements EmailService {

        private final List<String> sentEmails = new ArrayList<>();
        private String lastSubject = "";
        private String lastBody = "";

        @Override
        public void sendNotification(String toEmail, String subject, String body) {
            sentEmails.add(toEmail);
            lastSubject = subject;
            lastBody = body;
        }

        public List<String> getSentEmails() {
            return sentEmails;
        }

        public String getLastSubject() {
            return lastSubject;
        }

        public String getLastBody() {
            return lastBody;
        }
    }
}
