# US32 - Network Dynamics Over Time

## 4. Tests

The functionality is covered at two levels: the snapshot rules of `NetworkSnapshot` (covered by `NetworkSnapshotTest`) and the application flow of `NetworkDynamicsController` (covered by `NetworkDynamicsControllerTest`).

**Test 1:** Check that a snapshot includes only the entities active at the given date.

    @Test
    void ensureSnapshotIncludesActiveEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "2020-01-01", "2025-12-31"));
        entities.add(makePerson("p2", "2023-01-01", ""));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, new ArrayList<>());

        assertEquals(1, snap.getEntityCount());
        assertEquals("p1", snap.getActiveEntities().get(0).getId());
    }

**Test 2:** Check that an entity whose end date already passed is excluded.

    @Test
    void ensureSnapshotExcludesExpiredEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "2010-01-01", "2019-12-31"));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, new ArrayList<>());

        assertEquals(0, snap.getEntityCount());
    }

**Test 3:** Check that a relation active at the date is included in the snapshot.

    @Test
    void ensureSnapshotIncludesActiveEdges() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));
        entities.add(makePerson("p2", "", ""));

        List<Edge> edges = new ArrayList<>();
        edges.add(makeEdge("p1", "p2", "2020-01-01", "2025-12-31"));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, edges);

        assertTrue(snap.getEdgeCount() >= 1);
    }

**Test 4:** Check that a relation whose end date already passed is excluded.

    @Test
    void ensureSnapshotExcludesExpiredEdges() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));
        entities.add(makePerson("p2", "", ""));

        List<Edge> edges = new ArrayList<>();
        edges.add(makeEdge("p1", "p2", "2010-01-01", "2019-12-31"));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, edges);

        assertEquals(0, snap.getEdgeCount());
    }

**Test 5:** Check that an entity without dates is treated as always active.

    @Test
    void ensureEntityWithNoDateIsAlwaysActive() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));

        NetworkSnapshot snap = NetworkSnapshot.of("2000-01-01", entities, new ArrayList<>());

        assertEquals(1, snap.getEntityCount());
    }

**Test 6:** Check that a null snapshot date is rejected.

    @Test
    void ensureNullDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                NetworkSnapshot.of(null, new ArrayList<>(), new ArrayList<>()));
    }

**Test 7:** Check that a blank snapshot date is rejected.

    @Test
    void ensureBlankDateThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                NetworkSnapshot.of("  ", new ArrayList<>(), new ArrayList<>()));
    }

**Test 8:** Check that the snapshot keeps the date it was built for.

    @Test
    void ensureGetDateReturnsCorrectDate() {
        NetworkSnapshot snap = NetworkSnapshot.of("2023-05-15", new ArrayList<>(), new ArrayList<>());
        assertEquals("2023-05-15", snap.getDate());
    }

**Test 9:** Check that the list of active entities is a defensive copy.

    @Test
    void ensureActiveEntitiesListIsDefensiveCopy() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));
        NetworkSnapshot snap = NetworkSnapshot.of("2023-01-01", entities, new ArrayList<>());
        List<Entity> active = snap.getActiveEntities();
        active.clear();
        assertEquals(1, snap.getEntityCount());
    }

**Test 10:** Check that the controller reports no data when the repository is empty.

    @Test
    void ensureHasDataReturnsFalseWhenEmpty() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertFalse(controller.hasData());
    }

**Test 11:** Check that the controller reports data after entities are loaded.

    @Test
    void ensureHasDataReturnsTrueWhenEntitiesLoaded() {
        NetworkDynamicsController controller = new NetworkDynamicsController(
                repoWithPerson("p1", "", ""));
        assertTrue(controller.hasData());
    }

**Test 12:** Check that one snapshot is built per requested date, in order.

    @Test
    void ensureBuildSnapshotsReturnsOnePerDate() {
        NetworkDynamicsController controller = new NetworkDynamicsController(
                repoWithPerson("p1", "", ""));

        List<NetworkSnapshot> snaps = controller.buildSnapshots(
                Arrays.asList("2021-01-01", "2022-01-01", "2023-01-01"));

        assertEquals(3, snaps.size());
        assertEquals("2021-01-01", snaps.get(0).getDate());
        assertEquals("2022-01-01", snaps.get(1).getDate());
        assertEquals("2023-01-01", snaps.get(2).getDate());
    }

**Test 13:** Check that each snapshot filters the entities by its own date (before the start, active, after the end).

    @Test
    void ensureSnapshotFiltersByDateCorrectly() {
        GraphRepository repo = new GraphRepository();
        repo.addAll(Collections.singletonList(
                new Person("p1", "politician", "2020-01-01", "2021-12-31", "Alice", "1980-01-01", "PT")));

        NetworkDynamicsController controller = new NetworkDynamicsController(repo);

        List<NetworkSnapshot> snaps = controller.buildSnapshots(
                Arrays.asList("2019-01-01", "2020-06-01", "2022-01-01"));

        assertEquals(0, snaps.get(0).getEntityCount()); // before start
        assertEquals(1, snaps.get(1).getEntityCount()); // active
        assertEquals(0, snaps.get(2).getEntityCount()); // after end
    }

**Test 14:** Check that a null list of dates is rejected.

    @Test
    void ensureBuildSnapshotsWithNullThrows() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertThrows(IllegalArgumentException.class, () -> controller.buildSnapshots(null));
    }

**Test 15:** Check that an empty list of dates is rejected.

    @Test
    void ensureBuildSnapshotsWithEmptyListThrows() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertThrows(IllegalArgumentException.class, () -> controller.buildSnapshots(Collections.emptyList()));
    }

**Test 16:** Check that relations are also filtered by the snapshot date.

    @Test
    void ensureEdgesAreFilteredInSnapshot() {
        GraphRepository repo = new GraphRepository();
        repo.addAll(Arrays.asList(
                new Person("p1", "politician", "", "", "Alice", "1980-01-01", "PT"),
                new Person("p2", "politician", "", "", "Bob", "1980-01-01", "PT")));
        repo.setEdges(Collections.singletonList(
                new Edge("p1", "p2", "friendOf", 1.0, "2020-01-01", "2021-12-31")));

        NetworkDynamicsController controller = new NetworkDynamicsController(repo);

        List<NetworkSnapshot> snaps = controller.buildSnapshots(
                Arrays.asList("2020-06-01", "2023-01-01"));

        assertTrue(snaps.get(0).getEdgeCount() >= 1); // active (symmetric)
        assertEquals(0, snaps.get(1).getEdgeCount()); // expired
    }


## 5. Construction (Implementation)

### Class NetworkSnapshot

Domain class that represents the state of the network at a single point in time. The static factory `of` filters the entities and edges that are active at the given date and builds the relation graph for that moment. An entity or edge with no start date is treated as always active; one with no end date is treated as still active.

```java
public static NetworkSnapshot of(String date, List<Entity> allEntities, List<Edge> allEdges) {
    if (date == null || date.isBlank()) {
        throw new IllegalArgumentException("date must not be blank");
    }
    List<Entity> activeEntities = new ArrayList<>();
    for (Entity e : allEntities) {
        if (e.isActiveAt(date)) {
            activeEntities.add(e);
        }
    }
    List<Edge> activeEdges = new ArrayList<>();
    for (Edge e : allEdges) {
        if (e.isActiveAt(date)) {
            activeEdges.add(e);
        }
    }
    RelationGraph graph = GraphBuilder.build(activeEntities, activeEdges);
    return new NetworkSnapshot(date, activeEntities, activeEdges, graph);
}
```

The getters `getActiveEntities()` and `getActiveEdges()` return defensive copies, so callers cannot change the internal state of the snapshot.

### Class NetworkDynamicsController

Application controller that produces one snapshot per requested date. It reads all entities and edges from the `GraphRepository` once and delegates the date filtering to `NetworkSnapshot.of`.

```java
public List<NetworkSnapshot> buildSnapshots(List<String> dates) {
    if (dates == null || dates.isEmpty()) {
        throw new IllegalArgumentException("At least one date must be provided.");
    }
    List<Entity> allEntities = graphRepository.getAll();
    List<Edge> allEdges = graphRepository.getEdges();
    List<NetworkSnapshot> snapshots = new ArrayList<>();
    for (String date : dates) {
        snapshots.add(NetworkSnapshot.of(date, allEntities, allEdges));
    }
    return snapshots;
}
```

The default constructor obtains the repository from the `Repositories.getInstance()` singleton; a second constructor receives it by parameter so the tests can inject an isolated repository. The helper `hasData()` lets the UI warn the user when no entities were loaded yet (US19 must run first).

### Date activity check

Both `Entity` and `Edge` expose `isActiveAt(String date)`: an element is active when the snapshot date is not before its start date and not after its end date, with blank dates treated as open ends. Dates in `yyyy-MM-dd` format compare correctly as strings, so no parsing is needed.


## 6. Integration and Demo

* An option **"Network Dynamics Over Time"** is available to every authenticated user (Citizen, Journalist, Political Agent, Ethics Committee and Administrator), as the user story is written for a generic user, both in the console UI (`NetworkDynamicsUI`) and in the GUI (`NetworkDynamics.fxml` with `NetworkDynamicsFXController`).
* The user enters one or more snapshot dates in `yyyy-MM-dd` format; the dates are sorted before the snapshots are built.
* For each date, the snapshot shows the number of active entities and relations, the entities grouped by type and the relations grouped by label.
* The entities and relations must be loaded first with US19 and US20; otherwise the UI shows a warning and stops.


## 7. Observations

* The snapshots are independent of each other: each one filters the full repository by its own date, so the same run can show the network growing and shrinking over time.
* The date format validation (`yyyy-MM-dd`) is done in the UI; the domain only rejects null or blank dates. This keeps the domain simple and the feedback close to the user.
* The snapshot also builds a `RelationGraph` with only the active elements, so future user stories can run graph algorithms on the network as it was at a given date.
