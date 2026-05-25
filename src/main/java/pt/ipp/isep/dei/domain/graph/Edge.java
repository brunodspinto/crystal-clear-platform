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
    private final String startDate;
    private final String endDate;

    /**
     * Instantiates a new Edge.
     *
     * @param fromId the from id
     * @param toId   the to id
     * @param label  the label
     * @param weight the weight
     */
    public Edge(String fromId, String toId, String label, double weight) {
        this(fromId, toId, label, weight, "", "");
    }

    /**
     * Instantiates a new Edge with temporal bounds.
     *
     * @param fromId    the from id
     * @param toId      the to id
     * @param label     the label
     * @param weight    the weight
     * @param startDate the start date (yyyy-MM-dd), or blank if unknown
     * @param endDate   the end date (yyyy-MM-dd), or blank if still active
     */
    public Edge(String fromId, String toId, String label, double weight,
                String startDate, String endDate) {
        this.fromId = requireNonBlank(fromId, "fromId");
        this.toId = requireNonBlank(toId, "toId");
        this.label = requireNonBlank(label, "label");
        this.weight = weight;
        this.startDate = startDate == null ? "" : startDate.trim();
        this.endDate = endDate == null ? "" : endDate.trim();
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

    /**
     * Gets start date.
     *
     * @return the start date (yyyy-MM-dd), or blank if unset
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date (yyyy-MM-dd), or blank if still active
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Returns true if this edge is active at the given date.
     * An edge with no startDate is assumed to have always existed;
     * an edge with no endDate is assumed to still be active.
     *
     * @param date the snapshot date in yyyy-MM-dd format
     * @return true if active at that date
     */
    public boolean isActiveAt(String date) {
        if (date == null || date.isBlank()) return false;
        boolean startOk = startDate.isEmpty() || startDate.compareTo(date) <= 0;
        boolean endOk = endDate.isEmpty() || endDate.compareTo(date) >= 0;
        return startOk && endOk;
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
