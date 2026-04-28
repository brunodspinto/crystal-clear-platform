package pt.ipp.isep.dei.domain;

import java.util.Objects;

/**
 * Represents a citizen who can submit complaints about political agents.
 */
public class Citizen {
    private final String email;
    private final String name;

    /**
     * Creates a new Citizen.
     *
     * @param email the citizen's email address, used as identity.
     * @param name  the citizen's full name.
     * @throws IllegalArgumentException if email or name is null or blank.
     */
    public Citizen(String email, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.email = email;
        this.name = name;
    }

    /**
     * Returns the citizen's email address.
     *
     * @return the email.
     */
    public String getEmail() { return email; }

    /**
     * Returns the citizen's full name.
     *
     * @return the name.
     */
    public String getName() { return name; }

    /**
     * Checks whether this citizen's email matches the given email (case-insensitive).
     *
     * @param email the email to compare.
     * @return {@code true} if the emails match.
     */
    public boolean hasEmail(String email) {
        return this.email.equalsIgnoreCase(email);
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

    /**
     * Creates and returns a copy of this citizen.
     *
     * @return a new {@link Citizen} with the same email and name.
     */
    public Citizen clone() {
        return new Citizen(email, name);
    }
}
