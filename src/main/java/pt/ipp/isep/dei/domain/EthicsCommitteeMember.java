package pt.ipp.isep.dei.domain;

/**
 * Represents a member of the Ethics Committee who is responsible for
 * validating Declarations of Interests submitted by Political Agents.
 */
public class EthicsCommitteeMember {

    private final String name;
    private final String email;

    /**
     * Creates a new EthicsCommitteeMember.
     *
     * @param name  the full name of the member; cannot be null or blank.
     * @param email the email address of the member; cannot be null or blank.
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public EthicsCommitteeMember(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        this.name = name;
        this.email = email;
    }

    /** @return the full name of the member. */
    public String getName() { return name; }

    /** @return the email address of the member. */
    public String getEmail() { return email; }

    /**
     * Checks whether this member's email matches the given email (case-insensitive).
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
        if (!(o instanceof EthicsCommitteeMember)) return false;
        EthicsCommitteeMember that = (EthicsCommitteeMember) o;
        return email.equalsIgnoreCase(that.email);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, email);
    }
}
