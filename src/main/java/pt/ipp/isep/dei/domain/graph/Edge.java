package pt.ipp.isep.dei.domain.graph;

/**
 * A relation between two entities, identified by their ids.
 *
 * The label is kept as a free-form string for now. Once US19's entity model
 * lands and the relation taxonomy stabilises, this will likely become an enum
 * (kinship, employment, ownership, membership, ...).
 */
public class Edge {

    private final String fromId;
    private final String toId;
    private final String label;
    private final double weight;

    public Edge(String fromId, String toId, String label, double weight) {
        this.fromId = requireNonBlank(fromId, "fromId");
        this.toId = requireNonBlank(toId, "toId");
        this.label = requireNonBlank(label, "label");
        this.weight = weight;
    }

    public String fromId() {
        return fromId;
    }

    public String toId() {
        return toId;
    }

    public String label() {
        return label;
    }

    public double weight() {
        return weight;
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge other)) return false;
        return Double.compare(weight, other.weight) == 0
                && fromId.equals(other.fromId)
                && toId.equals(other.toId)
                && label.equals(other.label);
    }

}
