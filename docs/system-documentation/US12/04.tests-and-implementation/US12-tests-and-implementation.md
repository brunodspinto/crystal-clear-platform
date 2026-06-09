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

**Test 10:** Check that a grievance (`ComplaintItem`) validates its own data — AC2, AC3, AC4.

    @Test
    void ensureGrievanceFailsWithFutureDate() {
        Date futureDate = new Date(Long.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () ->
                new ComplaintItem("Description", futureDate, PoliticalFunction.MAYOR));
    }

**Test 11:** Check that several grievances can be added to the same complaint — AC6, AC7.

    @Test
    void ensureAddItemGrowsTheComplaint() {
        Complaint complaint = new Complaint(citizen, agent);
        complaint.addItem("First grievance", PAST_DATE, PoliticalFunction.MAYOR);
        complaint.addItem("Second grievance", PAST_DATE, PoliticalFunction.DEPUTY);
        assertEquals(2, complaint.getItemCount());
        assertEquals(PoliticalFunction.DEPUTY, complaint.getItems().get(1).getPoliticalFunction());
    }

**Test 12:** Check that `startComplaint` works for the authenticated citizen — AC1, AC5.

    @Test
    void ensureStartComplaintWorksForLoggedInCitizen() {
        PoliticalAgentDTO agentDto = controller.getPoliticalAgents().get(0);
        assertTrue(controller.startComplaint(agentDto));
        assertEquals(0, controller.getCurrentGrievanceCount());
    }

**Test 13:** Check that a complaint with several grievances is persisted as a single complaint — AC6, AC7.

    @Test
    void ensureSubmitComplaintStoresOneComplaintWithSeveralGrievances() {
        controller.startComplaint(controller.getPoliticalAgents().get(0));
        controller.addGrievance("First", PAST_DATE, PoliticalFunction.MAYOR);
        controller.addGrievance("Second", PAST_DATE, PoliticalFunction.DEPUTY);
        assertTrue(controller.submitComplaint());
        assertEquals(1, complaintRepo.getComplaints().size());
        assertEquals(2, complaintRepo.getComplaints().get(0).getItemCount());
    }

**Test 14:** Check that a complaint with no grievances is not submitted — AC7.

    @Test
    void ensureSubmitComplaintFailsWhenNoGrievances() {
        controller.startComplaint(controller.getPoliticalAgents().get(0));
        assertFalse(controller.submitComplaint());
        assertTrue(complaintRepo.getComplaints().isEmpty());
    }


## 5. Construction (Implementation)

### Class SubmitComplaintController (DTO-based, keeps the complaint being built)

```java
public List<PoliticalAgentDTO> getPoliticalAgents() {
    return politicalAgentMapper.toDTO(politicalAgentRepository.getAll());
}

public boolean startComplaint(PoliticalAgentDTO agentDto) {
    if (agentDto == null) {
        return false;
    }
    Email email = authenticationRepository.getCurrentUserSession().getUserId();
    Citizen citizen = citizenRepository.getCitizenByEmail(email.getEmail());
    if (citizen == null) {
        return false;
    }
    PoliticalAgent agent = politicalAgentRepository.getByEmail(agentDto.getEmail());
    if (agent == null) {
        return false;
    }
    // GRASP Creator: the repository (which records Complaints) instantiates it.
    currentComplaint = complaintRepository.createComplaint(citizen, agent);
    return true;
}

public void addGrievance(String description, Date complaintDate, PoliticalFunction politicalFunction) {
    if (currentComplaint == null) {
        throw new IllegalStateException("No complaint in progress. Call startComplaint first.");
    }
    currentComplaint.addItem(description, complaintDate, politicalFunction);
}

public List<ComplaintItemDTO> getCurrentGrievances() {
    if (currentComplaint == null) {
        return new ArrayList<>();
    }
    return complaintItemMapper.toDTO(currentComplaint.getItems());
}

public boolean submitComplaint() {
    if (currentComplaint == null || currentComplaint.getItemCount() == 0) {
        return false;
    }
    boolean saved = complaintRepository.save(currentComplaint);
    currentComplaint = null;
    return saved;
}
```

### Class PoliticalAgentDTO (and PoliticalAgentMapper)

```java
public class PoliticalAgentDTO {          // a plain "bag of data", no business logic
    private final String name;
    private final String email;           // identifier to map back to the domain

    public PoliticalAgentDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    @Override public String toString() { return name; }
}

public class PoliticalAgentMapper {
    public PoliticalAgentDTO toDTO(PoliticalAgent agent) {
        return new PoliticalAgentDTO(agent.getName(), agent.getEmail());
    }
    public List<PoliticalAgentDTO> toDTO(List<PoliticalAgent> agents) {
        List<PoliticalAgentDTO> dtos = new ArrayList<>();
        for (PoliticalAgent agent : agents) {
            dtos.add(toDTO(agent));
        }
        return dtos;
    }
}
```

### Class Complaint

```java
public Complaint(Citizen citizen, PoliticalAgent politicalAgent) {
    if (citizen == null) {
        throw new IllegalArgumentException("Citizen cannot be null");
    }
    if (politicalAgent == null) {
        throw new IllegalArgumentException("Political agent cannot be null");
    }
    this.citizen = citizen;
    this.politicalAgent = politicalAgent;
    this.submissionDate = new Date();
    this.items = new ArrayList<>();
}

public void addItem(String description, Date complaintDate, PoliticalFunction politicalFunction) {
    items.add(new ComplaintItem(description, complaintDate, politicalFunction));
}

public List<ComplaintItem> getItems() {
    return List.copyOf(items);
}

public int getItemCount() {
    return items.size();
}
```

### Class ComplaintItem

```java
public ComplaintItem(String description, Date complaintDate, PoliticalFunction politicalFunction) {
    if (description == null || description.isBlank()) {
        throw new IllegalArgumentException("Description cannot be null or empty");
    }
    if (complaintDate == null) {
        throw new IllegalArgumentException("Complaint date cannot be null");
    }
    if (complaintDate.after(new Date())) {
        throw new IllegalArgumentException("Complaint date cannot be in the future");
    }
    if (politicalFunction == null) {
        throw new IllegalArgumentException("Political function cannot be null");
    }
    this.description = description;
    this.complaintDate = complaintDate;
    this.politicalFunction = politicalFunction;
}
```

> A backward-compatible constructor `Complaint(description, date, citizen, agent, function)` is kept: it creates the complaint and adds the first grievance, so existing callers (and the assess-complaint US) keep working.


## 6. Integration and Demo

* A new **Citizen** role was added to the authentication system.
* A new **Citizen menu** with the option "Submit Complaint" was added (console UI). The console flow selects the agent once and then loops, collecting and confirming one grievance at a time and asking whether to add another grievance about the same agent, before persisting the whole complaint.
* A **JavaFX 21 graphical interface** is also provided for the citizen: `SubmitComplaintFXController` + `SubmitComplaint.fxml`, reached from the Citizen menu of the GUI. It follows the FXML + Controller (MVC) pattern, delegates to the same `SubmitComplaintController`, and communicates with the rest of the GUI through the `MainController` mediator. The citizen presses **Add grievance** to append each grievance to a list (the agent is locked after the first one) and **Submit complaint** to persist them together.
* For demo purposes, two political agents and one citizen are bootstrapped when the system starts.
* Demo credentials: **citizen@this.app / citizen**.


## 7. Observations

* Following the ESOFT **DTO pattern**, the controller never hands domain objects to the UI: `getPoliticalAgents()` returns `List<PoliticalAgentDTO>` and `getCurrentGrievances()` returns `List<ComplaintItemDTO>` (converted by `PoliticalAgentMapper` / `ComplaintItemMapper`), and the UI sends a `PoliticalAgentDTO` back to `startComplaint(...)`. The complaint being built is kept inside the controller, so the **UI is decoupled from the domain** and works only with DTOs and primitives.
* The identity of the citizen who submitted the complaint is stored internally (associated with the `Complaint`) for audit purposes, but is not publicly disclosed — AC5.
* The `submissionDate` is automatically set to `new Date()` at the time of Complaint instantiation — AC5.
* A `Complaint` aggregates one or more `ComplaintItem` (grievances), each with its own `description`, `complaintDate` and `PoliticalFunction`; all grievances of a complaint refer to the same `PoliticalAgent` — AC6, AC7.
* The `complaintDate` of each grievance (when the reported behaviour occurred) is provided by the citizen and is distinct from the complaint's `submissionDate`.
* For backward compatibility, the convenience constructor `Complaint(description, date, citizen, agent, function)` and the getters `getDescription()` / `getComplaintDate()` / `getPoliticalFunction()` (which return the data of the first grievance) are preserved, so the assess-complaint US keeps working unchanged.
* To support the object-serialization persistence requirement, `Complaint`, `ComplaintItem`, `ComplaintRepository`, `CitizenRepository` and `PoliticalAgentRepository` implement `java.io.Serializable` (`Citizen` and `PoliticalAgent` already do, through `User`; `PoliticalFunction` is an enum and is serializable by default), so the whole complaint object graph can be persisted together with the `Repositories` singleton.
* The persistence itself is performed by `RepositoriesFile` (object serialization to a binary file, following the PPROG pattern): the `Repositories` singleton is loaded on startup and saved on exit by both entry points (`Main` for the console and `App` for the GUI). As a result, submitted complaints survive between two successive runs.
