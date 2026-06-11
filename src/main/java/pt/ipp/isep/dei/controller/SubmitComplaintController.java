package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Citizen;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;
import pt.ipp.isep.dei.dto.ComplaintItemDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.mapper.ComplaintItemMapper;
import pt.ipp.isep.dei.mapper.PoliticalAgentMapper;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.CitizenRepository;
import pt.ipp.isep.dei.repository.ComplaintRepository;
import pt.ipp.isep.dei.repository.PoliticalAgentRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Controller responsible for handling the submission of a citizen complaint (US12).
 *
 * <p>Following the ESOFT DTO pattern, the controller never exposes domain objects
 * to the UI: it provides the political agents and the grievances as Data Transfer
 * Objects (via mappers) and it keeps the complaint being built internally, so the
 * UI stays decoupled from the domain.</p>
 */
public class SubmitComplaintController {

    private final PoliticalAgentRepository politicalAgentRepository;
    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;
    private final AuthenticationRepository authenticationRepository;

    private final PoliticalAgentMapper politicalAgentMapper = new PoliticalAgentMapper();
    private final ComplaintItemMapper complaintItemMapper = new ComplaintItemMapper();

    /** The complaint currently being built (not exposed to the UI). */
    private Complaint currentComplaint;

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
     * Returns all registered political agents as DTOs for the UI to select one.
     *
     * @return list of {@link PoliticalAgentDTO}.
     */
    public List<PoliticalAgentDTO> getPoliticalAgents() {
        return politicalAgentMapper.toDTO(politicalAgentRepository.getAll());
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
     * Starts a new complaint about the selected political agent, on behalf of the
     * currently logged-in citizen. The agent DTO is mapped back to the domain agent.
     *
     * @param agentDto the selected political agent (DTO).
     * @return {@code true} if the complaint was started; {@code false} if the agent
     *         DTO is null, the citizen is not found, or the agent is not found.
     */
    public boolean startComplaint(PoliticalAgentDTO agentDto) {
        if (agentDto == null) {
            return false;
        }
        Email email = authenticationRepository.getCurrentUserSession().getUserId();
        Citizen citizen = citizenRepository.getCitizenByEmail(email.getEmail());
        if (citizen == null) {
            return false;
        }
        PoliticalAgent agent = politicalAgentRepository.getByEmail(agentDto.getEmail());
        if (agent == null) {
            return false;
        }
        currentComplaint = complaintRepository.createComplaint(citizen, agent);
        return true;
    }

    /**
     * Adds a grievance to the complaint being built.
     *
     * @param description       a description of the reported behaviour.
     * @param complaintDate     the date when the behaviour occurred.
     * @param politicalFunction the function the agent held at the time.
     * @throws IllegalStateException    if no complaint has been started.
     * @throws IllegalArgumentException if the grievance data is invalid.
     */
    public void addGrievance(String description, Date complaintDate, PoliticalFunction politicalFunction) {
        if (currentComplaint == null) {
            throw new IllegalStateException("No complaint in progress. Call startComplaint first.");
        }
        currentComplaint.addItem(description, complaintDate, politicalFunction);
    }

    /**
     * Returns the grievances already added to the complaint being built, as DTOs.
     *
     * @return list of {@link ComplaintItemDTO} (empty if no complaint is in progress).
     */
    public List<ComplaintItemDTO> getCurrentGrievances() {
        if (currentComplaint == null) {
            return new ArrayList<>();
        }
        return complaintItemMapper.toDTO(currentComplaint.getItems());
    }

    /**
     * @return the number of grievances in the complaint being built.
     */
    public int getCurrentGrievanceCount() {
        return currentComplaint == null ? 0 : currentComplaint.getItemCount();
    }

    /**
     * Persists the complaint being built (with all its grievances) and clears the
     * in-progress state.
     *
     * @return {@code true} if the complaint was saved; {@code false} if there is no
     *         complaint in progress, or it has no grievances.
     */
    public boolean submitComplaint() {
        if (currentComplaint == null || currentComplaint.getItemCount() == 0) {
            return false;
        }
        boolean saved = complaintRepository.save(currentComplaint);
        currentComplaint = null;
        return saved;
    }

    /**
     * Discards the complaint being built, if any.
     */
    public void cancelComplaint() {
        currentComplaint = null;
    }
}
