package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.ConflictDetector;
import pt.ipp.isep.dei.domain.graph.ConflictDetector.Chain;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Detect conflicts controller.
 */
public class DetectConflictsController {

    /* ------------------------------------------------------------------ */
    /*  Question catalogue (AC1 – at least 5)                             */
    /* ------------------------------------------------------------------ */

    /**
     * The constant QUERY_RELATIVES_IN_POSITIONS.
     */
    public static final int QUERY_RELATIVES_IN_POSITIONS        = 0;
    /**
     * The constant QUERY_RELATIVES_IN_ORGANISATION.
     */
    public static final int QUERY_RELATIVES_IN_ORGANISATION     = 1;
    /**
     * The constant QUERY_PUBLIC_OFFICIALS_INFLUENCING.
     */
    public static final int QUERY_PUBLIC_OFFICIALS_INFLUENCING  = 2;
    /**
     * The constant QUERY_ASSOCIATED_WITH_ASSET_OWNERS.
     */
    public static final int QUERY_ASSOCIATED_WITH_ASSET_OWNERS  = 3;
    /**
     * The constant QUERY_APPOINTED_BY_ORG_MEMBERS.
     */
    public static final int QUERY_APPOINTED_BY_ORG_MEMBERS      = 4;

    private static final String[] QUESTION_LABELS = {
        "Which individuals have relatives who hold prominent positions?",
        "Which individuals have relatives in a specific organisation?",
        "Which individuals in public organisations influence companies?",
        "Which individuals are associated with asset owners?",
        "Which individuals were appointed by organization members?"
    };

    /* ------------------------------------------------------------------ */
    /*  Dependencies                                                       */
    /* ------------------------------------------------------------------ */

    private final GraphRepository graphRepository;
    private final ConflictDetector detector;

    /**
     * Instantiates a new Detect conflicts controller.
     */
    public DetectConflictsController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
        this.detector = new ConflictDetector();
    }

    /**
     * Constructor used in unit tests to inject dependencies.  @param graphRepository the graph repository
     *
     * @param graphRepository the graph repository
     */
    public DetectConflictsController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
        this.detector = new ConflictDetector();
    }

    /* ------------------------------------------------------------------ */
    /*  Public API                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Returns a copy of the question catalogue.
     * The returned list index corresponds to the {@code queryIndex} parameter
     * of {@link #runQuery(int, String)}.
     *
     * @return the available questions
     */
    public List<String> getAvailableQuestions() {
        List<String> list = new ArrayList<>();
        for (String q : QUESTION_LABELS) {
            list.add(q);
        }
        return list;
    }

    /**
     * Runs the selected query and returns the detected chains.
     *
     * @param queryIndex     index into {@link #getAvailableQuestions()}
     * @param organisationId only used by {@link #QUERY_RELATIVES_IN_ORGANISATION};                        pass null or blank to match any organisation
     * @return list of detected chains (may be empty)
     * @throws IllegalStateException    if no relations graph has been built yet
     * @throws IllegalArgumentException if {@code queryIndex} is out of range
     */
    public List<Chain> runQuery(int queryIndex, String organisationId) {
        RelationGraph graph = graphRepository.getRelationGraph();
        if (graph == null) {
            throw new IllegalStateException(
                    "No relations graph available. Build the graph first (US20).");
        }
        switch (queryIndex) {
            case QUERY_RELATIVES_IN_POSITIONS:
                return detector.findPersonsWithRelativesInPositions(graph);
            case QUERY_RELATIVES_IN_ORGANISATION:
                return detector.findPersonsWithRelativesInOrganisation(graph, organisationId);
            case QUERY_PUBLIC_OFFICIALS_INFLUENCING:
                return detector.findPublicOfficialsThatInfluenceCompanies(graph);
            case QUERY_ASSOCIATED_WITH_ASSET_OWNERS:
                return detector.findPersonsAssociatedWithAssetOwners(graph);
            case QUERY_APPOINTED_BY_ORG_MEMBERS:
                return detector.findPersonsAppointedByOrganisationMembers(graph);
            default:
                throw new IllegalArgumentException("Unknown query index: " + queryIndex);
        }
    }
}
