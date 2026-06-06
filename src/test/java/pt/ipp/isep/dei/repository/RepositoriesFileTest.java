package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ipp.isep.dei.domain.Citizen;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.Organization;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RepositoriesFileTest {

    @Test
    void ensureLoadOfMissingFileReturnsNull(@TempDir Path tmp) {
        File missing = tmp.resolve("does-not-exist.dat").toFile();
        assertNull(new RepositoriesFile().load(missing));
    }

    @Test
    void ensureSaveReturnsTrue(@TempDir Path tmp) {
        File file = tmp.resolve("repos.dat").toFile();
        assertTrue(new RepositoriesFile().save(file, Repositories.getInstance()));
    }

    @Test
    void ensureSaveThenLoadPreservesSerializableData(@TempDir Path tmp) {
        File file = tmp.resolve("repos.dat").toFile();
        Repositories repos = Repositories.getInstance();

        String orgName = "PersistOrg_" + System.nanoTime();
        repos.getOrganizationRepository().save(
                new Organization(orgName, "private", OrganizationType.COMPANY));

        Citizen citizen = new Citizen("persist_" + System.nanoTime() + "@test.com",
                "Persist Citizen", "CC999999999");
        repos.getCitizenRepository().save(citizen);
        PoliticalAgent agent = new PoliticalAgent("Persist Agent",
                "pa_" + System.nanoTime() + "@gov.pt", "11112222", "999888777", new Date(), null);
        Complaint complaint = new Complaint("Reported behaviour", new Date(0),
                citizen, agent, PoliticalFunction.MAYOR);
        repos.getComplaintRepository().save(complaint);

        RepositoriesFile store = new RepositoriesFile();
        assertTrue(store.save(file, repos));

        Repositories loaded = store.load(file);
        assertNotNull(loaded);

        // Serializable repositories kept their content across the round-trip.
        boolean orgFound = false;
        for (Organization o : loaded.getOrganizationRepository().getOrganizations()) {
            if (orgName.equals(o.getName())) {
                orgFound = true;
            }
        }
        assertTrue(orgFound);
        assertFalse(loaded.getComplaintRepository().getComplaints().isEmpty());

        // Transient repositories were rebuilt (not null) after deserialization.
        assertNotNull(loaded.getAuthenticationRepository());
        assertNotNull(loaded.getTaskCategoryRepository());
        assertNotNull(loaded.getDeclarationRepository());
        assertNotNull(loaded.getGraphRepository());
        assertNotNull(loaded.getComplaintAssessmentRepository());
    }
}
