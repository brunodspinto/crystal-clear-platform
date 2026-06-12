package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class NetworkSnapshotTest {

    private Person makePerson(String id, String start, String end) {
        return new Person(id, "politician", start, end, "Person " + id, "1980-01-01", "PT");
    }

    private Edge makeEdge(String from, String to, String start, String end) {
        return new Edge(from, to, "relativeOf", 1.0, start, end);
    }

    @Test
    void ensureSnapshotIncludesActiveEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "2020-01-01", "2025-12-31"));
        entities.add(makePerson("p2", "2023-01-01", ""));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, new ArrayList<>());

        assertEquals(1, snap.getEntityCount());
        assertEquals("p1", snap.getActiveEntities().get(0).getId());
    }

    @Test
    void ensureSnapshotExcludesExpiredEntities() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "2010-01-01", "2019-12-31"));

        NetworkSnapshot snap = NetworkSnapshot.of("2022-06-01", entities, new ArrayList<>());

        assertEquals(0, snap.getEntityCount());
    }

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

    @Test
    void ensureEntityWithNoDateIsAlwaysActive() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));

        NetworkSnapshot snap = NetworkSnapshot.of("2000-01-01", entities, new ArrayList<>());

        assertEquals(1, snap.getEntityCount());
    }

    @Test
    void ensureNullDateThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                NetworkSnapshot.of(null, new ArrayList<>(), new ArrayList<>());
            }
        });
    }

    @Test
    void ensureBlankDateThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                NetworkSnapshot.of("  ", new ArrayList<>(), new ArrayList<>());
            }
        });
    }

    @Test
    void ensureGetDateReturnsCorrectDate() {
        NetworkSnapshot snap = NetworkSnapshot.of("2023-05-15", new ArrayList<>(), new ArrayList<>());
        assertEquals("2023-05-15", snap.getDate());
    }

    @Test
    void ensureActiveEntitiesListIsDefensiveCopy() {
        List<Entity> entities = new ArrayList<>();
        entities.add(makePerson("p1", "", ""));
        NetworkSnapshot snap = NetworkSnapshot.of("2023-01-01", entities, new ArrayList<>());
        List<Entity> active = snap.getActiveEntities();
        active.clear();
        assertEquals(1, snap.getEntityCount());
    }
}
