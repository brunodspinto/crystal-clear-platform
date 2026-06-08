package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Citizen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link Citizen} instances.
 */
public class CitizenRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Citizen> citizens;

    /**
     * Creates an empty CitizenRepository.
     */
    public CitizenRepository() {
        citizens = new ArrayList<>();
    }

    /**
     * Saves a citizen if no duplicate (by email) already exists.
     * A clone is stored to protect internal state.
     *
     * @param citizen the citizen to save.
     * @return {@code true} if saved, {@code false} if a duplicate exists.
     */
    public boolean save(Citizen citizen) {
        if (citizens.contains(citizen)) {
            return false;
        }
        return citizens.add(citizen.clone());
    }

    /**
     * Finds a citizen by email address (case-insensitive).
     *
     * @param email the email to search for.
     * @return the citizen, or {@code null} if not found.
     */
    public Citizen getCitizenByEmail(String email) {
        for (Citizen c : citizens) {
            if (c.hasEmail(email)) {
                return c;
            }
        }
        return null;
    }
}
