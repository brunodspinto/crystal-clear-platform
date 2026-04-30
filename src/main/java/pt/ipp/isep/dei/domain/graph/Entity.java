package pt.ipp.isep.dei.domain.graph;

public abstract class Entity {

    private final String id;
    private final String type;
    private final String startDate;
    private final String endDate;

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

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStartDate() {
        return startDate;
    }

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
