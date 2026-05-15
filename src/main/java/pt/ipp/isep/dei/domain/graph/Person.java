package pt.ipp.isep.dei.domain.graph;

/**
 * The type Person.
 */
public class Person extends Entity {

    private final String name;
    private final String birthDate;
    private final String nationality;

    /**
     * Instantiates a new Person.
     *
     * @param id          the id
     * @param type        the type
     * @param startDate   the start date
     * @param endDate     the end date
     * @param name        the name
     * @param birthDate   the birth date
     * @param nationality the nationality
     */
    public Person(String id, String type, String startDate, String endDate,
                  String name, String birthDate, String nationality) {
        super(id, type, startDate, endDate);
        this.name = name == null ? "" : name;
        this.birthDate = birthDate == null ? "" : birthDate;
        this.nationality = nationality == null ? "" : nationality;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets birth date.
     *
     * @return the birth date
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Gets nationality.
     *
     * @return the nationality
     */
    public String getNationality() {
        return nationality;
    }

    @Override
    public String toString() {
        return "Person{id='" + getId() + "', name='" + name + "', birthDate='" + birthDate + "', nationality='" + nationality + "'}";
    }
}
