package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.NepotismDetector;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DetectNepotismControllerTest {

    // -----------------------------------------------------------------------
    // detect() – missing graph
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectThrowsWhenGraphIsMissing() {
        GraphRepository repo = new GraphRepository();
        DetectNepotismController controller = new DetectNepotismController(repo);

        assertThrows(IllegalStateException.class, controller::detect);
    }

    // -----------------------------------------------------------------------
    // detect() – empty graph
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsEmptyForEmptyGraph() {
        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(new RelationGraph());
        DetectNepotismController controller = new DetectNepotismController(repo);

        assertTrue(controller.detect().isEmpty());
    }

    // -----------------------------------------------------------------------
    // detect() – single pair found
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsPairThroughController() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectNepotismController controller = new DetectNepotismController(repo);

        List<NepotismDetector.NepotismPair> pairs = controller.detect();

        assertEquals(1, pairs.size());
        assertEquals("A", pairs.get(0).getAppointer());
        assertEquals("B", pairs.get(0).getAppointed());
        assertEquals(NepotismDetector.REL_RELATIVE_OF, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – friend-of trigger
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectFlagsFriendAppointment() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_FRIEND_OF, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectNepotismController controller = new DetectNepotismController(repo);

        List<NepotismDetector.NepotismPair> pairs = controller.detect();

        assertEquals(1, pairs.size());
        assertEquals(NepotismDetector.REL_FRIEND_OF, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – associatedWith trigger
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectFlagsAssociateAppointment() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_ASSOCIATED_WITH, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectNepotismController controller = new DetectNepotismController(repo);

        List<NepotismDetector.NepotismPair> pairs = controller.detect();

        assertEquals(1, pairs.size());
        assertEquals(NepotismDetector.REL_ASSOCIATED_WITH, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – all pairs returned (AC1)
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsAllPairsAC1() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("D", "C", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("C", "D", NepotismDetector.REL_FRIEND_OF, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectNepotismController controller = new DetectNepotismController(repo);

        assertEquals(2, controller.detect().size());
    }

    // -----------------------------------------------------------------------
    // detect() – unrelated appointment not flagged
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectIgnoresAppointmentWithNoPersonalTie() {
        RelationGraph graph = new RelationGraph();
        // only an appointment, no personal tie
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        DetectNepotismController controller = new DetectNepotismController(repo);

        assertTrue(controller.detect().isEmpty());
    }
}
