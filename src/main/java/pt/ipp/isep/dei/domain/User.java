package pt.ipp.isep.dei.domain;

public abstract class User {

    protected final String name;
    protected final String email;

    public User(String name, String email) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be null or blank");
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public boolean hasEmail(String email) {
        return this.email.equalsIgnoreCase(email);
    }
}
