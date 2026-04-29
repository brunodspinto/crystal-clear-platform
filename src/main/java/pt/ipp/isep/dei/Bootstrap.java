package pt.ipp.isep.dei;

import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;

import java.util.Date;

public class Bootstrap implements Runnable {

    public void run() {
        addTaskCategories();
        addOrganization();
        addUS04Organizations();
        addPoliticalAgents();
        addCitizens();
        addUsers();
    }

    private void addOrganization() {
        //get organization repository
        OrganizationRepository organizationRepository = Repositories.getInstance().getOrganizationRepository();

        Organization organization = new Organization("123456789", "This Company", "www.this.app", "912345678", "geral@this.app");
        organization.addEmployee(new Employee("admin@this.app"));
        organization.addEmployee(new Employee("employee@this.app"));
        organizationRepository.add(organization);
    }

    private void addTaskCategories() {
        //get task category repository
        TaskCategoryRepository taskCategoryRepository = Repositories.getInstance().getTaskCategoryRepository();

        taskCategoryRepository.add(new TaskCategory("Analysis"));
        taskCategoryRepository.add(new TaskCategory("Design"));
        taskCategoryRepository.add(new TaskCategory("Implementation"));
        taskCategoryRepository.add(new TaskCategory("Development"));
        taskCategoryRepository.add(new TaskCategory("Testing"));
        taskCategoryRepository.add(new TaskCategory("Deployment"));
        taskCategoryRepository.add(new TaskCategory("Maintenance"));
    }

    /**
     * Adds sample organizations for US04 demo purposes.
     */
    private void addUS04Organizations() {
        OrganizationRepository orgRepo = Repositories.getInstance().getOrganizationRepository();
        orgRepo.save(new Organization("PartyX", "public", OrganizationType.POLITICAL_PARTY));
        orgRepo.save(new Organization("TechCorp", "private", OrganizationType.COMPANY));
        orgRepo.save(new Organization("GreenFoundation", "social", OrganizationType.FOUNDATION));
    }

    private void addPoliticalAgents() {
        PoliticalAgentRepository agentRepo = Repositories.getInstance().getPoliticalAgentRepository();
        agentRepo.save(new PoliticalAgent("António Félix", "antonio@gov.pt", "12345678", "123456789",
                new Date(), null));
        agentRepo.save(new PoliticalAgent("Mariana Rodrigues", "mariana@gov.pt", "87654321", "987654321",
                new Date(), null));
    }

    private void addCitizens() {
        CitizenRepository citizenRepo = Repositories.getInstance().getCitizenRepository();
        citizenRepo.save(new Citizen("citizen@this.app", "Active Citizen"));
    }

    private void addUsers() {
        AuthenticationRepository authenticationRepository = Repositories.getInstance().getAuthenticationRepository();

        authenticationRepository.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_EMPLOYEE, AuthenticationController.ROLE_EMPLOYEE);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_CITIZEN, AuthenticationController.ROLE_CITIZEN);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_POLITICAL_AGENT, AuthenticationController.ROLE_POLITICAL_AGENT);

        authenticationRepository.addUserWithRole("Main Administrator", "admin@this.app", "admin", AuthenticationController.ROLE_ADMIN);
        authenticationRepository.addUserWithRole("Employee", "employee@this.app", "employee", AuthenticationController.ROLE_EMPLOYEE);
        authenticationRepository.addUserWithRole("Active Citizen", "citizen@this.app", "citizen", AuthenticationController.ROLE_CITIZEN);
        authenticationRepository.addUserWithRole("António Félix", "antonio@gov.pt", "AAA11bb", AuthenticationController.ROLE_POLITICAL_AGENT);
    }
}