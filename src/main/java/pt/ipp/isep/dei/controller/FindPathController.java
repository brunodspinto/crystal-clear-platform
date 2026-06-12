package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.PathFinder;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.domain.graph.SupportGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US34 – Verify if there is a pathway between two entities
 * in the support graph and, if so, return the distance between them.
 *
 * <p>The support graph is built on demand from the directed
 * {@link RelationGraph} stored in the repository. It is cached in this
 * controller instance so repeated queries within the same UI session do not
 * rebuild it each time.</p>
 */
public class FindPathController {

    private final GraphRepository graphRepository;
    private SupportGraph supportGraph;

    /**
     * Creates a controller using the singleton repository.
     */
    public FindPathController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository. Used in tests.
     *
     * @param graphRepository the graph repository
     */
    public FindPathController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the ordered list of all entity ids known to the support graph.
     * Used by the UI to display a selection list to the user.
     *
     * @return list of entity ids; never null
     * @throws IllegalStateException if no relations graph has been built yet
     */
    public List<String> getEntityIds() {
        SupportGraph graph = getSupportGraph();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < graph.size(); i++) {
            ids.add(graph.getRegistry().idAt(i));
        }
        return ids;
    }

    /**
     * Verifies whether a pathway exists between {@code sourceId} and
     * {@code targetId} in the support graph and returns the shortest distance.
     *
     * @param sourceId the id of the source entity
     * @param targetId the id of the target entity
     * @return a {@link PathResult} containing reachability and distance
     * @throws IllegalStateException    if no relations graph has been built yet
     * @throws IllegalArgumentException if either id is unknown
     */
    public PathResult findPath(String sourceId, String targetId) {
        SupportGraph graph = getSupportGraph();
        int distance = PathFinder.shortestDistance(graph, sourceId, targetId);
        return new PathResult(sourceId, targetId, distance);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    /** Lazily builds and caches the support graph. */
    private SupportGraph getSupportGraph() {
        if (supportGraph == null) {
            RelationGraph relationGraph = graphRepository.getRelationGraph();
            if (relationGraph == null) {
                throw new IllegalStateException(
                        "No relations graph available. Build the graph first (US20).");
            }
            supportGraph = new SupportGraph(relationGraph);
        }
        return supportGraph;
    }

    // -------------------------------------------------------------------------
    // PathResult value object
    // -------------------------------------------------------------------------

    /**
     * Immutable result of a path query.
     */
    public static class PathResult {

        private final String sourceId;
        private final String targetId;
        private final int distance;

        /**
         * @param sourceId the source entity id
         * @param targetId the target entity id
         * @param distance shortest distance, or {@link PathFinder#NO_PATH} if unreachable
         */
        public PathResult(String sourceId, String targetId, int distance) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.distance = distance;
        }

        /** @return true when a path exists between source and target */
        public boolean hasPath() {
            return distance != PathFinder.NO_PATH;
        }

        /** @return shortest distance in edges, or {@link PathFinder#NO_PATH} if none */
        public int getDistance() {
            return distance;
        }

        /** @return the source entity id */
        public String getSourceId() {
            return sourceId;
        }

        /** @return the target entity id */
        public String getTargetId() {
            return targetId;
        }
    }
}
