package pt.ipp.isep.dei.dto;

/**
 * Data Transfer Object (DTO) carrying the data of a single asset entry to show
 * in the assets consultation (US11), decoupling the UI from the domain
 * {@code AssetEntry} (ESOFT &mdash; DTO pattern). The UI uses
 * {@code sensitiveDetail} to know whether the detail line must be masked for
 * non-journalist users (AC1/AC4). It holds no business logic.
 */
public class AssetEntryDTO {

    private final String type;
    private final double value;
    private final String detail;
    private final boolean sensitiveDetail;

    /**
     * Creates an asset entry DTO.
     *
     * @param type            the asset type as text.
     * @param value           the declared asset value.
     * @param detail          the display line with the asset details (may be empty).
     * @param sensitiveDetail whether the detail must be masked for non-journalists.
     */
    public AssetEntryDTO(String type, double value, String detail, boolean sensitiveDetail) {
        this.type = type;
        this.value = value;
        this.detail = detail;
        this.sensitiveDetail = sensitiveDetail;
    }

    /**
     * @return the asset type as text.
     */
    public String getType() {
        return type;
    }

    /**
     * @return the declared asset value.
     */
    public double getValue() {
        return value;
    }

    /**
     * @return the display line with the asset details (may be empty).
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @return whether the detail must be masked for non-journalist users.
     */
    public boolean isSensitiveDetail() {
        return sensitiveDetail;
    }
}
