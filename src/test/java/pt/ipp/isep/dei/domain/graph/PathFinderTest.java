package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class PathFinderTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Builds a SupportGraph from a sequence of directed edges. */
    private SupportGraph sg(String[]... edges) {
        RelationGraph rg = new RelationGraph();
        for (String[] e : edges) {
            rg.addEdge(new Edge(e[0], e[1], "rel", 1.0));
        }
        return new SupportGraph(rg);
    }

    // -------------------------------------------------------------------------
    // Null / unknown guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullGraphThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                PathFinder.shortestDistance(null, "A", "B");
            }
        });
    }

    @Test
    void ensureUnknownSourceThrows() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                PathFinder.shortestDistance(g, "Z", "B");
            }
        });
    }

    @Test
    void ensureUnknownTargetThrows() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                PathFinder.shortestDistance(g, "A", "Z");
            }
        });
    }

    // -------------------------------------------------------------------------
    // Distance 0 — same entity
    // -------------------------------------------------------------------------

    @Test
    void ensureSameEntityReturnsZero() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertEquals(0, PathFinder.shortestDistance(g, "A", "A"));
    }

    @Test
    void ensureHasPathReturnsTrueForSameEntity() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertTrue(PathFinder.hasPath(g, "A", "A"));
    }

    // -------------------------------------------------------------------------
    // Distance 1 — directly adjacent
    // -------------------------------------------------------------------------

    @Test
    void ensureDirectlyAdjacentReturnsOne() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertEquals(1, PathFinder.shortestDistance(g, "A", "B"));
    }

    @Test
    void ensureReverseDirectlyAdjacentReturnsOne() {
        // support graph is undirected — B→A same as A→B
        SupportGraph g = sg(new String[]{"A", "B"});
        assertEquals(1, PathFinder.shortestDistance(g, "B", "A"));
    }

    // -------------------------------------------------------------------------
    // Distance 2
    // -------------------------------------------------------------------------

    @Test
    void ensurePathOfLengthTwoIsFound() {
        // A - B - C
        SupportGraph g = sg(new String[]{"A", "B"}, new String[]{"B", "C"});
        assertEquals(2, PathFinder.shortestDistance(g, "A", "C"));
    }

    @Test
    void ensurePathOfLengthTwoIsFoundInReverseDirection() {
        SupportGraph g = sg(new String[]{"A", "B"}, new String[]{"B", "C"});
        assertEquals(2, PathFinder.shortestDistance(g, "C", "A"));
    }

    // -------------------------------------------------------------------------
    // Shortest path chosen — BFS correctness
    // -------------------------------------------------------------------------

    @Test
    void ensureShortestPathChosenWhenMultiplePaths() {
        // A - B - C
        //  \     /
        //   D - E
        // Direct: A→B→C = distance 2
        // Long:   A→D→E→C = distance 3
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"A", "D"},
                new String[]{"D", "E"},
                new String[]{"E", "C"}
        );
        assertEquals(2, PathFinder.shortestDistance(g, "A", "C"));
    }

    // -------------------------------------------------------------------------
    // No path
    // -------------------------------------------------------------------------

    @Test
    void ensureNoPathReturnsMinusOne() {
        // A - B    C (isolated)
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addNode("C");
        SupportGraph g = new SupportGraph(rg);
        assertEquals(PathFinder.NO_PATH, PathFinder.shortestDistance(g, "A", "C"));
    }

    @Test
    void ensureHasPathReturnsFalseWhenNoPath() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addNode("C");
        SupportGraph g = new SupportGraph(rg);
        assertFalse(PathFinder.hasPath(g, "A", "C"));
    }

    @Test
    void ensureHasPathReturnsTrueWhenPathExists() {
        SupportGraph g = sg(new String[]{"A", "B"}, new String[]{"B", "C"});
        assertTrue(PathFinder.hasPath(g, "A", "C"));
    }

    // -------------------------------------------------------------------------
    // Longer chains
    // -------------------------------------------------------------------------

    @Test
    void ensureDistanceThreeIsCorrect() {
        // A - B - C - D
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"}
        );
        assertEquals(3, PathFinder.shortestDistance(g, "A", "D"));
    }

    @Test
    void ensureLinearChainOfFiveNodesReturnsCorrectDistance() {
        // A - B - C - D - E
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"},
                new String[]{"D", "E"}
        );
        assertEquals(4, PathFinder.shortestDistance(g, "A", "E"));
    }

    // -------------------------------------------------------------------------
    // Graph with cycle
    // -------------------------------------------------------------------------

    @Test
    void ensureCycleDoesNotCauseInfiniteLoop() {
        // A - B - C - A (cycle)
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "A"}
        );
        assertEquals(1, PathFinder.shortestDistance(g, "A", "B"));
        assertEquals(1, PathFinder.shortestDistance(g, "A", "C"));
    }

    // -------------------------------------------------------------------------
    // Real-data scenario with mixed labels
    // -------------------------------------------------------------------------

    @Test
    void ensurePathFoundAcrossMultipleLabelTypes() {
        // P1 --holdsPosition--> C1 --inOrganization--> O1
        // P2 --influences--> O1
        // Path P1 to P2: P1 - C1 - O1 - P2 (distance 3)
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("P1", "C1", "holdsPosition",  1.0));
        rg.addEdge(new Edge("C1", "O1", "inOrganization", 1.0));
        rg.addEdge(new Edge("P2", "O1", "influences",     1.0));
        SupportGraph g = new SupportGraph(rg);
        assertEquals(3, PathFinder.shortestDistance(g, "P1", "P2"));
    }
}
