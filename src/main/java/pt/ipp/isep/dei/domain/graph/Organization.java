package pt.ipp.isep.dei.domain.graph;

public class Organization extends Entity {

    private final String name;
    private final String organizationType;
    private final String country;

    public Organization(String id, String type, String startDate, String endDate,
                        String name, String organizationType, String country) {
        super(id, type, startDate, endDate);
        this.name = name == null ? "" : name;
        this.organizationType = organizationType == null ? "" : organizationType;
        this.country = country == null ? "" : country;
    }

    public String name() {
        return name;
    }

    public String organizationType() {
        return organizationType;
    }

    public String country() {
        return country;
    }
}
