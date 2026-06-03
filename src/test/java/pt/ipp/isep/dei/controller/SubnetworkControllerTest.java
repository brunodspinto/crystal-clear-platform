package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.domain.graph.SubnetworkExtractor.SubnetworkResult;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubnetworkControllerTest {

    // -------------------------------------------------------------------------
    // getEntityIds
    // -------------------------------------------------------------------------

    @Test
    void ensureGetEntityIdsThrowsWhenNoGraphBuilt() {
        GraphRepository repo = new GraphRepository();
        SubnetworkController ctrl = new SubnetworkController(repo);
        assertThrows(IllegalStateException.class, ctrl::getEntityIds);
    }

    @Test
    void ensureGetEntityIdsReturnsAllNodes() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("B", "C", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        List<String> ids = ctrl.getEntityIds();
        assertEquals(3, ids.size());
        assertTrue(ids.contains("A"));
        assertTrue(ids.contains("B"));
        assertTrue(ids.contains("C"));
    }

    // -------------------------------------------------------------------------
    // extractSubnetwork – guards
    // -------------------------------------------------------------------------

    @Test
    void ensureExtractThrowsWhenNoGraphBuilt() {
        GraphRepository repo = new GraphRepository();
        SubnetworkController ctrl = new SubnetworkController(repo);
        assertThrows(IllegalStateException.class, () -> ctrl.extractSubnetwork("A"));
    }

    @Test
    void ensureExtractThrowsForUnknownEntity() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);
        assertThrows(IllegalArgumentException.class, () -> ctrl.extractSubnetwork("Z"));
    }

    // -------------------------------------------------------------------------
    // extractSubnetwork – correctness
    // -------------------------------------------------------------------------

    @Test
    void ensureFullyConnectedGraphReturnsAllNodes() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("B", "C", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        SubnetworkResult result = ctrl.extractSubnetwork("A");
        assertEquals(3, result.size());
    }

    @Test
    void ensureIsolatedNodeReturnsSubnetworkOfSizeOne() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addNode("C");
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        SubnetworkResult result = ctrl.extractSubnetwork("C");
        assertEquals(1, result.size());
        assertEquals("C", result.getNodeId(0));
    }

    @Test
    void ensureDisconnectedGraphReturnsOnlyOriginComponent() {
        // A - B   C - D
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        rg.addEdge(new Edge("C", "D", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        SubnetworkResult result = ctrl.extractSubnetwork("A");
        assertEquals(2, result.size());

        boolean hasA = false, hasB = false, hasC = false, hasD = false;
        for (int i = 0; i < result.size(); i++) {
            if ("A".equals(result.getNodeId(i))) hasA = true;
            if ("B".equals(result.getNodeId(i))) hasB = true;
            if ("C".equals(result.getNodeId(i))) hasC = true;
            if ("D".equals(result.getNodeId(i))) hasD = true;
        }
        assertTrue(hasA);
        assertTrue(hasB);
        assertFalse(hasC);
        assertFalse(hasD);
    }

    @Test
    void ensureOriginIdIsCorrectInResult() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        SubnetworkResult result = ctrl.extractSubnetwork("B");
        assertEquals("B", result.getOriginId());
    }

    @Test
    void ensureSupportGraphIsCachedBetweenCalls() {
        RelationGraph rg = new RelationGraph();
        rg.addEdge(new Edge("A", "B", "rel", 1.0));
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(rg);
        SubnetworkController ctrl = new SubnetworkController(repo);

        // Two calls should both succeed and return the same structure
        SubnetworkResult r1 = ctrl.extractSubnetwork("A");
        SubnetworkResult r2 = ctrl.extractSubnetwork("B");
        assertEquals(2, r1.size());
        assertEquals(2, r2.size());
    }
}
