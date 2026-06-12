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
}
