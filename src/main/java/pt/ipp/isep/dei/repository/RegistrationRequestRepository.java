package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.RegistrationStatus;
import pt.ipp.isep.dei.domain.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages registration requests (US01/US02).
 */
public class RegistrationRequestRepository {

    private final List<RegistrationRequest> requests = new ArrayList<>();

    /**
     * Saves a new registration request.
     *
     * @param request the request to save
     * @return {@code true} if saved, {@code false} if a duplicate already exists
     */
    public boolean save(RegistrationRequest request) {
        if (existsByEmailAndRole(request.getEmail(), request.getRole())) {
            return false;
        }
        requests.add(request);
        return true;
    }

    /**
     * Returns whether a registration request already exists for the given email and role.
     *
     * @param email the email
     * @param role  the role
     * @return the boolean
     */
    public boolean existsByEmailAndRole(String email, UserRole role) {
        for (RegistrationRequest r : requests) {
            if (r.getEmail().equalsIgnoreCase(email) && r.getRole() == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all requests with PENDING status.
     *
     * @return the pending requests
     */
    public List<RegistrationRequest> getPendingRequests() {
        List<RegistrationRequest> pending = new ArrayList<>();
        for (RegistrationRequest r : requests) {
            if (r.getStatus() == RegistrationStatus.PENDING) {
                pending.add(r);
            }
        }
        return pending;
    }

    /**
     * Returns all stored requests (any status).
     *
     * @return the all
     */
    public List<RegistrationRequest> getAll() {
        return new ArrayList<>(requests);
    }
}
