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

    public String getPositionTitle() {
        return positionTitle;
    }

    public String getPositionType() {
        return positionType;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public String toString() {
        return "Position{id='" + getId() + "', positionTitle='" + positionTitle + "', positionType='" + positionType + "', organizationId='" + organizationId + "'}";
    }
}
