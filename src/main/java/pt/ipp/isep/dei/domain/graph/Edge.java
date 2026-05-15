package pt.ipp.isep.dei.domain.graph;

/**
 * A relation between two entities, identified by their ids.
 * <p>
 * The label is kept as a free-form string for now. Once US19's entity model
 * lands and the relation taxonomy stabilises, this will likely become an enum
 * (kinship, employment, ownership, membership, ...).
 */
public class Edge {

    private final String fromId;
    private final String toId;
    private final String label;
    private final double weight;

    /**
     * Instantiates a new Edge.
     *
     * @param fromId the from id
     * @param toId   the to id
     * @param label  the label
     * @param weight the weight
     */
    public Edge(String fromId, String toId, String label, double weight) {
        this.fromId = requireNonBlank(fromId, "fromId");
        this.toId = requireNonBlank(toId, "toId");
        this.label = requireNonBlank(label, "label");
        this.weight = weight;
    }

    /**
     * Gets from id.
     *
     * @return the from id
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * Gets to id.
     *
     * @return the to id
     */
    public String getToId() {
        return toId;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets weight.
     *
     * @return the weight
     */
    public double getWeight() {
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
        if (o == null || getClass() != o.getClass()) return false;
        Edge other = (Edge) o;
        return Double.compare(weight, other.weight) == 0
                && fromId.equals(other.fromId)
                && toId.equals(other.toId)
                && label.equals(other.label);
    }

    @Override
    public String toString() {
        return "Edge{fromId='" + fromId + "', toId='" + toId + "', label='" + label + "', weight=" + weight + "}";
    }

}
