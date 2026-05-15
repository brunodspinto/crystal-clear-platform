package pt.ipp.isep.dei.domain;

/**
 * The type Ethics committee member.
 */
public class EthicsCommitteeMember extends User {

    /**
     * Instantiates a new Ethics committee member.
     *
     * @param name  the name
     * @param email the email
     */
    public EthicsCommitteeMember(String name, String email) {
        super(name, email);
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
