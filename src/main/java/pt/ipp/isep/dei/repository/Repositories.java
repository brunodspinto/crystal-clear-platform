package pt.ipp.isep.dei.repository;

/**
 * Inspired on https://refactoring.guru/design-patterns/singleton/java/example
 *
 * The Repositories class works as a Singleton. It defines the getInstance method that serves as an alternative
 * to the constructor and lets client classes access the same instance of this class over and over.
 */
public class Repositories {
    private static Repositories instance;
    private final OrganizationRepository organizationRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final AuthenticationRepository authenticationRepository;
    private final PoliticalAgentRepository politicalAgentRepository;
    private final CitizenRepository citizenRepository;
    private final ComplaintRepository complaintRepository;
    private final DeclarationRepository declarationRepository;
    private final GraphRepository graphRepository;

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
    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     */
    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                instance = new Repositories();
            }
        }
        return instance;
    }

    public OrganizationRepository getOrganizationRepository() {
        return organizationRepository;
    }

    public TaskCategoryRepository getTaskCategoryRepository() {
        return taskCategoryRepository;
    }

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

    public GraphRepository getGraphRepository() {
        return graphRepository;
    }
}