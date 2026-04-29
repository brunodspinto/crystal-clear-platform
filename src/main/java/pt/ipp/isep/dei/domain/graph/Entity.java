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

    public String id() {
        return id;
    }

    public String type() {
        return type;
    }

    public String startDate() {
        return startDate;
    }

    public String endDate() {
        return endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entity)) {
            return false;
        }
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }

}
