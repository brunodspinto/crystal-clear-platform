package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Ethics committee.
 */
public class EthicsCommittee {

    private final String name;
    private final List<EthicsCommitteeMember> members;

    /**
     * Instantiates a new Ethics committee.
     *
     * @param name the name
     */
    public EthicsCommittee(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        this.name = name;
        this.members = new ArrayList<>();
    }

    /**
     * Add member.
     *
     * @param member the member
     */
    public void addMember(EthicsCommitteeMember member) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (!members.contains(member))
            members.add(member);
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Gets members.
     *
     * @return the members
     */
    public List<EthicsCommitteeMember> getMembers() { return new ArrayList<>(members); }

    @Override
    public String toString() {
        return String.format("EthicsCommittee{name='%s', members=%d}", name, members.size());
    }
}
