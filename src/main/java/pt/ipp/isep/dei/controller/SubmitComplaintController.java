package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Citizen;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.CitizenRepository;
import pt.ipp.isep.dei.repository.ComplaintRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller responsible for handling the submission of a citizen complaint (US12).
 */
public class SubmitComplaintController {
    private final PoliticalAgentRepository politicalAgentRepository;
    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public SubmitComplaintController() {
        Repositories repos = Repositories.getInstance();
        this.politicalAgentRepository = repos.getPoliticalAgentRepository();
        this.citizenRepository = repos.getCitizenRepository();
        this.complaintRepository = repos.getComplaintRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     *
     * @param politicalAgentRepository the political agent repository.
     * @param citizenRepository        the citizen repository.
     * @param complaintRepository      the complaint repository.
     * @param authenticationRepository the authentication repository.
     */
    public SubmitComplaintController(PoliticalAgentRepository politicalAgentRepository,
                                     CitizenRepository citizenRepository,
                                     ComplaintRepository complaintRepository,
                                     AuthenticationRepository authenticationRepository) {
        this.politicalAgentRepository = politicalAgentRepository;
        this.citizenRepository = citizenRepository;
        this.complaintRepository = complaintRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Returns all registered political agents available for selection.
     *
     * @return list of {@link PoliticalAgent}.
     */
    public List<PoliticalAgent> getPoliticalAgents() {
        return politicalAgentRepository.getAll();
    }

    /**
     * Returns all available political functions.
     *
     * @return list of {@link PoliticalFunction} values.
     */
    public List<PoliticalFunction> getPoliticalFunctions() {
        return Arrays.asList(PoliticalFunction.values());
    }

    /**
     * Submits a complaint on behalf of the currently logged-in citizen.
     * The citizen's identity is retrieved from the active session.
     *
     * @param description       a description of the reported behaviour.
     * @param complaintDate     the date when the behaviour occurred.
     * @param politicalAgent    the political agent being complained about.
     * @param politicalFunction the function the agent held at the time.
     * @return {@code true} if the complaint was saved, {@code false} if the citizen was not found.
     */
    public boolean submitComplaint(String description, Date complaintDate,
                                   PoliticalAgent politicalAgent, PoliticalFunction politicalFunction) {
        Email email = authenticationRepository.getCurrentUserSession().getUserId();
        Citizen citizen = citizenRepository.getCitizenByEmail(email.getEmail());
        if (citizen == null) {
            return false;
        }
        Complaint complaint = new Complaint(description, complaintDate, citizen,
                politicalAgent, politicalFunction);
        return complaintRepository.save(complaint);
    }
}
