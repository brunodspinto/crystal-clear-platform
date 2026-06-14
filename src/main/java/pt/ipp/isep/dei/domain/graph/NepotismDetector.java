package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects <em>direct</em> nepotism in a {@link RelationGraph} for US22.
 *
 * <p>Direct nepotism occurs when person B was appointed by person A, and A and B
 * share at least one personal relationship: {@code relativeOf}, {@code friendOf},
 * or {@code associatedWith}. AC1 requires that <strong>all</strong> such pairs
 * be listed.</p>
 *
 * <p>Personal-tie edges ({@code relativeOf}, {@code friendOf},
 * {@code associatedWith}) are guaranteed to exist in both directions by
 * {@code GraphBuilder}, so only the forward direction is checked.
 * The {@code appointedBy} edge is directional: "B appointedBy A" means
 * A appointed B.</p>
 */
public class NepotismDetector {

    /* ------------------------------------------------------------------ */
    /*  Relation-label constants                                           */
    /* ------------------------------------------------------------------ */

    /**
     * The constant REL_APPOINTED_BY.
     */
    public static final String REL_APPOINTED_BY    = "appointedBy";
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

    /* ------------------------------------------------------------------ */
    /*  Public API                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Returns every pair (appointer, appointed) where the appointer and the
     * appointed share a personal relationship (relative, friend, or associate).
     *
     * <p>The returned {@link NepotismPair} always stores the appointer as
     * {@code personA} and the appointed as {@code personB}, together with the
     * personal-relationship label that triggered the detection.</p>
     *
     * @param graph the relation graph to analyse; must not be null
     * @return list of detected pairs, possibly empty
     * @throws IllegalArgumentException if {@code graph} is null
     */
    public List<NepotismPair> detect(RelationGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph must not be null");
        }

        List<NepotismPair> results = new ArrayList<>();

        // Iterate every node looking for outgoing "appointedBy" edges.
        // Edge direction: appointed --appointedBy--> appointer
        for (String appointed : graph.nodes()) {
            for (Edge apptEdge : graph.neighbors(appointed)) {
                if (REL_APPOINTED_BY.equals(apptEdge.getLabel())) {
                    String appointer = apptEdge.getToId();

                    // GraphBuilder guarantees symmetric ties are stored in both
                    // directions, so checking only appointer→appointed is sufficient.
                    String tie = findPersonalTie(graph, appointer, appointed);
                    if (tie != null) {
                        results.add(new NepotismPair(appointer, appointed, tie));
                    }
                }
            }
        }

        return results;
    }

    /* ------------------------------------------------------------------ */
    /*  Internal helpers                                                   */
    /* ------------------------------------------------------------------ */

    /**
     * Returns the label of the first personal-relationship edge found from
     * {@code a} to {@code b}, or {@code null} if none exists.
     *
     * <p>Only the forward direction {@code a→b} is checked because
     * {@code GraphBuilder} ensures symmetric labels are stored in both
     * directions before the graph reaches this detector.</p>
     */
    private String findPersonalTie(RelationGraph graph, String a, String b) {
        for (Edge edge : graph.neighbors(a)) {
            if (b.equals(edge.getToId()) && isPersonalTie(edge.getLabel())) {
                return edge.getLabel();
            }
        }
        return null;
    }

    private boolean isPersonalTie(String label) {
        return REL_RELATIVE_OF.equals(label)
                || REL_FRIEND_OF.equals(label)
                || REL_ASSOCIATED_WITH.equals(label);
    }

    /* ------------------------------------------------------------------ */
    /*  NepotismPair value object                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Immutable record of a detected nepotism pair.
     *
     * <ul>
     *   <li>{@code appointer} – the person who did the appointing</li>
     *   <li>{@code appointed} – the person who was appointed</li>
     *   <li>{@code relationshipLabel} – the personal tie that makes this
     *       nepotism ({@code relativeOf}, {@code friendOf}, or
     *       {@code associatedWith})</li>
     * </ul>
     */
    public static class NepotismPair {

        private final String appointer;
        private final String appointed;
        private final String relationshipLabel;

        /**
         * Instantiates a new Nepotism pair.
         *
         * @param appointer         the appointer
         * @param appointed         the appointed
         * @param relationshipLabel the relationship label
         */
        public NepotismPair(String appointer, String appointed, String relationshipLabel) {
            if (appointer == null || appointer.isBlank()) {
                throw new IllegalArgumentException("appointer must not be blank");
            }
            if (appointed == null || appointed.isBlank()) {
                throw new IllegalArgumentException("appointed must not be blank");
            }
            if (relationshipLabel == null || relationshipLabel.isBlank()) {
                throw new IllegalArgumentException("relationshipLabel must not be blank");
            }
            this.appointer = appointer;
            this.appointed = appointed;
            this.relationshipLabel = relationshipLabel;
        }

        /**
         * Gets appointer.
         *
         * @return the person who performed the appointment
         */
        public String getAppointer() {
            return appointer;
        }

        /**
         * Gets appointed.
         *
         * @return the person who was appointed
         */
        public String getAppointed() {
            return appointed;
        }

        /**
         * Gets relationship label.
         *
         * @return the personal-relationship label that flags this as nepotism         ({@code relativeOf}, {@code friendOf}, or {@code associatedWith})
         */
        public String getRelationshipLabel() {
            return relationshipLabel;
        }

        @Override
        public String toString() {
            return "NepotismPair{"
                    + "appointer='" + appointer + '\''
                    + ", appointed='" + appointed + '\''
                    + ", relationship='" + relationshipLabel + '\''
                    + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NepotismPair other = (NepotismPair) o;
            return appointer.equals(other.appointer)
                    && appointed.equals(other.appointed)
                    && relationshipLabel.equals(other.relationshipLabel);
        }
    }
}
