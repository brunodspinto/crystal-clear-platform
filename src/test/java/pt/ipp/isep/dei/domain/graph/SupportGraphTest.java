package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class SupportGraphTest {

    // -------------------------------------------------------------------------
    // Construction guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullRelationGraphThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new SupportGraph(null);
            }
        });
    }

    @Test
    void ensureEmptyRelationGraphProducesEmptySupportGraph() {
        SupportGraph sg = new SupportGraph(new RelationGraph());
        assertEquals(0, sg.size());
    }

    // -------------------------------------------------------------------------
    // Node registration
    // -------------------------------------------------------------------------

    @Test
    void ensureSingleEdgeRegistersBothNodes() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "relativeOf", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertEquals(2, sg.size());
    }

    @Test
    void ensureIsolatedNodeFromAddNodeIsRegistered() {
        RelationGraph rg = new RelationGraph();
        rg.addNode("X");
        SupportGraph sg = new SupportGraph(rg);
        assertEquals(1, sg.size());
    }

    // -------------------------------------------------------------------------
    // isAdjacent — basic adjacency
    // -------------------------------------------------------------------------

    @Test
    void ensureDirectedEdgeCreatesUndirectedAdjacency() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "holdsPosition", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertTrue(sg.isAdjacent("A", "B"));
    }

    @Test
    void ensureDirectedEdgeIsSymmetricInSupportGraph() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "holdsPosition", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        // B→A must also be true even though the directed edge is only A→B
        assertTrue(sg.isAdjacent("B", "A"));
    }

    @Test
    void ensureNonAdjacentNodesReturnFalse() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "holdsPosition", 1.0));
        rg.addNode("C");
        SupportGraph sg = new SupportGraph(rg);
        assertFalse(sg.isAdjacent("A", "C"));
        assertFalse(sg.isAdjacent("C", "B"));
    }

    @Test
    void ensureSelfLoopIsIgnored() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "A", "selfRef", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertFalse(sg.isAdjacent("A", "A"));
    }

    // -------------------------------------------------------------------------
    // isAdjacent — label independence
    // -------------------------------------------------------------------------

    @Test
    void ensureDifferentLabelsAllCreateAdjacency() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "relativeOf",    1.0));
        rg.addEdge(new Edge("A", "C", "holdsPosition", 1.0));
        rg.addEdge(new Edge("A", "D", "influences",    1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertTrue(sg.isAdjacent("A", "B"));
        assertTrue(sg.isAdjacent("A", "C"));
        assertTrue(sg.isAdjacent("A", "D"));
    }

    @Test
    void ensureMultipleEdgesBetweenSamePairDoNotBreakAdjacency() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "relativeOf",    1.0));
        rg.addEdge(new Edge("A", "B", "holdsPosition", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertTrue(sg.isAdjacent("A", "B"));
        assertTrue(sg.isAdjacent("B", "A"));
    }

    // -------------------------------------------------------------------------
    // isAdjacent — unknown id guard
    // -------------------------------------------------------------------------

    @Test
    void ensureIsAdjacentWithUnknownSourceThrows() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "relativeOf", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                sg.isAdjacent("Z", "B");
            }
        });
    }

    @Test
    void ensureIsAdjacentWithUnknownTargetThrows() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "relativeOf", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                sg.isAdjacent("A", "Z");
            }
        });
    }

    // -------------------------------------------------------------------------
    // adjacencyRow
    // -------------------------------------------------------------------------

    @Test
    void ensureAdjacencyRowHasCorrectLength() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("A", "C", "rel", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertEquals(3, sg.adjacencyRow("A").length);
    }

    @Test
    void ensureAdjacencyRowIsDefensiveCopy() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        boolean[] row1 = sg.adjacencyRow("A");
        boolean[] row2 = sg.adjacencyRow("A");
        assertNotSame(row1, row2);
    }

    // -------------------------------------------------------------------------
    // getAdjacencyMatrix
    // -------------------------------------------------------------------------

    @Test
    void ensureGetAdjacencyMatrixIsDefensiveCopy() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        assertNotSame(sg.getAdjacencyMatrix(), sg.getAdjacencyMatrix());
    }

    @Test
    void ensureGetAdjacencyMatrixIsSymmetric() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        SupportGraph sg = new SupportGraph(rg);
        boolean[][] m = sg.getAdjacencyMatrix();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m.length; j++) {
                assertEquals(m[i][j], m[j][i],
                        "Matrix must be symmetric at [" + i + "][" + j + "]");
            }
        }
    }
}
