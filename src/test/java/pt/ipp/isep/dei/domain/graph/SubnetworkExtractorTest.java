package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class SubnetworkExtractorTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private SupportGraph sg(String[]... edges) {
        RelationGraph rg = new RelationGraph();
        for (String[] e : edges) {
            rg.addEdge(new Edge(e[0], e[1], "rel", 1.0));
        }
        return new SupportGraph(rg);
    }

    private boolean containsId(SubnetworkExtractor.SubnetworkResult result, String id) {
        for (int i = 0; i < result.size(); i++) {
            if (result.getNodeId(i).equals(id)) return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // extract() – null / unknown guards
    // -------------------------------------------------------------------------

    @Test
    void ensureNullGraphThrows() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                SubnetworkExtractor.extract(null, "A");
            }
        });
    }

    @Test
    void ensureUnknownOriginThrows() {
        SupportGraph g = sg(new String[]{"A", "B"});
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                SubnetworkExtractor.extract(g, "Z");
            }
        });
    }

    // -------------------------------------------------------------------------
    // Single-node graph
    // -------------------------------------------------------------------------

    @Test
    void ensureSingleIsolatedNodeReturnsSubnetworkOfSizeOne() {
        RelationGraph rg = new RelationGraph();
        rg.addNode("A");
        SupportGraph g = new SupportGraph(rg);
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(1, result.size());
        assertEquals("A", result.getNodeId(0));
    }

    // -------------------------------------------------------------------------
    // Two directly connected nodes
    // -------------------------------------------------------------------------

    @Test
    void ensureBothConnectedNodesAreInSubnetwork() {
        SupportGraph g = sg(new String[]{"A", "B"});
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(2, result.size());
        assertTrue(containsId(result, "A"));
        assertTrue(containsId(result, "B"));
    }

    @Test
    void ensureSubnetworkFromBAlsoContainsBoth() {
        SupportGraph g = sg(new String[]{"A", "B"});
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "B");
        assertEquals(2, result.size());
    }

    // -------------------------------------------------------------------------
    // Linear chain
    // -------------------------------------------------------------------------

    @Test
    void ensureLinearChainAllNodesReturned() {
        // A - B - C - D
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"}
        );
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(4, result.size());
        assertTrue(containsId(result, "A"));
        assertTrue(containsId(result, "B"));
        assertTrue(containsId(result, "C"));
        assertTrue(containsId(result, "D"));
    }

    @Test
    void ensureLinearChainFromMiddleNodeAlsoReturnsAll() {
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "D"}
        );
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "B");
        assertEquals(4, result.size());
    }

    // -------------------------------------------------------------------------
    // Disconnected graph — only component of origin returned
    // -------------------------------------------------------------------------

    @Test
    void ensureIsolatedComponentNotIncluded() {
        // A - B    C - D  (two separate components)
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("C", "D", "rel", 1.0));
        SupportGraph g = new SupportGraph(rg);

        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(2, result.size());
        assertTrue(containsId(result, "A"));
        assertTrue(containsId(result, "B"));
        assertFalse(containsId(result, "C"));
        assertFalse(containsId(result, "D"));
    }

    @Test
    void ensureOriginFromSecondComponentOnlyReturnsItself() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addNode("C");
        SupportGraph g = new SupportGraph(rg);

        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "C");
        assertEquals(1, result.size());
        assertEquals("C", result.getNodeId(0));
    }

    // -------------------------------------------------------------------------
    // Subnetwork adjacency matrix correctness
    // -------------------------------------------------------------------------

    @Test
    void ensureSubAdjacencyIsSymmetric() {
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"}
        );
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        boolean[][] adj = result.getAdjacencyMatrix();
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.size(); j++) {
                assertEquals(adj[i][j], adj[j][i],
                        "Sub-adjacency matrix must be symmetric at [" + i + "][" + j + "]");
            }
        }
    }

    @Test
    void ensureSubAdjacencyReflectsOnlySubnetworkEdges() {
        // A - B - C   D (isolated)
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("B", "C", "rel", 1.0));
        rg.addNode("D");
        SupportGraph g = new SupportGraph(rg);

        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(3, result.size());

        // find indices of A, B, C in result
        int ia = -1, ib = -1, ic = -1;
        for (int i = 0; i < result.size(); i++) {
            if ("A".equals(result.getNodeId(i))) ia = i;
            if ("B".equals(result.getNodeId(i))) ib = i;
            if ("C".equals(result.getNodeId(i))) ic = i;
        }

        assertTrue(result.isAdjacent(ia, ib));
        assertTrue(result.isAdjacent(ib, ic));
        assertFalse(result.isAdjacent(ia, ic)); // A and C not directly connected
    }

    @Test
    void ensureSubAdjacencyMatrixIsDefensiveCopy() {
        SupportGraph g = sg(new String[]{"A", "B"});
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertNotSame(result.getAdjacencyMatrix(), result.getAdjacencyMatrix());
    }

    // -------------------------------------------------------------------------
    // Graph with cycle
    // -------------------------------------------------------------------------

    @Test
    void ensureCycleDoesNotCauseInfiniteLoop() {
        // A - B - C - A
        SupportGraph g = sg(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "A"}
        );
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertEquals(3, result.size());
    }

    // -------------------------------------------------------------------------
    // getNodeIds defensive copy
    // -------------------------------------------------------------------------

    @Test
    void ensureGetNodeIdsReturnsDefensiveCopy() {
        SupportGraph g = sg(new String[]{"A", "B"});
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "A");
        assertNotSame(result.getNodeIds(), result.getNodeIds());
    }

    // -------------------------------------------------------------------------
    // originId is preserved
    // -------------------------------------------------------------------------

    @Test
    void ensureOriginIdIsPreserved() {
        SupportGraph g = sg(new String[]{"A", "B"}, new String[]{"B", "C"});
        SubnetworkExtractor.SubnetworkResult result = SubnetworkExtractor.extract(g, "B");
        assertEquals("B", result.getOriginId());
    }

    // -------------------------------------------------------------------------
    // SubnetworkResult constructor guards
    // -------------------------------------------------------------------------

    @Test
    void ensureSubnetworkResultRejectsNullOrigin() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new SubnetworkExtractor.SubnetworkResult(
                            null, new String[]{"A"}, new boolean[][]{{false}});
            }
        });
    }

    @Test
    void ensureSubnetworkResultRejectsEmptyNodeIds() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new SubnetworkExtractor.SubnetworkResult(
                            "A", new String[]{}, new boolean[][]{});
            }
        });
    }

    @Test
    void ensureSubnetworkResultRejectsNullMatrix() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new SubnetworkExtractor.SubnetworkResult(
                            "A", new String[]{"A"}, null);
            }
        });
    }
}
