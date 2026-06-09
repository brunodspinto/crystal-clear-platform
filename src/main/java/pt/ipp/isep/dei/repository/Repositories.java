package pt.ipp.isep.dei.repository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Inspired on https://refactoring.guru/design-patterns/singleton/java/example
 * <p>
 * The Repositories class works as a Singleton. It defines the getInstance method that serves as an alternative
 * to the constructor and lets client classes access the same instance of this class over and over.
 */
public class Repositories implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Repositories instance;

    // Serializable repositories: their content is persisted across runs.
    private final OrganizationRepository organizationRepository;
    private final PoliticalAgentRepository politicalAgentRepository;
    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final DeclarationRepository declarationRepository;
    private final EthicsCommitteeMemberRepository ethicsCommitteeMemberRepository;
    private final ValidationRecordRepository validationRecordRepository;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final ComplaintAssessmentRepository complaintAssessmentRepository;

    // Transient repositories: the authentication repository wraps a
    // non-serializable library, and the graph repository holds derived data that
    // is rebuilt at runtime. They are recreated on load and repopulated by the
    // Bootstrap on startup.
    private transient AuthenticationRepository authenticationRepository;
    private transient GraphRepository graphRepository;

    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */
    private Repositories() {
        organizationRepository = new OrganizationRepository();
        taskCategoryRepository = new TaskCategoryRepository();
        authenticationRepository = new AuthenticationRepository();
        politicalAgentRepository = new PoliticalAgentRepository();
        citizenRepository = new CitizenRepository();
        complaintRepository = new ComplaintRepository();
        declarationRepository = new DeclarationRepository();
        graphRepository = new GraphRepository();
        ethicsCommitteeMemberRepository = new EthicsCommitteeMemberRepository();
        validationRecordRepository = new ValidationRecordRepository();
        registrationRequestRepository = new RegistrationRequestRepository();
        complaintAssessmentRepository = new ComplaintAssessmentRepository();
    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     *
     * @return the instance
     */
    public static Repositories getInstance() {
        if (instance == null) {
            instance = new Repositories();
        }
        return instance;
    }

    /**
     * Replaces the singleton instance, typically with one loaded from disk by
     * {@link RepositoriesFile}. Used to support object-serialization persistence
     * across successive runs.
     *
     * @param repositories the instance to use as the singleton.
     */
    public static void setInstance(Repositories repositories) {
        instance = repositories;
    }

    /**
     * Restores the transient (non-serialized) repositories after deserialization.
     * Their content is then repopulated by the Bootstrap on startup.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        authenticationRepository = new AuthenticationRepository();
        graphRepository = new GraphRepository();
    }

    /**
     * Gets organization repository.
     *
     * @return the organization repository
     */
    public OrganizationRepository getOrganizationRepository() {
        return organizationRepository;
    }

    /**
     * Gets task category repository.
     *
     * @return the task category repository
     */
    public TaskCategoryRepository getTaskCategoryRepository() {
        return taskCategoryRepository;
    }

    /**
     * Gets authentication repository.
     *
     * @return the authentication repository
     */
    public AuthenticationRepository getAuthenticationRepository() {
        return authenticationRepository;
    }

    /**
     * Returns the political agent repository.
     *
     * @return the {@link PoliticalAgentRepository} instance.
     */
    public PoliticalAgentRepository getPoliticalAgentRepository() {
        return politicalAgentRepository;
    }

    /**
     * Returns the citizen repository.
     *
     * @return the {@link CitizenRepository} instance.
     */
    public CitizenRepository getCitizenRepository() {
        return citizenRepository;
    }

    /**
     * Returns the complaint repository.
     *
     * @return the {@link ComplaintRepository} instance.
     */
    public ComplaintRepository getComplaintRepository() {
        return complaintRepository;
    }

    /**
     * Returns the declaration repository.
     *
     * @return the {@link DeclarationRepository} instance.
     */
    public DeclarationRepository getDeclarationRepository() {
        return declarationRepository;
    }

    /**
     * Gets graph repository.
     *
     * @return the graph repository
     */
    public GraphRepository getGraphRepository() {
        return graphRepository;
    }

    /**
     * Gets ethics committee member repository.
     *
     * @return the ethics committee member repository
     */
    public EthicsCommitteeMemberRepository getEthicsCommitteeMemberRepository() {
        return ethicsCommitteeMemberRepository;
    }

    /**
     * Gets validation record repository.
     *
     * @return the validation record repository
     */
    public ValidationRecordRepository getValidationRecordRepository() {
        return validationRecordRepository;
    }

    /**
     * Gets registration request repository.
     *
     * @return the registration request repository
     */
    public RegistrationRequestRepository getRegistrationRequestRepository() {
        return registrationRequestRepository;
    }

    /**
     * Gets complaint assessment repository.
     *
     * @return the complaint assessment repository
     */
    public ComplaintAssessmentRepository getComplaintAssessmentRepository() {
        return complaintAssessmentRepository;
    }
}