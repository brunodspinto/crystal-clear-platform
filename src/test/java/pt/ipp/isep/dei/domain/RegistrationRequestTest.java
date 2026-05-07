package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    // --- password validation ---

    @Test
    void ensureValidPasswordIsAccepted() {
        assertTrue(RegistrationRequest.isValidPassword("AAA11bb"));
    }

    @Test
    void ensurePasswordWithExactly3UpperAnd2DigitsIsAccepted() {
        assertTrue(RegistrationRequest.isValidPassword("ABCde12"));
    }

    @Test
    void ensurePasswordShorterThan7IsRejected() {
        assertFalse(RegistrationRequest.isValidPassword("AAA11b"));
    }

    @Test
    void ensurePasswordLongerThan7IsRejected() {
        assertFalse(RegistrationRequest.isValidPassword("AAA11bbb"));
    }

    @Test
    void ensurePasswordWithFewerThan3UppercaseIsRejected() {
        assertFalse(RegistrationRequest.isValidPassword("AAb1234"));
    }

    @Test
    void ensurePasswordWithFewerThan2DigitsIsRejected() {
        assertFalse(RegistrationRequest.isValidPassword("AAAbbbb"));
    }

    @Test
    void ensurePasswordWithSpecialCharacterIsRejected() {
        assertFalse(RegistrationRequest.isValidPassword("AAA11b!"));
    }

    @Test
    void ensureNullPasswordIsRejected() {
        assertFalse(RegistrationRequest.isValidPassword(null));
    }

    // --- construction ---

    @Test
    void ensureValidRequestIsCreated() {
        RegistrationRequest req = new RegistrationRequest(
                "John Doe", "john@mail.com", "AAA11bb", UserRole.POLITICAL_AGENT, null);
        assertEquals("John Doe", req.getFullName());
        assertEquals("john@mail.com", req.getEmail());
        assertEquals(UserRole.POLITICAL_AGENT, req.getRole());
        assertEquals(RegistrationStatus.PENDING, req.getStatus());
    }

    @Test
    void ensureJournalistWithDocumentIsCreated() {
        RegistrationRequest req = new RegistrationRequest(
                "Jane Press", "jane@news.pt", "AAA11bb", UserRole.JOURNALIST, "J-12345");
        assertEquals("J-12345", req.getIdentificationDocument());
    }

    @Test
    void ensureJournalistWithoutDocumentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("Jane Press", "jane@news.pt", "AAA11bb", UserRole.JOURNALIST, null));
    }

    @Test
    void ensureCitizenWithoutDocumentThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("Joe Citizen", "joe@mail.pt", "AAA11bb", UserRole.CITIZEN, null));
    }

    @Test
    void ensureBlankFullNameThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("  ", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null));
    }

    @Test
    void ensureBlankEmailThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("Name", "", "AAA11bb", UserRole.ADMINISTRATOR, null));
    }

    @Test
    void ensureInvalidPasswordThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("Name", "a@b.com", "short", UserRole.ADMINISTRATOR, null));
    }

    @Test
    void ensureNullRoleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegistrationRequest("Name", "a@b.com", "AAA11bb", null, null));
    }

    // --- approve / reject ---

    @Test
    void ensureApproveChangeStatusToApproved() {
        RegistrationRequest req = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        req.approve();
        assertEquals(RegistrationStatus.APPROVED, req.getStatus());
    }

    @Test
    void ensureRejectChangeStatusToRejectedAndStoresReason() {
        RegistrationRequest req = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        req.reject("Incomplete information");
        assertEquals(RegistrationStatus.REJECTED, req.getStatus());
        assertEquals("Incomplete information", req.getRejectionReason());
    }

    @Test
    void ensureApproveAlreadyApprovedThrows() {
        RegistrationRequest req = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        req.approve();
        assertThrows(IllegalStateException.class, req::approve);
    }

    @Test
    void ensureRejectAlreadyRejectedThrows() {
        RegistrationRequest req = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        req.reject("Reason");
        assertThrows(IllegalStateException.class, () -> req.reject("Another reason"));
    }

    @Test
    void ensureRejectWithBlankReasonThrows() {
        RegistrationRequest req = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        assertThrows(IllegalArgumentException.class, () -> req.reject("  "));
    }

    // --- equals ---

    @Test
    void ensureSameEmailAndRoleAreEqual() {
        RegistrationRequest r1 = new RegistrationRequest(
                "Name A", "same@mail.com", "AAA11bb", UserRole.CITIZEN, "CC123");
        RegistrationRequest r2 = new RegistrationRequest(
                "Name B", "same@mail.com", "BBB22cc", UserRole.CITIZEN, "CC456");
        assertEquals(r1, r2);
    }

    @Test
    void ensureDifferentRoleMeansNotEqual() {
        RegistrationRequest r1 = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        RegistrationRequest r2 = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.POLITICAL_AGENT, null);
        assertNotEquals(r1, r2);
    }

    @Test
    void ensureDifferentEmailMeansNotEqual() {
        RegistrationRequest r1 = new RegistrationRequest(
                "Name", "a@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        RegistrationRequest r2 = new RegistrationRequest(
                "Name", "b@b.com", "AAA11bb", UserRole.ADMINISTRATOR, null);
        assertNotEquals(r1, r2);
    }
}
