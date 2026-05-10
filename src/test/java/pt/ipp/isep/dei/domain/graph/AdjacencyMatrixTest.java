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

    @Test
    void ensureMultiplyComputesPathsOfLengthTwo() {
        // 0 -> 1 -> 2 with weights 2 and 3 → result[0][2] should be 6
        AdjacencyMatrix m = new AdjacencyMatrix(3);
        m.addEdge(0, 1, 2.0);
        m.addEdge(1, 2, 3.0);

        AdjacencyMatrix squared = m.multiply(m);

        assertEquals(6.0, squared.getWeight(0, 2));
        assertEquals(0.0, squared.getWeight(0, 1));
        assertEquals(0.0, squared.getWeight(1, 0));
    }

    @Test
    void ensureMultiplyRejectsNull() {
        AdjacencyMatrix m = new AdjacencyMatrix(2);
        assertThrows(IllegalArgumentException.class, () -> m.multiply(null));
    }

    @Test
    void ensureMultiplyRejectsDifferentSize() {
        AdjacencyMatrix small = new AdjacencyMatrix(2);
        AdjacencyMatrix big = new AdjacencyMatrix(3);
        assertThrows(IllegalArgumentException.class, () -> small.multiply(big));
    }

    @Test
    void ensureTransposeSwapsRowsAndColumns() {
        AdjacencyMatrix m = new AdjacencyMatrix(3);
        m.addEdge(0, 1, 5.0);
        m.addEdge(1, 2, 7.0);

        AdjacencyMatrix t = m.transpose();

        assertEquals(5.0, t.getWeight(1, 0));
        assertEquals(7.0, t.getWeight(2, 1));
        assertEquals(0.0, t.getWeight(0, 1));
    }

    @Test
    void ensureTransposeOfTransposeIsOriginal() {
        AdjacencyMatrix m = new AdjacencyMatrix(3);
        m.addEdge(0, 2, 1.5);
        m.addEdge(2, 1, 9.0);

        AdjacencyMatrix tt = m.transpose().transpose();

        assertEquals(1.5, tt.getWeight(0, 2));
        assertEquals(9.0, tt.getWeight(2, 1));
    }
}
