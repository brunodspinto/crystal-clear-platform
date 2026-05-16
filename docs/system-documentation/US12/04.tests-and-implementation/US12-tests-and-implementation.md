# US12 - Make a Complaint About a Political Agent

## 4. Tests

**Test 1:** Check that it is not possible to create a Complaint with a null description — AC4.

    @Test
    void ensureComplaintFailsWithNullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint(null, PAST_DATE, citizen, agent, PoliticalFunction.MAYOR));
    }

**Test 2:** Check that it is not possible to create a Complaint with a blank description — AC4.

    @Test
    void ensureComplaintFailsWithBlankDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("   ", PAST_DATE, citizen, agent, PoliticalFunction.MAYOR));
    }

**Test 3:** Check that it is not possible to create a Complaint with a null date — AC3.

    @Test
    void ensureComplaintFailsWithNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", null, citizen, agent, PoliticalFunction.MAYOR));
    }

**Test 4:** Check that it is not possible to create a Complaint with a future date — AC3.

    @Test
    void ensureComplaintFailsWithFutureDate() {
        Date futureDate = new Date(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", futureDate, citizen, agent, PoliticalFunction.MAYOR));
    }

**Test 5:** Check that today's date is a valid complaint date — AC3.

    @Test
    void ensureComplaintWithTodayDateWorks() {
        Complaint complaint = new Complaint("Description", new Date(), citizen, agent, PoliticalFunction.COUNCILLOR);
        assertNotNull(complaint);
    }

**Test 6:** Check that the submission date is set automatically — AC5.

    @Test
    void ensureSubmissionDateIsSetAutomatically() {
        Complaint complaint = new Complaint("Description", PAST_DATE, citizen, agent, PoliticalFunction.DEPUTY);
        assertNotNull(complaint.getSubmissionDate());
        assertFalse(complaint.getSubmissionDate().after(new Date()));
    }

**Test 7:** Check that a Complaint cannot be created with a null citizen — AC5.

    @Test
    void ensureComplaintFailsWithNullCitizen() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, null, agent, PoliticalFunction.MAYOR));
    }

**Test 8:** Check that a Complaint cannot be created with a null political agent — AC1.

    @Test
    void ensureComplaintFailsWithNullPoliticalAgent() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, citizen, null, PoliticalFunction.MAYOR));
    }

**Test 9:** Check that a Complaint cannot be created with a null political function — AC2.

    @Test
    void ensureComplaintFailsWithNullPoliticalFunction() {
        assertThrows(IllegalArgumentException.class, () ->
                new Complaint("Description", PAST_DATE, citizen, agent, null));
    }


## 5. Construction (Implementation)

### Class SubmitComplaintController

```java
public boolean submitComplaint(String description, Date complaintDate,
                               PoliticalAgent politicalAgent, PoliticalFunction politicalFunction) {
    Email email = authenticationRepository.getCurrentUserSession().getUserId();
    Citizen citizen = citizenRepository.getCitizenByEmail(email.getEmail());
    if (citizen == null) {
        return false;
    }
    Complaint complaint = new Complaint(description, complaintDate, citizen,
            politicalAgent, politicalFunction);
    return complaintRepository.save(complaint);
}
```

### Class Complaint

```java
public Complaint(String description, Date complaintDate, Citizen citizen,
                 PoliticalAgent politicalAgent, PoliticalFunction politicalFunction) {
    if (description == null || description.isBlank()) {
        throw new IllegalArgumentException("Description cannot be null or empty");
    }
    if (complaintDate == null) {
        throw new IllegalArgumentException("Complaint date cannot be null");
    }
    if (complaintDate.after(new Date())) {
        throw new IllegalArgumentException("Complaint date cannot be in the future");
    }
    if (citizen == null) {
        throw new IllegalArgumentException("Citizen cannot be null");
    }
    if (politicalAgent == null) {
        throw new IllegalArgumentException("Political agent cannot be null");
    }
    if (politicalFunction == null) {
        throw new IllegalArgumentException("Political function cannot be null");
    }
    this.description = description;
    this.complaintDate = complaintDate;
    this.submissionDate = new Date();
    this.citizen = citizen;
    this.politicalAgent = politicalAgent;
    this.politicalFunction = politicalFunction;
}
```


## 6. Integration and Demo

* A new **Citizen** role was added to the authentication system.
* A new **Citizen menu** with the option "Submit Complaint" was added.
* For demo purposes, two political agents and one citizen are bootstrapped when the system starts.
* Demo credentials: **citizen@this.app / citizen**.


## 7. Observations

* The identity of the citizen who submitted the complaint is stored internally (associated with the `Complaint`) for audit purposes, but is not publicly disclosed — AC5.
* The `submissionDate` is automatically set to `new Date()` at the time of Complaint instantiation — AC5.
* The `complaintDate` (when the reported behaviour occurred) is provided by the citizen and is distinct from the `submissionDate`.
