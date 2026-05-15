package pt.ipp.isep.dei.domain.graph;

/**
 * The type Position.
 */
public class Position extends Entity {

    private final String positionTitle;
    private final String positionType;
    private final String organizationId;

    /**
     * Instantiates a new Position.
     *
     * @param id             the id
     * @param type           the type
     * @param startDate      the start date
     * @param endDate        the end date
     * @param positionTitle  the position title
     * @param positionType   the position type
     * @param organizationId the organization id
     */
    public Position(String id, String type, String startDate, String endDate,
                    String positionTitle, String positionType, String organizationId) {
        super(id, type, startDate, endDate);
        this.positionTitle = positionTitle == null ? "" : positionTitle;
        this.positionType = positionType == null ? "" : positionType;
        this.organizationId = organizationId == null ? "" : organizationId;
    }

    /**
     * Gets position title.
     *
     * @return the position title
     */
    public String getPositionTitle() {
        return positionTitle;
    }

    /**
     * Gets position type.
     *
     * @return the position type
     */
    public String getPositionType() {
        return positionType;
    }

    /**
     * Gets organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    @Override
    public String toString() {
        return "Position{id='" + getId() + "', positionTitle='" + positionTitle + "', positionType='" + positionType + "', organizationId='" + organizationId + "'}";
    }
}
