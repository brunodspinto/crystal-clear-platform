package pt.ipp.isep.dei.domain.graph;

public class Person extends Entity {

    private final String name;
    private final String birthDate;
    private final String nationality;

    public Person(String id, String type, String startDate, String endDate,
                  String name, String birthDate, String nationality) {
        super(id, type, startDate, endDate);
        this.name = name == null ? "" : name;
        this.birthDate = birthDate == null ? "" : birthDate;
        this.nationality = nationality == null ? "" : nationality;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    @Override
    public String toString() {
        return "Person{id='" + getId() + "', name='" + name + "', birthDate='" + birthDate + "', nationality='" + nationality + "'}";
    }
}
