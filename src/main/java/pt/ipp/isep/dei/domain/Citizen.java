package pt.ipp.isep.dei.domain;

public class Citizen extends User {

    public Citizen(String email, String name) {
        super(name, email);
    }

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
        return new Citizen(email, name);
    }
}
