package pt.ipp.isep.dei.domain.graph;

/**
 * The type Entity.
 */
public abstract class Entity {

    private final String id;
    private final String type;
    private final String startDate;
    private final String endDate;

    /**
     * Instantiates a new Entity.
     *
     * @param id        the id
     * @param type      the type
     * @param startDate the start date
     * @param endDate   the end date
     */
    protected Entity(String id, String type, String startDate, String endDate) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        this.id = id;
        this.type = type;
        this.startDate = startDate == null ? "" : startDate;
        this.endDate = endDate == null ? "" : endDate;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{id='" + id + "', type='" + type + "', startDate='" + startDate + "', endDate='" + endDate + "'}";
    }

}
