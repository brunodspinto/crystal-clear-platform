package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class NepotismDetectorTest {

    private NepotismDetector detector;

    @BeforeEach
    void setUp() {
        detector = new NepotismDetector();
    }

    // -----------------------------------------------------------------------
    // NepotismPair value-object tests
    // -----------------------------------------------------------------------

    @Test
    void ensureNepotismPairStoresFields() {
        NepotismDetector.NepotismPair pair =
                new NepotismDetector.NepotismPair("A", "B", NepotismDetector.REL_RELATIVE_OF);
        assertEquals("A", pair.getAppointer());
        assertEquals("B", pair.getAppointed());
        assertEquals(NepotismDetector.REL_RELATIVE_OF, pair.getRelationshipLabel());
    }

    @Test
    void ensureNepotismPairRejectsBlankAppointer() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair("", "B", NepotismDetector.REL_RELATIVE_OF);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair(null, "B", NepotismDetector.REL_RELATIVE_OF);
            }
        });
    }

    @Test
    void ensureNepotismPairRejectsBlankAppointed() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair("A", "", NepotismDetector.REL_RELATIVE_OF);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair("A", null, NepotismDetector.REL_RELATIVE_OF);
            }
        });
    }

    @Test
    void ensureNepotismPairRejectsBlankLabel() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair("A", "B", "");
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new NepotismDetector.NepotismPair("A", "B", null);
            }
        });
    }

    @Test
    void ensureNepotismPairEqualityConsidersAllFields() {
        NepotismDetector.NepotismPair p1 =
                new NepotismDetector.NepotismPair("A", "B", NepotismDetector.REL_RELATIVE_OF);
        NepotismDetector.NepotismPair p2 =
                new NepotismDetector.NepotismPair("A", "B", NepotismDetector.REL_RELATIVE_OF);
        NepotismDetector.NepotismPair p3 =
                new NepotismDetector.NepotismPair("A", "B", NepotismDetector.REL_FRIEND_OF);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    void ensureNepotismPairToStringContainsIds() {
        NepotismDetector.NepotismPair pair =
                new NepotismDetector.NepotismPair("P1", "P2", NepotismDetector.REL_RELATIVE_OF);
        String s = pair.toString();
        assertTrue(s.contains("P1"));
        assertTrue(s.contains("P2"));
        assertTrue(s.contains(NepotismDetector.REL_RELATIVE_OF));
    }

    // -----------------------------------------------------------------------
    // detect() – null guard
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectRejectsNullGraph() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                detector.detect(null);
            }
        });
    }

    // -----------------------------------------------------------------------
    // detect() – empty graph
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsEmptyForEmptyGraph() {
        assertTrue(detector.detect(new RelationGraph()).isEmpty());
    }

    // -----------------------------------------------------------------------
    // detect() – relativeOf trigger
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectFindsPairWhenAppointedByRelative() {
        RelationGraph graph = new RelationGraph();
        // B was appointed by A; A and B are relatives
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));

        List<NepotismDetector.NepotismPair> pairs = detector.detect(graph);

        assertEquals(1, pairs.size());
        assertEquals("A", pairs.get(0).getAppointer());
        assertEquals("B", pairs.get(0).getAppointed());
        assertEquals(NepotismDetector.REL_RELATIVE_OF, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – friendOf trigger
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectFindsPairWhenAppointedByFriend() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_FRIEND_OF, 1.0));

        List<NepotismDetector.NepotismPair> pairs = detector.detect(graph);

        assertEquals(1, pairs.size());
        assertEquals(NepotismDetector.REL_FRIEND_OF, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – associatedWith trigger
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectFindsPairWhenAppointedByAssociate() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_ASSOCIATED_WITH, 1.0));

        List<NepotismDetector.NepotismPair> pairs = detector.detect(graph);

        assertEquals(1, pairs.size());
        assertEquals(NepotismDetector.REL_ASSOCIATED_WITH, pairs.get(0).getRelationshipLabel());
    }

    // -----------------------------------------------------------------------
    // detect() – no nepotism when there is no personal tie
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsEmptyWhenNoPersonalTie() {
        RelationGraph graph = new RelationGraph();
        // B was appointed by A, but they share no personal relationship
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "C", NepotismDetector.REL_RELATIVE_OF, 1.0));

        assertTrue(detector.detect(graph).isEmpty());
    }

    // -----------------------------------------------------------------------
    // detect() – no nepotism when there is no appointment
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsEmptyWhenNoAppointmentEdge() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));

        assertTrue(detector.detect(graph).isEmpty());
    }

    // -----------------------------------------------------------------------
    // detect() – multiple pairs all returned (AC1)
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectReturnsAllPairs() {
        RelationGraph graph = new RelationGraph();
        // pair 1: A appointed B (relative)
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));
        // pair 2: C appointed D (friend)
        graph.addEdge(new Edge("D", "C", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("C", "D", NepotismDetector.REL_FRIEND_OF, 1.0));

        List<NepotismDetector.NepotismPair> pairs = detector.detect(graph);

        assertEquals(2, pairs.size());
    }

    // -----------------------------------------------------------------------
    // detect() – unrelated appointment is not flagged
    // -----------------------------------------------------------------------

    @Test
    void ensureDetectDoesNotFlagUnrelatedAppointment() {
        RelationGraph graph = new RelationGraph();
        // A appointed B (relatives) — nepotism
        graph.addEdge(new Edge("B", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("A", "B", NepotismDetector.REL_RELATIVE_OF, 1.0));
        // A also appointed E — not a relative/friend/associate
        graph.addEdge(new Edge("E", "A", NepotismDetector.REL_APPOINTED_BY, 1.0));

        List<NepotismDetector.NepotismPair> pairs = detector.detect(graph);

        assertEquals(1, pairs.size());
        assertEquals("B", pairs.get(0).getAppointed());
    }
}
