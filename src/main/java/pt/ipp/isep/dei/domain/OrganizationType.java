package pt.ipp.isep.dei.domain;

public enum OrganizationType {
    COMPANY("Company"),
    POLITICAL_PARTY("Political Party"),
    FOUNDATION("Foundation"),
    INSTITUTE("Institute"),
    ASSOCIATION("Association");

    private final String label;

    OrganizationType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
