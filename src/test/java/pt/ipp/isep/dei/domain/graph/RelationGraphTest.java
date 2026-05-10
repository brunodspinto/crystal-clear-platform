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

    @Test
    void ensureToAdjacencyMatrixCopiesEdgeWeights() {
        RelationGraph g = new RelationGraph();
        g.addEdge(new Edge("A", "B", "ownership", 0.5));
        g.addEdge(new Edge("B", "C", "ownership", 0.8));
        IndexRegistry r = new IndexRegistry();

        AdjacencyMatrix m = g.toAdjacencyMatrix(r);

        int a = r.indexFor("A");
        int b = r.indexFor("B");
        int c = r.indexFor("C");
        assertEquals(0.5, m.getWeight(a, b));
        assertEquals(0.8, m.getWeight(b, c));
        assertEquals(0.0, m.getWeight(a, c));
    }

    @Test
    void ensureToAdjacencyMatrixWithLabelFiltersEdges() {
        RelationGraph g = new RelationGraph();
        g.addEdge(new Edge("A", "B", "ownership", 0.5));
        g.addEdge(new Edge("A", "B", "kinship", 0.9));
        IndexRegistry r = new IndexRegistry();

        AdjacencyMatrix m = g.toAdjacencyMatrix("kinship", r);

        int a = r.indexFor("A");
        int b = r.indexFor("B");
        assertEquals(0.9, m.getWeight(a, b));
    }

    @Test
    void ensureToAdjacencyMatrixRejectsNullRegistry() {
        RelationGraph g = new RelationGraph();
        assertThrows(IllegalArgumentException.class, () -> g.toAdjacencyMatrix(null));
    }

    @Test
    void ensureToAdjacencyMatrixWithLabelRejectsBlankLabel() {
        RelationGraph g = new RelationGraph();
        IndexRegistry r = new IndexRegistry();
        assertThrows(IllegalArgumentException.class, () -> g.toAdjacencyMatrix("", r));
        assertThrows(IllegalArgumentException.class, () -> g.toAdjacencyMatrix(null, r));
    }

    @Test
    void ensureAddNodeRegistersIsolatedNode() {
        RelationGraph g = new RelationGraph();
        g.addNode("X");
        assertTrue(g.nodes().contains("X"));
        assertTrue(g.neighbors("X").isEmpty());
    }

    @Test
    void ensureAddNodeIsIdempotent() {
        RelationGraph g = new RelationGraph();
        g.addNode("X");
        g.addEdge(new Edge("X", "Y", "kinship", 1.0));
        g.addNode("X");
        assertEquals(1, g.neighbors("X").size());
    }

    @Test
    void ensureAddNodeRejectsBlankId() {
        RelationGraph g = new RelationGraph();
        assertThrows(IllegalArgumentException.class, () -> g.addNode(null));
        assertThrows(IllegalArgumentException.class, () -> g.addNode(""));
        assertThrows(IllegalArgumentException.class, () -> g.addNode("   "));
    }
}
