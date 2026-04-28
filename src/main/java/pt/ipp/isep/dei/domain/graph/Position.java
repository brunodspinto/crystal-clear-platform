package pt.ipp.isep.dei.domain.graph;

public class Position extends Entity {

    private final String positionTitle;
    private final String positionType;
    private final String organizationId;

    public Position(String id, String type, String startDate, String endDate,
                    String positionTitle, String positionType, String organizationId) {
        super(id, type, startDate, endDate);
        this.positionTitle = positionTitle == null ? "" : positionTitle;
        this.positionType = positionType == null ? "" : positionType;
        this.organizationId = organizationId == null ? "" : organizationId;
    }

    public String positionTitle() {
        return positionTitle;
    }

    public String positionType() {
        return positionType;
    }

    public String organizationId() {
        return organizationId;
    }
}
