package pt.ipp.isep.dei.domain;

import java.io.Serializable;

/**
 * The type User.
 */
public abstract class User implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * The Name.
     */
    protected final String name;
    /**
     * The Email.
     */
    protected final String email;

    /**
     * Instantiates a new User.
     *
     * @param name  the name
     * @param email the email
     */
    public User(String name, String email) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");
        this.name = name;
        this.email = email;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() { return email; }

    /**
     * Has email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public boolean hasEmail(String email) {
        return this.email.equalsIgnoreCase(email);
    }
}
