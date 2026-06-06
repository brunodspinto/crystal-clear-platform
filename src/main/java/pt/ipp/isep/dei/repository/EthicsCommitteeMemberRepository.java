package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.EthicsCommitteeMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link EthicsCommitteeMember} instances.
 */
public class EthicsCommitteeMemberRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<EthicsCommitteeMember> members;

    /**
     * Instantiates a new Ethics committee member repository.
     */
    public EthicsCommitteeMemberRepository() {
        members = new ArrayList<>();
    }

    /**
     * Saves an Ethics Committee Member.
     *
     * @param member the member to save; cannot be null.
     * @return {@code true} if saved successfully.
     * @throws IllegalArgumentException if member is null.
     */
    public boolean save(EthicsCommitteeMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        return members.add(member);
    }

    /**
     * Finds a member by email address (case-insensitive).
     *
     * @param email the email to search for.
     * @return the member, or {@code null} if not found.
     */
    public EthicsCommitteeMember getByEmail(String email) {
        for (EthicsCommitteeMember m : members) {
            if (m.hasEmail(email)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Returns all registered members.
     *
     * @return a defensive copy of all members.
     */
    public List<EthicsCommitteeMember> getAll() {
        return new ArrayList<>(members);
    }
}
