# US27 - Assess Complaint

## 4. Tests

The functionality is covered at two levels: the domain rules of `ComplaintAssessment` (covered by `ComplaintAssessmentTest`) and the application flow of `AssessComplaintController` (covered by `AssessComplaintControllerTest`).

**Test 1:** Check that a valid assessment can be created.

    @Test
    void ensureValidAssessmentCreationWorks() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.VALID, null);
        assertNotNull(assessment);
    }

**Test 2:** Check that an invalid assessment with a reason can be created (AC: a reason must be provided when the complaint is classified as invalid).

    @Test
    void ensureInvalidAssessmentWithReasonWorks() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.INVALID, "Lacks evidence.");
        assertNotNull(assessment);
        assertEquals("Lacks evidence.", assessment.getReason());
    }

**Test 3:** Check that an INVALID outcome without a reason is rejected.

    @Test
    void ensureInvalidOutcomeWithoutReasonThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(createMember(), createComplaint(), new Date(),
                        ComplaintOutcome.INVALID, null));
    }

**Test 4:** Check that an INVALID outcome with a blank reason is rejected.

    @Test
    void ensureInvalidOutcomeWithBlankReasonThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(createMember(), createComplaint(), new Date(),
                        ComplaintOutcome.INVALID, "   "));
    }

**Test 5:** Check that a VALID outcome stores no reason, even if one is passed.

    @Test
    void ensureValidOutcomeHasNullReason() {
        ComplaintAssessment assessment = new ComplaintAssessment(
                createMember(), createComplaint(), new Date(), ComplaintOutcome.VALID, "ignored");
        assertNull(assessment.getReason());
    }

**Test 6:** Check that a null member is rejected.

    @Test
    void ensureNullMemberThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(null, createComplaint(), new Date(), ComplaintOutcome.VALID, null));
    }

**Test 7:** Check that a null complaint is rejected.

    @Test
    void ensureNullComplaintThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(createMember(), null, new Date(), ComplaintOutcome.VALID, null));
    }

**Test 8:** Check that a null assessment date is rejected.

    @Test
    void ensureNullDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(createMember(), createComplaint(), null, ComplaintOutcome.VALID, null));
    }

**Test 9:** Check that a null outcome is rejected.

    @Test
    void ensureNullOutcomeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintAssessment(createMember(), createComplaint(), new Date(), null, null));
    }

**Test 10:** Check that all getters return the values given to the constructor.

    @Test
    void ensureGettersReturnCorrectValues() {
        EthicsCommitteeMember member = createMember();
        Complaint complaint = createComplaint();
        Date date = new Date();
        ComplaintAssessment assessment = new ComplaintAssessment(
                member, complaint, date, ComplaintOutcome.INVALID, "No proof.");

        assertEquals(member, assessment.getMember());
        assertEquals(complaint, assessment.getComplaint());
        assertEquals(date, assessment.getAssessmentDate());
        assertEquals(ComplaintOutcome.INVALID, assessment.getOutcome());
        assertEquals("No proof.", assessment.getReason());
    }

**Test 11:** Check that the controller lists all submitted complaints.

    @Test
    void ensureGetComplaintsReturnsAll() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        complaintRepo.save(createComplaint());
        complaintRepo.save(createComplaint());

        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, new ComplaintAssessmentRepository());

        assertEquals(2, controller.getComplaints().size());
    }

**Test 12:** Check that the controller returns an empty list when there are no complaints.

    @Test
    void ensureGetComplaintsReturnsEmptyWhenNone() {
        AssessComplaintController controller = createController("ec@test.com",
                new ComplaintRepository(), new ComplaintAssessmentRepository());

        assertTrue(controller.getComplaints().isEmpty());
    }

**Test 13:** Check that assessing a complaint as valid saves the assessment.

    @Test
    void ensureAssessComplaintValidWorks() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, assessmentRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.VALID, null);

        assertTrue(result);
        assertEquals(1, assessmentRepo.getAssessments().size());
        assertEquals(ComplaintOutcome.VALID, assessmentRepo.getAssessments().get(0).getOutcome());
    }

**Test 14:** Check that assessing a complaint as invalid with a reason saves the assessment with that reason.

    @Test
    void ensureAssessComplaintInvalidWithReasonWorks() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, assessmentRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.INVALID, "No evidence.");

        assertTrue(result);
        assertEquals(ComplaintOutcome.INVALID, assessmentRepo.getAssessments().get(0).getOutcome());
        assertEquals("No evidence.", assessmentRepo.getAssessments().get(0).getReason());
    }

**Test 15:** Check that the assessment fails when the logged-in user is not a registered Ethics Committee Member.

    @Test
    void ensureAssessComplaintFailsWhenMemberNotInRepository() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        ComplaintAssessmentRepository assessmentRepo = new ComplaintAssessmentRepository();
        EthicsCommitteeMemberRepository memberRepo = new EthicsCommitteeMemberRepository();
        // member not saved in the repository
        AuthenticationRepository authRepo = createAuthRepoLoggedInAs("unknown@ec.pt", "Unknown");

        AssessComplaintController controller = new AssessComplaintController(
                complaintRepo, assessmentRepo, memberRepo, authRepo);

        boolean result = controller.assessComplaint(complaint, ComplaintOutcome.VALID, null);

        assertFalse(result);
        assertTrue(assessmentRepo.getAssessments().isEmpty());
    }

**Test 16:** Check that the controller rejects an INVALID outcome without a reason.

    @Test
    void ensureAssessComplaintInvalidWithoutReasonThrows() {
        ComplaintRepository complaintRepo = new ComplaintRepository();
        Complaint complaint = createComplaint();
        complaintRepo.save(complaint);

        AssessComplaintController controller = createController("ec@test.com",
                complaintRepo, new ComplaintAssessmentRepository());

        assertThrows(IllegalArgumentException.class, () ->
                controller.assessComplaint(complaint, ComplaintOutcome.INVALID, null));
    }


## 5. Construction (Implementation)

### Class ComplaintAssessment

Domain class that records the result of assessing a complaint. The constructor validates all required fields and enforces the acceptance criterion: when the outcome is INVALID, a non-blank reason must be provided. When the outcome is VALID, any reason passed is discarded.

```java
public ComplaintAssessment(EthicsCommitteeMember member, Complaint complaint,
                           Date assessmentDate, ComplaintOutcome outcome, String reason) {
    if (member == null) {
        throw new IllegalArgumentException("Member cannot be null.");
    }
    if (complaint == null) {
        throw new IllegalArgumentException("Complaint cannot be null.");
    }
    if (assessmentDate == null) {
        throw new IllegalArgumentException("Assessment date cannot be null.");
    }
    if (outcome == null) {
        throw new IllegalArgumentException("Outcome cannot be null.");
    }
    if (outcome == ComplaintOutcome.INVALID && (reason == null || reason.isBlank())) {
        throw new IllegalArgumentException("A reason must be provided when the outcome is INVALID.");
    }
    this.member = member;
    this.complaint = complaint;
    this.assessmentDate = assessmentDate;
    this.outcome = outcome;
    this.reason = (outcome == ComplaintOutcome.INVALID) ? reason : null;
}
```

### Enum ComplaintOutcome

Enum with the two possible classifications of a complaint: `VALID` and `INVALID`.

### Class AssessComplaintController

Application controller that lists the submitted complaints and saves the assessment on behalf of the logged-in Ethics Committee Member. The member is identified through the current user session and looked up in the `EthicsCommitteeMemberRepository`; if not found, the assessment is not saved.

```java
public boolean assessComplaint(Complaint complaint, ComplaintOutcome outcome, String reason) {
    Email email = authenticationRepository.getCurrentUserSession().getUserId();
    EthicsCommitteeMember member = ethicsCommitteeMemberRepository.getByEmail(email.getEmail());
    if (member == null) {
        return false;
    }
    ComplaintAssessment assessment = new ComplaintAssessment(member, complaint, new Date(), outcome, reason);
    return complaintAssessmentRepository.save(assessment);
}
```

The default constructor obtains all repositories from the `Repositories.getInstance()` singleton; a second constructor receives them by parameter so the tests can inject isolated repositories.

### Class ComplaintAssessmentRepository

Stores the saved assessments and is obtained through the `Repositories` singleton.


## 6. Integration and Demo

* An option **"Assess Complaint"** is available in the Ethics Committee menu, both in the console UI (`AssessComplaintUI`) and in the GUI (`AssessComplaint.fxml` with `AssessComplaintFXController`).
* The member selects one of the submitted complaints, classifies it as valid or invalid and, when invalid, must type the reason.
* The assessment is stored with the member, the complaint, the assessment date, the outcome and the reason.


## 7. Observations

* The validation of the reason is done in two places on purpose: the domain class guarantees the invariant and the UI gives immediate feedback before calling the controller.
* The controller never receives the member by parameter; it always resolves the member from the authenticated session, so a complaint cannot be assessed on behalf of someone else.
