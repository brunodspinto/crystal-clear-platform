package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class EthicsCommitteeTest {

    @Test
    void ensureCreationWorks() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        assertNotNull(committee);
    }

    @Test
    void ensureCreationFailsWithNullName() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new EthicsCommittee(null);
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankName() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new EthicsCommittee("   ");
            }
        });
    }

    @Test
    void ensureGetNameReturnsCorrectValue() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        assertEquals("National Ethics Committee", committee.getName());
    }

    @Test
    void ensureNewCommitteeHasNoMembers() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        assertTrue(committee.getMembers().isEmpty());
    }

    @Test
    void ensureAddMemberWorks() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        EthicsCommitteeMember member = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        committee.addMember(member);
        assertEquals(1, committee.getMembers().size());
        assertTrue(committee.getMembers().contains(member));
    }

    @Test
    void ensureAddMemberFailsWithNull() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                committee.addMember(null);
            }
        });
    }

    @Test
    void ensureDuplicateMemberIsNotAdded() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        EthicsCommitteeMember member = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        committee.addMember(member);
        committee.addMember(member);
        assertEquals(1, committee.getMembers().size());
    }

    @Test
    void ensureGetMembersReturnsCopy() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        committee.addMember(new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt"));
        committee.getMembers().clear();
        assertEquals(1, committee.getMembers().size());
    }

    @Test
    void ensureToStringContainsName() {
        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        assertTrue(committee.toString().contains("National Ethics Committee"));
    }
}
