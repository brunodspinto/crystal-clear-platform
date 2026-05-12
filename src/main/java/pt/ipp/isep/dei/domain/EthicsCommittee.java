package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

public class EthicsCommittee {

    private final String name;
    private final List<EthicsCommitteeMember> members;

    public EthicsCommittee(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(EthicsCommitteeMember member) {
        if (member == null)
            throw new IllegalArgumentException("Member cannot be null");
        if (!members.contains(member))
            members.add(member);
    }

    public String getName() { return name; }

    public List<EthicsCommitteeMember> getMembers() { return new ArrayList<>(members); }

    @Override
    public String toString() {
        return String.format("EthicsCommittee{name='%s', members=%d}", name, members.size());
    }
}
