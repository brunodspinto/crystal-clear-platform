package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides indirect conflict-of-interest and nepotism detection queries
 * for US23. Each query returns a list of {@link Chain} objects, where every
 * chain exposes its first and last entity id as required by AC2.
 *
 * <p>All queries operate on the {@link RelationGraph} stored in the repository.
 * The graph is treated as <em>directed</em>: an edge A→B with label L means
 * "A has relation L with B".</p>
 *
 * <p>Symmetric labels ({@code relativeOf}, {@code friendOf}, {@code associatedWith})
 * are guaranteed to exist in both directions by {@code GraphBuilder}, so all
 * queries use plain outgoing-edge traversal without defensive reverse checks.</p>
 *
 * <p>Supported queries (AC1 – at least 5):</p>
 * <ol>
 *   <li>Q1 – Persons who have a relative holding any position
 *       (chain: person → relative → position)</li>
 *   <li>Q2 – Persons who have a relative holding a position in a given
 *       organisation (chain: person → relative → position → organisation)</li>
 *   <li>Q3 – Persons holding a position in a public organisation who also
 *       influence a company
 *       (chain: person → position[public] → organisation ← influence ← person)</li>
 *   <li>Q4 – Persons who are associated with someone who owns an asset
 *       (chain: person → associatedWith → person → ownerOf → asset)</li>
 *   <li>Q5 – Persons appointed by someone who is a member of the same
 *       organisation (chain: person → appointedBy → person → memberOf → organisation)</li>
 * </ol>
 */
public class ConflictDetector {

    /* ------------------------------------------------------------------ */
    /*  Relation-label constants (must match the values used in the CSV)   */
    /* ------------------------------------------------------------------ */

    /**
     * The constant REL_RELATIVE_OF.
     */
    public static final String REL_RELATIVE_OF     = "relativeOf";
    /**
     * The constant REL_FRIEND_OF.
     */
    public static final String REL_FRIEND_OF       = "friendOf";
    /**
     * The constant REL_ASSOCIATED_WITH.
     */
    public static final String REL_ASSOCIATED_WITH = "associatedWith";
    /**
     * The constant REL_APPOINTED_BY.
     */
    public static final String REL_APPOINTED_BY    = "appointedBy";
    /**
     * The constant REL_HOLDS_POSITION.
     */
    public static final String REL_HOLDS_POSITION  = "holdsPosition";
    /**
     * The constant REL_IN_ORGANIZATION.
     */
    public static final String REL_IN_ORGANIZATION = "inOrganization";
    /**
     * The constant REL_INFLUENCES.
     */
    public static final String REL_INFLUENCES      = "influences";
    /**
     * The constant REL_MEMBER_OF.
     */
    public static final String REL_MEMBER_OF       = "memberOf";
    /**
     * The constant REL_OWNER_OF.
     */
    public static final String REL_OWNER_OF        = "ownerOf";

    /* ------------------------------------------------------------------ */
    /*  Public API                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Q1 – Persons who have a relative holding any position.
     * Chain: personA --relativeOf--> personB --holdsPosition--> position
     *
     * @param graph the graph
     * @return the list
     */
    public List<Chain> findPersonsWithRelativesInPositions(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge relEdge : edgesWithLabel(graph, personA, REL_RELATIVE_OF)) {
                String personB = relEdge.getToId();
                for (Edge posEdge : edgesWithLabel(graph, personB, REL_HOLDS_POSITION)) {
                    results.add(Chain.of(personA, personB, posEdge.getToId()));
                }
            }
        }
        return results;
    }

    /**
     * Q2 – Persons who have a relative holding a position in a specific
     * organisation (the organisation id is supplied as a filter; pass null or
     * blank to match any organisation).
     * Chain: personA --relativeOf--> personB --holdsPosition--> position
     * --inOrganization--> org
     *
     * @param graph          the graph
     * @param organisationId the organisation id
     * @return the list
     */
    public List<Chain> findPersonsWithRelativesInOrganisation(RelationGraph graph,
                                                               String organisationId) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge relEdge : edgesWithLabel(graph, personA, REL_RELATIVE_OF)) {
                String personB = relEdge.getToId();
                for (Edge posEdge : edgesWithLabel(graph, personB, REL_HOLDS_POSITION)) {
                    String position = posEdge.getToId();
                    for (Edge orgEdge : edgesWithLabel(graph, position, REL_IN_ORGANIZATION)) {
                        String org = orgEdge.getToId();
                        if (organisationId == null || organisationId.isBlank()
                                || org.equals(organisationId)) {
                            results.add(Chain.of(personA, personB, position, org));
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * Q3 – Persons holding a position in a public organisation who also
     * influence a company.
     *
     * <p>The chain reflects the actual graph relation: {@code personA --influences--&gt; company}.
     * The public organisation (and position) are pre-conditions that qualify the person
     * for this query; they are attached as context on each chain so the UI can display
     * them without polluting the chain semantics.</p>
     *
     * Chain: personA → company  (first = person, last = company)
     * Context: "holds [position] in [org]"
     *
     * @param graph the graph
     * @return the list
     */
    public List<Chain> findPublicOfficialsThatInfluenceCompanies(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            List<String> publicOrgs = new ArrayList<>();
            List<String> positions  = new ArrayList<>();

            for (Edge posEdge : edgesWithLabel(graph, personA, REL_HOLDS_POSITION)) {
                String position = posEdge.getToId();
                for (Edge orgEdge : edgesWithLabel(graph, position, REL_IN_ORGANIZATION)) {
                    publicOrgs.add(orgEdge.getToId());
                    positions.add(position);
                }
            }

            if (publicOrgs.isEmpty()) continue;

            for (Edge infEdge : edgesWithLabel(graph, personA, REL_INFLUENCES)) {
                String company = infEdge.getToId();
                for (int i = 0; i < publicOrgs.size(); i++) {
                    String ctx = "holds " + positions.get(i) + " in " + publicOrgs.get(i);
                    results.add(Chain.ofWithContext(ctx, personA, company));
                }
            }
        }
        return results;
    }

    /**
     * Q4 – Persons who are associated with someone who owns an asset.
     * Chain: personA --associatedWith--> personB --ownerOf--> asset
     *
     * @param graph the graph
     * @return the list
     */
    public List<Chain> findPersonsAssociatedWithAssetOwners(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge assocEdge : edgesWithLabel(graph, personA, REL_ASSOCIATED_WITH)) {
                String personB = assocEdge.getToId();
                for (Edge ownEdge : edgesWithLabel(graph, personB, REL_OWNER_OF)) {
                    results.add(Chain.of(personA, personB, ownEdge.getToId()));
                }
            }
        }
        return results;
    }

    /**
     * Q5 – Persons appointed by someone who holds a position in an organisation.
     *
     * <p>In this data model, a person's membership in an organisation is expressed
     * via an intermediate position node:
     * {@code personB --holdsPosition--&gt; position --inOrganization--&gt; org}.
     * There is no direct {@code memberOf} edge from person to organisation.</p>
     *
     * Chain: personA → personB → org  (first = appointed person, last = organisation)
     * Context: "appointer holds [position] in [org]"
     *
     * @param graph the graph
     * @return the list
     */
    public List<Chain> findPersonsAppointedByOrganisationMembers(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge apptEdge : edgesWithLabel(graph, personA, REL_APPOINTED_BY)) {
                String personB = apptEdge.getToId();
                for (Edge posEdge : edgesWithLabel(graph, personB, REL_HOLDS_POSITION)) {
                    String position = posEdge.getToId();
                    for (Edge orgEdge : edgesWithLabel(graph, position, REL_IN_ORGANIZATION)) {
                        String org = orgEdge.getToId();
                        String ctx = "appointer holds " + position + " in " + org;
                        results.add(Chain.ofWithContext(ctx, personA, personB, org));
                    }
                }
            }
        }
        return results;
    }

    /* ------------------------------------------------------------------ */
    /*  Internal helpers                                                   */
    /* ------------------------------------------------------------------ */

    private static void requireGraph(RelationGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }
    }

    /**
     * Returns all outgoing edges from {@code nodeId} with the given label.
     * Use for asymmetric labels where direction is meaningful
     * ({@code holdsPosition}, {@code inOrganization}, {@code appointedBy},
     * {@code influences}, {@code memberOf}, {@code ownerOf}).
     */
    private static List<Edge> edgesWithLabel(RelationGraph graph, String nodeId, String label) {
        List<Edge> result = new ArrayList<>();
        for (Edge edge : graph.neighbors(nodeId)) {
            if (label.equals(edge.getLabel())) {
                result.add(edge);
            }
        }
        return result;
    }

    /* ------------------------------------------------------------------ */
    /*  Chain value object                                                 */
    /* ------------------------------------------------------------------ */

    /**
     * Immutable value object that holds an ordered sequence of entity ids
     * representing a detected chain. AC2 requires exposing the first and
     * last entity ids.
     *
     * <p>An optional {@code context} string may be attached to provide
     * human-readable information about the pre-conditions that triggered the
     * query (e.g. which public organisation a person belongs to in Q3).
     * The context is not part of the chain itself and does not affect
     * {@code getFirst()}, {@code getLast()}, or {@code equals()}.</p>
     */
    public static class Chain {

        private final List<String> entityIds;
        private final String context;

        private Chain(List<String> entityIds, String context) {
            this.entityIds = new ArrayList<>(entityIds);
            this.context   = context;
        }

        /** Factory for a vararg sequence of ids (minimum 2), no context. */
        public static Chain of(String... ids) {
            if (ids == null || ids.length < 2) {
                throw new IllegalArgumentException("a chain needs at least two entity ids");
            }
            List<String> list = new ArrayList<>();
            for (String id : ids) {
                list.add(id);
            }
            return new Chain(list, null);
        }

        /**
         * Factory for a vararg sequence of ids with an attached context string.
         *
         * @param context human-readable pre-condition info shown alongside the chain
         * @param ids     at least two entity ids
         * @return the chain
         */
        public static Chain ofWithContext(String context, String... ids) {
            if (ids == null || ids.length < 2) {
                throw new IllegalArgumentException("a chain needs at least two entity ids");
            }
            List<String> list = new ArrayList<>();
            for (String id : ids) {
                list.add(id);
            }
            return new Chain(list, context);
        }

        /** @return the id of the first entity in the chain (AC2). */
        public String getFirst() {
            return entityIds.get(0);
        }

        /** @return the id of the last entity in the chain (AC2). */
        public String getLast() {
            return entityIds.get(entityIds.size() - 1);
        }

        /** @return all entity ids in the chain, in order. */
        public List<String> getAll() {
            return new ArrayList<>(entityIds);
        }

        /**
         * Returns optional context about the pre-conditions that triggered this
         * chain, or {@code null} if none was provided.
         *
         * @return context string or null
         */
        public String getContext() {
            return context;
        }

        /** @return true if this chain has an attached context string. */
        public boolean hasContext() {
            return context != null && !context.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < entityIds.size(); i++) {
                if (i > 0) sb.append(" -> ");
                sb.append(entityIds.get(i));
            }
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Chain other = (Chain) o;
            return entityIds.equals(other.entityIds);
        }
    }
}
