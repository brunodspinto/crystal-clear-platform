package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixTest {

    @Test
    void ensureSizeIsKept() {
        AdjacencyMatrix m = new AdjacencyMatrix(5);
        assertEquals(5, m.getSize());
    }

    @Test
    void ensureNonPositiveSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AdjacencyMatrix(0));
        assertThrows(IllegalArgumentException.class, () -> new AdjacencyMatrix(-1));
    }

    @Test
    void ensureNewMatrixHasNoEdges() {
        AdjacencyMatrix m = new AdjacencyMatrix(3);
        assertFalse(m.hasEdge(0, 1));
        assertEquals(0.0, m.getWeight(0, 1));
    }

    @Test
    void ensureAddEdgeStoresWeight() {
        AdjacencyMatrix m = new AdjacencyMatrix(3);
        m.addEdge(0, 2, 0.75);
        assertTrue(m.hasEdge(0, 2));
        assertEquals(0.75, m.getWeight(0, 2));
    }
}
