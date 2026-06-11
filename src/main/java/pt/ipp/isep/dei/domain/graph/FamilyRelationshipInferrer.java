package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Fabrication that takes a seed list of family relationships (as
 * {@link Edge}s with a {@link FamilyRelationshipType} label) and applies the
 * three closures required by US37:
 * <ul>
 *     <li>Symmetric (e.g. spouseOf)</li>
 *     <li>Inverse (e.g. childOf vs parentOf)</li>
 *     <li>Transitive (parentOf + parentOf =&gt; grandparentOf)</li>
 * </ul>
 *
 * The inferrer never modifies the seed list; it always returns a new list
 * with the inferred relationships appended.
 */
public class FamilyRelationshipInferrer {

    private FamilyRelationshipInferrer() {}

    /**
     * Applies the symmetric, inverse and transitive closures to the seed
     * relationships and returns the resulting list.
     *
     * @param seed list of seed relationships (only those whose label matches
     *             a {@link FamilyRelationshipType} are considered)
     * @return new list with the seed plus the inferred relationships
     */
    public static List<Edge> infer(List<Edge> seed) {
        if (seed == null) {
            throw new IllegalArgumentException("seed must not be null");
        }
        List<Edge> result = new ArrayList<>();
        Set<String> existing = new HashSet<>();
        for (Edge e : seed) {
            addIfNew(result, existing, e);
        }
        applySymmetricClosure(result, existing);
        applyInverseClosure(result, existing);
        applyTransitiveClosure(result, existing);
        return result;
    }

    private static void applySymmetricClosure(List<Edge> rels, Set<String> existing) {
        List<Edge> toAdd = new ArrayList<>();
        for (Edge e : rels) {
            FamilyRelationshipType type = FamilyRelationshipType.fromLabel(e.getLabel());
            if (type != null && type.isSymmetric()) {
                Edge rev = new Edge(e.getToId(), e.getFromId(), e.getLabel(), e.getWeight());
                String key = key(rev);
                if (!existing.contains(key)) {
                    toAdd.add(rev);
                    existing.add(key);
                }
            }
        }
        rels.addAll(toAdd);
    }

    private static void applyInverseClosure(List<Edge> rels, Set<String> existing) {
        List<Edge> toAdd = new ArrayList<>();
        for (Edge e : rels) {
            FamilyRelationshipType type = FamilyRelationshipType.fromLabel(e.getLabel());
            if (type != null && !type.isSymmetric()) {
                FamilyRelationshipType inverse = type.getInverse();
                Edge inv = new Edge(e.getToId(), e.getFromId(), inverse.getLabel(), e.getWeight());
                String key = key(inv);
                if (!existing.contains(key)) {
                    toAdd.add(inv);
                    existing.add(key);
                }
            }
        }
        rels.addAll(toAdd);
    }

    private static void applyTransitiveClosure(List<Edge> rels, Set<String> existing) {
        List<Edge> parents = filterByType(rels, FamilyRelationshipType.PARENT_OF);
        List<Edge> grandParents = new ArrayList<>();
        for (Edge first : parents) {
            for (Edge second : parents) {
                if (first.getToId().equals(second.getFromId())
                        && !first.getFromId().equals(second.getToId())) {
                    double weight = Math.min(first.getWeight(), second.getWeight());
                    Edge g = new Edge(first.getFromId(), second.getToId(),
                            FamilyRelationshipType.GRANDPARENT_OF.getLabel(), weight);
                    String key = key(g);
                    if (!existing.contains(key)) {
                        grandParents.add(g);
                        existing.add(key);
                    }
                }
            }
        }
        rels.addAll(grandParents);

        List<Edge> grandChildren = new ArrayList<>();
        for (Edge g : grandParents) {
            Edge inv = new Edge(g.getToId(), g.getFromId(),
                    FamilyRelationshipType.GRANDCHILD_OF.getLabel(), g.getWeight());
            String key = key(inv);
            if (!existing.contains(key)) {
                grandChildren.add(inv);
                existing.add(key);
            }
        }
        rels.addAll(grandChildren);
    }

    private static List<Edge> filterByType(List<Edge> rels, FamilyRelationshipType type) {
        List<Edge> out = new ArrayList<>();
        for (Edge e : rels) {
            if (type.getLabel().equals(e.getLabel())) {
                out.add(e);
            }
        }
        return out;
    }

    private static void addIfNew(List<Edge> list, Set<String> existing, Edge e) {
        String k = key(e);
        if (!existing.contains(k)) {
            list.add(e);
            existing.add(k);
        }
    }

    private static String key(Edge e) {
        return e.getFromId() + "|" + e.getToId() + "|" + e.getLabel();
    }
}
