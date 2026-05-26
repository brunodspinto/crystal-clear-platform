package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;
import pt.ipp.isep.dei.domain.graph.Person;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NetworkDynamicsControllerTest {

    private GraphRepository repoWithPerson(String id, String start, String end) {
        GraphRepository repo = new GraphRepository();
        repo.addAll(Collections.singletonList(
                new Person(id, "politician", start, end, "Person " + id, "1980-01-01", "PT")));
        return repo;
    }

    @Test
    void ensureHasDataReturnsFalseWhenEmpty() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertFalse(controller.hasData());
    }

    @Test
    void ensureHasDataReturnsTrueWhenEntitiesLoaded() {
        NetworkDynamicsController controller = new NetworkDynamicsController(
                repoWithPerson("p1", "", ""));
        assertTrue(controller.hasData());
    }

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

    @Test
    void ensureBuildSnapshotsWithNullThrows() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertThrows(IllegalArgumentException.class, () -> controller.buildSnapshots(null));
    }

    @Test
    void ensureBuildSnapshotsWithEmptyListThrows() {
        NetworkDynamicsController controller = new NetworkDynamicsController(new GraphRepository());
        assertThrows(IllegalArgumentException.class, () -> controller.buildSnapshots(Collections.emptyList()));
    }

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
}
