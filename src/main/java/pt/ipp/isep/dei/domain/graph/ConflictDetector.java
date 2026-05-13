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

    public static final String REL_RELATIVE_OF     = "relativeOf";
    public static final String REL_FRIEND_OF       = "friendOf";
    public static final String REL_ASSOCIATED_WITH = "associatedWith";
    public static final String REL_APPOINTED_BY    = "appointedBy";
    public static final String REL_HOLDS_POSITION  = "holdsPosition";
    public static final String REL_IN_ORGANIZATION = "inOrganization";
    public static final String REL_INFLUENCES      = "influences";
    public static final String REL_MEMBER_OF       = "memberOf";
    public static final String REL_OWNER_OF        = "ownerOf";

    /* ------------------------------------------------------------------ */
    /*  Public API                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Q1 – Persons who have a relative holding any position.
     * Chain: personA --relativeOf--> personB --holdsPosition--> position
     */
    public List<Chain> findPersonsWithRelativesInPositions(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge relEdge : graph.neighbors(personA)) {
                if (!REL_RELATIVE_OF.equals(relEdge.getLabel())) continue;
                String personB = relEdge.getToId();
                for (Edge posEdge : graph.neighbors(personB)) {
                    if (!REL_HOLDS_POSITION.equals(posEdge.getLabel())) continue;
                    String position = posEdge.getToId();
                    results.add(Chain.of(personA, personB, position));
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
     *                                                          --inOrganization--> org
     */
    public List<Chain> findPersonsWithRelativesInOrganisation(RelationGraph graph,
                                                               String organisationId) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge relEdge : graph.neighbors(personA)) {
                if (!REL_RELATIVE_OF.equals(relEdge.getLabel())) continue;
                String personB = relEdge.getToId();
                for (Edge posEdge : graph.neighbors(personB)) {
                    if (!REL_HOLDS_POSITION.equals(posEdge.getLabel())) continue;
                    String position = posEdge.getToId();
                    for (Edge orgEdge : graph.neighbors(position)) {
                        if (!REL_IN_ORGANIZATION.equals(orgEdge.getLabel())) continue;
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
     * Chain: personA --holdsPosition--> position --inOrganization--> org
     *        personA --influences--> company
     * (Both arms start at personA; the chain reported is
     * org ← position ← personA → company so first=org, last=company.)
     */
    public List<Chain> findPublicOfficialsThatInfluenceCompanies(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            List<String> publicOrgs = new ArrayList<>();
            List<String> positions  = new ArrayList<>();

            // collect all public organisations this person is linked to via a position
            for (Edge posEdge : graph.neighbors(personA)) {
                if (!REL_HOLDS_POSITION.equals(posEdge.getLabel())) continue;
                String position = posEdge.getToId();
                for (Edge orgEdge : graph.neighbors(position)) {
                    if (!REL_IN_ORGANIZATION.equals(orgEdge.getLabel())) continue;
                    publicOrgs.add(orgEdge.getToId());
                    positions.add(position);
                }
            }

            if (publicOrgs.isEmpty()) continue;

            // collect all companies this person influences
            List<String> companies = new ArrayList<>();
            for (Edge infEdge : graph.neighbors(personA)) {
                if (REL_INFLUENCES.equals(infEdge.getLabel())) {
                    companies.add(infEdge.getToId());
                }
            }

            // emit a chain for each (org, company) pair
            for (int i = 0; i < publicOrgs.size(); i++) {
                for (String company : companies) {
                    results.add(Chain.of(publicOrgs.get(i), positions.get(i),
                            personA, company));
                }
            }
        }
        return results;
    }

    /**
     * Q4 – Persons who are associated with someone who owns an asset.
     * Chain: personA --associatedWith--> personB --ownerOf--> asset
     */
    public List<Chain> findPersonsAssociatedWithAssetOwners(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge assocEdge : graph.neighbors(personA)) {
                if (!REL_ASSOCIATED_WITH.equals(assocEdge.getLabel())) continue;
                String personB = assocEdge.getToId();
                for (Edge ownEdge : graph.neighbors(personB)) {
                    if (!REL_OWNER_OF.equals(ownEdge.getLabel())) continue;
                    String asset = ownEdge.getToId();
                    results.add(Chain.of(personA, personB, asset));
                }
            }
        }
        return results;
    }

    /**
     * Q5 – Persons appointed by someone who is a member of the same
     * organisation as the appointer.
     * Chain: personA --appointedBy--> personB --memberOf--> org
     */
    public List<Chain> findPersonsAppointedByOrganisationMembers(RelationGraph graph) {
        requireGraph(graph);
        List<Chain> results = new ArrayList<>();
        for (String personA : graph.nodes()) {
            for (Edge apptEdge : graph.neighbors(personA)) {
                if (!REL_APPOINTED_BY.equals(apptEdge.getLabel())) continue;
                String personB = apptEdge.getToId();
                for (Edge memEdge : graph.neighbors(personB)) {
                    if (!REL_MEMBER_OF.equals(memEdge.getLabel())) continue;
                    String org = memEdge.getToId();
                    results.add(Chain.of(personA, personB, org));
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

    /* ------------------------------------------------------------------ */
    /*  Chain value object                                                 */
    /* ------------------------------------------------------------------ */

    /**
     * Immutable value object that holds an ordered sequence of entity ids
     * representing a detected chain. AC2 requires exposing the first and
     * last entity ids.
     */
    public static class Chain {

        private final List<String> entityIds;

        private Chain(List<String> entityIds) {
            this.entityIds = new ArrayList<>(entityIds);
        }

        /** Factory for a vararg sequence of ids (minimum 2). */
        public static Chain of(String... ids) {
            if (ids == null || ids.length < 2) {
                throw new IllegalArgumentException("a chain needs at least two entity ids");
            }
            List<String> list = new ArrayList<>();
            for (String id : ids) {
                list.add(id);
            }
            return new Chain(list);
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

        @Override
        public int hashCode() {
            return entityIds.hashCode();
        }
    }
}
