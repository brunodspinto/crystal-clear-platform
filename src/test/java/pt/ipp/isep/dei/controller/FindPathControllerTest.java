package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.PathFinder;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class FindPathControllerTest {

    // -------------------------------------------------------------------------
    // getEntityIds
    // -------------------------------------------------------------------------

    @Test
    void ensureGetEntityIdsThrowsWhenNoGraphBuilt() {
        GraphRepository repo = new GraphRepository();
        FindPathController ctrl = new FindPathController(repo);
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ctrl.getEntityIds();
            }
        });
    }

    @Test
    void ensureGetEntityIdsReturnsAllNodes() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("B", "C", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        List<String> ids = ctrl.getEntityIds();
        assertEquals(3, ids.size());
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));
        assertTrue(ids.contains("C"));
    }

    // -------------------------------------------------------------------------
    // findPath — guards
    // -------------------------------------------------------------------------

    @Test
    void ensureFindPathThrowsWhenNoGraphBuilt() {
        GraphRepository repo = new GraphRepository();
        FindPathController ctrl = new FindPathController(repo);
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ctrl.findPath("A", "B");
            }
        });
    }

    @Test
    void ensureFindPathThrowsForUnknownSource() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ctrl.findPath("Z", "B");
            }
        });
    }

    @Test
    void ensureFindPathThrowsForUnknownTarget() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                ctrl.findPath("A", "Z");
            }
        });
    }

    // -------------------------------------------------------------------------
    // findPath — PathResult
    // -------------------------------------------------------------------------

    @Test
    void ensureSameEntityReturnsDistanceZero() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("A", "A");
        assertTrue(result.hasPath());
        assertEquals(0, result.getDistance());
    }

    @Test
    void ensureDirectlyAdjacentReturnsDistanceOne() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("A", "B");
        assertTrue(result.hasPath());
        assertEquals(1, result.getDistance());
    }

    @Test
    void ensureUndirectedPathFoundFromTarget() {
        // directed edge A→B; support graph makes it bidirectional
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("B", "A");
        assertTrue(result.hasPath());
        assertEquals(1, result.getDistance());
    }

    @Test
    void ensurePathOfLengthTwoIsReturned() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("B", "C", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("A", "C");
        assertTrue(result.hasPath());
        assertEquals(2, result.getDistance());
    }

    @Test
    void ensureNoPathReturnsFalse() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addNode("C");
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("A", "C");
        assertFalse(result.hasPath());
        assertEquals(PathFinder.NO_PATH, result.getDistance());
    }

    @Test
    void ensurePathResultStoresCorrectIds() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        FindPathController ctrl = new FindPathController(repo);

        FindPathController.PathResult result = ctrl.findPath("A", "B");
        assertEquals("A", result.getSourceId());
        assertEquals("B", result.getTargetId());
    }

    // -------------------------------------------------------------------------
    // findPath(date, ...) — temporal snapshot (US32 integration)
    // -------------------------------------------------------------------------

    /** Repo with A and B connected only by an edge active from 2015 onwards. */
    private GraphRepository temporalRepo() {
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(new RelationGraph()); // marks the graph as built
        repo.addAll(java.util.Arrays.asList(
                new pt.ipp.isep.dei.domain.graph.Person("A", "person", "2009-01-01", "2027-01-01", "A", "", ""),
                new pt.ipp.isep.dei.domain.graph.Person("B", "person", "2009-01-01", "2027-01-01", "B", "", "")));
        repo.setEdges(java.util.Arrays.asList(
                new Edge("A", "B", "friendOf", 1.0, "2015-01-01", "2027-01-01")));
        return repo;
    }

    @Test
    void ensureNoPathBeforeTheEdgeIsActive() {
        FindPathController ctrl = new FindPathController(temporalRepo());
        FindPathController.PathResult result = ctrl.findPath("2010-01-01", "A", "B");
        assertFalse(result.hasPath());
    }

    @Test
    void ensurePathExistsOnceTheEdgeIsActive() {
        FindPathController ctrl = new FindPathController(temporalRepo());
        FindPathController.PathResult result = ctrl.findPath("2020-01-01", "A", "B");
        assertTrue(result.hasPath());
        assertEquals(1, result.getDistance());
    }

    @Test
    void ensureGetEntityIdsByDateExcludesInactiveEntities() {
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(new RelationGraph());
        repo.addAll(java.util.Arrays.asList(
                new pt.ipp.isep.dei.domain.graph.Person("A", "person", "2009-01-01", "2027-01-01", "A", "", ""),
                new pt.ipp.isep.dei.domain.graph.Person("B", "person", "2020-01-01", "2027-01-01", "B", "", "")));
        repo.setEdges(new java.util.ArrayList<Edge>());
        FindPathController ctrl = new FindPathController(repo);

        assertEquals(1, ctrl.getEntityIds("2015-01-01").size()); // só A activo
        assertEquals(2, ctrl.getEntityIds("2021-01-01").size()); // A e B activos
    }

    @Test
    void ensureFindPathRejectsBlankDate() {
        FindPathController ctrl = new FindPathController(temporalRepo());
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                ctrl.findPath("  ", "A", "B");
            }
        });
    }
}
