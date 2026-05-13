package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.ConflictDetector;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DetectConflictsControllerTest {

    // -----------------------------------------------------------------------
    // getAvailableQuestions
    // -----------------------------------------------------------------------

    @Test
    void ensureAtLeastFiveQuestionsAreAvailable() {
        GraphRepository repo = new GraphRepository();
        DetectConflictsController controller = new DetectConflictsController(repo);

        assertTrue(controller.getAvailableQuestions().size() >= 5);
    }

    @Test
    void ensureQuestionsListIsDefensiveCopy() {
        GraphRepository repo = new GraphRepository();
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<String> q1 = controller.getAvailableQuestions();
        q1.clear();
        assertEquals(5, controller.getAvailableQuestions().size());
    }

    // -----------------------------------------------------------------------
    // runQuery – missing graph
    // -----------------------------------------------------------------------

    @Test
    void ensureRunQueryThrowsWhenGraphIsMissing() {
        GraphRepository repo = new GraphRepository();
        DetectConflictsController controller = new DetectConflictsController(repo);

        assertThrows(IllegalStateException.class,
                () -> controller.runQuery(DetectConflictsController.QUERY_RELATIVES_IN_POSITIONS, null));
    }

    // -----------------------------------------------------------------------
    // runQuery – unknown index
    // -----------------------------------------------------------------------

    @Test
    void ensureRunQueryThrowsOnUnknownIndex() {
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(new RelationGraph());
        DetectConflictsController controller = new DetectConflictsController(repo);

        assertThrows(IllegalArgumentException.class,
                () -> controller.runQuery(999, null));
    }

    // -----------------------------------------------------------------------
    // runQuery – Q1
    // -----------------------------------------------------------------------

    @Test
    void ensureQ1ReturnsChainThroughController() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<ConflictDetector.Chain> chains =
                controller.runQuery(DetectConflictsController.QUERY_RELATIVES_IN_POSITIONS, null);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("J1", chains.get(0).getLast());
    }

    // -----------------------------------------------------------------------
    // runQuery – Q2 with organisation filter
    // -----------------------------------------------------------------------

    @Test
    void ensureQ2FiltersOrganisationThroughController() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<ConflictDetector.Chain> chains =
                controller.runQuery(DetectConflictsController.QUERY_RELATIVES_IN_ORGANISATION, "O1");

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("O1", chains.get(0).getLast());
    }

    @Test
    void ensureQ2ReturnsEmptyForWrongOrganisation() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        assertTrue(controller.runQuery(
                DetectConflictsController.QUERY_RELATIVES_IN_ORGANISATION, "O_WRONG").isEmpty());
    }

    // -----------------------------------------------------------------------
    // runQuery – Q3
    // -----------------------------------------------------------------------

    @Test
    void ensureQ3DetectsPublicOfficialInfluencingCompany() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<ConflictDetector.Chain> chains =
                controller.runQuery(DetectConflictsController.QUERY_PUBLIC_OFFICIALS_INFLUENCING, null);

        assertEquals(1, chains.size());
        assertEquals("O1", chains.get(0).getFirst());
        assertEquals("C1", chains.get(0).getLast());
    }

    // -----------------------------------------------------------------------
    // runQuery – Q4
    // -----------------------------------------------------------------------

    @Test
    void ensureQ4DetectsAssociationWithAssetOwner() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_ASSOCIATED_WITH, 1.0));
        graph.addEdge(new Edge("P2", "A1", ConflictDetector.REL_OWNER_OF, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<ConflictDetector.Chain> chains =
                controller.runQuery(DetectConflictsController.QUERY_ASSOCIATED_WITH_ASSET_OWNERS, null);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("A1", chains.get(0).getLast());
    }

    // -----------------------------------------------------------------------
    // runQuery – Q5
    // -----------------------------------------------------------------------

    @Test
    void ensureQ5DetectsAppointmentByOrgMember() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("P2", "O1", ConflictDetector.REL_MEMBER_OF, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectConflictsController controller = new DetectConflictsController(repo);

        List<ConflictDetector.Chain> chains =
                controller.runQuery(DetectConflictsController.QUERY_APPOINTED_BY_ORG_MEMBERS, null);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("O1", chains.get(0).getLast());
    }
}
