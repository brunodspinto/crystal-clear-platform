package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationGraphTest {

    @Test
    void ensureAddEdgeStoresIt() {
        RelationGraph g = new RelationGraph();
        g.addEdge(new Edge("A", "B", "ownership", 1.0));

        assertEquals(1, g.neighbors("A").size());
        assertEquals("B", g.neighbors("A").get(0).toId());
    }

    @Test
    void ensureUnknownNodeReturnsEmpty() {
        RelationGraph g = new RelationGraph();
        assertTrue(g.neighbors("xpto").isEmpty());
    }

    @Test
    void ensureNodesContainsBothEnds() {
        RelationGraph g = new RelationGraph();
        g.addEdge(new Edge("A", "B", "kinship", 1.0));
        g.addEdge(new Edge("B", "C", "employment", 1.0));

        assertEquals(3, g.nodeCount());
        assertTrue(g.nodes().containsAll(java.util.List.of("A", "B", "C")));
    }

    @Test
    void ensureNullEdgeIsRejected() {
        RelationGraph g = new RelationGraph();
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(null));
    }
}
