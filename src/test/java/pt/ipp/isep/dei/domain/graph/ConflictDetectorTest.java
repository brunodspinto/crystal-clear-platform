package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConflictDetectorTest {

    private ConflictDetector detector;

    @BeforeEach
    void setUp() {
        detector = new ConflictDetector();
    }

    // -----------------------------------------------------------------------
    // Chain value-object tests
    // -----------------------------------------------------------------------

    @Test
    void ensureChainOfReturnsCorrectFirstAndLast() {
        ConflictDetector.Chain chain = ConflictDetector.Chain.of("A", "B", "C");
        assertEquals("A", chain.getFirst());
        assertEquals("C", chain.getLast());
    }

    @Test
    void ensureChainGetAllReturnsAllIds() {
        ConflictDetector.Chain chain = ConflictDetector.Chain.of("X", "Y", "Z");
        List<String> all = chain.getAll();
        assertEquals(3, all.size());
        assertEquals("X", all.get(0));
        assertEquals("Y", all.get(1));
        assertEquals("Z", all.get(2));
    }

    @Test
    void ensureChainOfRejectsTooFewIds() {
        assertThrows(IllegalArgumentException.class, () -> ConflictDetector.Chain.of("A"));
        assertThrows(IllegalArgumentException.class, () -> ConflictDetector.Chain.of());
        assertThrows(IllegalArgumentException.class, () -> ConflictDetector.Chain.of((String[]) null));
    }

    @Test
    void ensureChainToStringContainsArrow() {
        ConflictDetector.Chain chain = ConflictDetector.Chain.of("P1", "P2", "J1");
        assertTrue(chain.toString().contains("->"));
    }

    // -----------------------------------------------------------------------
    // Q1 – relatives in positions
    // -----------------------------------------------------------------------

    @Test
    void ensureQ1FindsChainWhenRelativeHoldsPosition() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPersonsWithRelativesInPositions(graph);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("J1", chains.get(0).getLast());
    }

    @Test
    void ensureQ1ReturnsEmptyWhenNoRelatives() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));

        assertTrue(detector.findPersonsWithRelativesInPositions(graph).isEmpty());
    }

    @Test
    void ensureQ1ReturnsEmptyWhenRelativeHasNoPosition() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));

        assertTrue(detector.findPersonsWithRelativesInPositions(graph).isEmpty());
    }

    @Test
    void ensureQ1RejectsNullGraph() {
        assertThrows(IllegalArgumentException.class,
                () -> detector.findPersonsWithRelativesInPositions(null));
    }

    // -----------------------------------------------------------------------
    // Q2 – relatives in a specific organisation
    // -----------------------------------------------------------------------

    @Test
    void ensureQ2FindsChainWhenRelativeIsInOrganisation() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPersonsWithRelativesInOrganisation(graph, "O1");

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("O1", chains.get(0).getLast());
    }

    @Test
    void ensureQ2WithBlankFilterMatchesAllOrganisations() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("J1", "O2", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPersonsWithRelativesInOrganisation(graph, null);

        assertEquals(2, chains.size());
    }

    @Test
    void ensureQ2FiltersOutNonMatchingOrganisation() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_RELATIVE_OF, 1.0));
        graph.addEdge(new Edge("P2", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        assertTrue(detector.findPersonsWithRelativesInOrganisation(graph, "O_OTHER").isEmpty());
    }

    // -----------------------------------------------------------------------
    // Q3 – public officials influencing companies
    // -----------------------------------------------------------------------

    @Test
    void ensureQ3FindsChainWhenPublicOfficialInfluencesCompany() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPublicOfficialsThatInfluenceCompanies(graph);

        assertEquals(1, chains.size());
    }

    @Test
    void ensureQ3ChainFirstIsPersonLastIsCompany() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        ConflictDetector.Chain chain =
                detector.findPublicOfficialsThatInfluenceCompanies(graph).get(0);

        assertEquals("P1", chain.getFirst());
        assertEquals("C1", chain.getLast());
    }

    @Test
    void ensureQ3ChainHasTwoEntities() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        ConflictDetector.Chain chain =
                detector.findPublicOfficialsThatInfluenceCompanies(graph).get(0);

        assertEquals(2, chain.getAll().size());
    }

    @Test
    void ensureQ3ChainContextContainsOrgAndPosition() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        ConflictDetector.Chain chain =
                detector.findPublicOfficialsThatInfluenceCompanies(graph).get(0);

        assertTrue(chain.hasContext());
        assertTrue(chain.getContext().contains("J1"));
        assertTrue(chain.getContext().contains("O1"));
    }

    @Test
    void ensureQ3ReturnsEmptyWhenNoInfluenceEdge() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "J1", ConflictDetector.REL_HOLDS_POSITION, 1.0));
        graph.addEdge(new Edge("J1", "O1", ConflictDetector.REL_IN_ORGANIZATION, 1.0));

        assertTrue(detector.findPublicOfficialsThatInfluenceCompanies(graph).isEmpty());
    }

    @Test
    void ensureQ3ReturnsEmptyWhenNoPositionEdge() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "C1", ConflictDetector.REL_INFLUENCES, 1.0));

        assertTrue(detector.findPublicOfficialsThatInfluenceCompanies(graph).isEmpty());
    }

    // -----------------------------------------------------------------------
    // Q4 – associated with asset owners
    // -----------------------------------------------------------------------

    @Test
    void ensureQ4FindsChainWhenAssociateOwnsAsset() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_ASSOCIATED_WITH, 1.0));
        graph.addEdge(new Edge("P2", "A1", ConflictDetector.REL_OWNER_OF, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPersonsAssociatedWithAssetOwners(graph);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("A1", chains.get(0).getLast());
    }

    @Test
    void ensureQ4ReturnsEmptyWhenAssociateOwnsNothing() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_ASSOCIATED_WITH, 1.0));

        assertTrue(detector.findPersonsAssociatedWithAssetOwners(graph).isEmpty());
    }

    // -----------------------------------------------------------------------
    // Q5 – appointed by organisation member
    // -----------------------------------------------------------------------

    @Test
    void ensureQ5FindsChainWhenAppointedByOrgMember() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_APPOINTED_BY, 1.0));
        graph.addEdge(new Edge("P2", "O1", ConflictDetector.REL_MEMBER_OF, 1.0));

        List<ConflictDetector.Chain> chains =
                detector.findPersonsAppointedByOrganisationMembers(graph);

        assertEquals(1, chains.size());
        assertEquals("P1", chains.get(0).getFirst());
        assertEquals("O1", chains.get(0).getLast());
    }

    @Test
    void ensureQ5ReturnsEmptyWhenAppointingPersonIsNotOrgMember() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("P1", "P2", ConflictDetector.REL_APPOINTED_BY, 1.0));

        assertTrue(detector.findPersonsAppointedByOrganisationMembers(graph).isEmpty());
    }

}
