package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Citizen;

import static org.junit.jupiter.api.Assertions.*;

class CitizenRepositoryTest {

    private Citizen createCitizen(String email, String name, String idCard) {
        return new Citizen(email, name, idCard);
    }

    @Test
    void ensureSaveCitizenWorks() {
        CitizenRepository repo = new CitizenRepository();
        Citizen citizen = createCitizen("citizen@test.com", "Test Citizen", "CC123456789");

        assertTrue(repo.save(citizen));
    }

    @Test
    void ensureSaveDuplicateCitizenFails() {
        CitizenRepository repo = new CitizenRepository();
        Citizen citizen = createCitizen("citizen@test.com", "Test Citizen", "CC123456789");
        repo.save(citizen);

        Citizen duplicate = createCitizen("citizen@test.com", "Other Name", "CC999999999");

        assertFalse(repo.save(duplicate));
    }

    @Test
    void ensureGetCitizenByEmailReturnsTheCitizen() {
        CitizenRepository repo = new CitizenRepository();
        Citizen citizen = createCitizen("citizen@test.com", "Test Citizen", "CC123456789");
        repo.save(citizen);

        Citizen found = repo.getCitizenByEmail("citizen@test.com");

        assertNotNull(found);
        assertEquals(citizen, found);
    }

    @Test
    void ensureGetCitizenByEmailReturnsNullWhenNotFound() {
        CitizenRepository repo = new CitizenRepository();
        repo.save(createCitizen("citizen@test.com", "Test Citizen", "CC123456789"));

        Citizen found = repo.getCitizenByEmail("unknown@test.com");

        assertNull(found);
    }

    @Test
    void ensureGetCitizenByEmailReturnsNullForEmptyRepository() {
        CitizenRepository repo = new CitizenRepository();

        Citizen found = repo.getCitizenByEmail("citizen@test.com");

        assertNull(found);
    }

    @Test
    void ensureSavedCitizenIsAClone() {
        CitizenRepository repo = new CitizenRepository();
        Citizen original = createCitizen("citizen@test.com", "Test Citizen", "CC123456789");
        repo.save(original);

        Citizen found = repo.getCitizenByEmail("citizen@test.com");

        // The stored citizen equals the original but is not the same instance
        assertEquals(original, found);
        assertNotSame(original, found);
    }
}
