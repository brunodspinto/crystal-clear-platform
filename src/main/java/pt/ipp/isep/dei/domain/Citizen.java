package pt.ipp.isep.dei.domain;

/**
 * The type Citizen.
 */
public class Citizen extends User {

    private final String nationalIdCardNumber;

    /**
     * Instantiates a new Citizen.
     *
     * @param email                the email
     * @param name                 the name
     * @param nationalIdCardNumber the national id card number
     */
    public Citizen(String email, String name, String nationalIdCardNumber) {
        super(name, email);
        if (nationalIdCardNumber == null || nationalIdCardNumber.isBlank())
            throw new IllegalArgumentException("National ID card number cannot be null or blank");
        this.nationalIdCardNumber = nationalIdCardNumber;
    }

    /**
     * Gets national id card number.
     *
     * @return the national id card number
     */
    public String getNationalIdCardNumber() { return nationalIdCardNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Citizen)) return false;
        Citizen that = (Citizen) o;
        return email.equalsIgnoreCase(that.email);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, email);
    }

    public Citizen clone() {
        return new Citizen(email, name, nationalIdCardNumber);
    }
}
