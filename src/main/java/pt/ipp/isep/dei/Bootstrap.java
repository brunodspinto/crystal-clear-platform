package pt.ipp.isep.dei;

import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;

import java.util.Calendar;
import java.util.Date;

public class Bootstrap implements Runnable {

    public void run() {
        addTaskCategories();
        addOrganization();
        addUS04Organizations();
        addPoliticalAgents();
        addCitizens();
        addEthicsCommitteeMembers();
        addUsers();
        addValidatedDeclaration();
        addUS10IncomeHistory();
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
        citizenRepo.save(new Citizen("citizen@this.app", "Active Citizen", "CC123456789"));
    }

    private void addEthicsCommitteeMembers() {
        EthicsCommitteeMemberRepository repo =
                Repositories.getInstance().getEthicsCommitteeMemberRepository();
        EthicsCommitteeMember member = new EthicsCommitteeMember("Maria Sousa", "maria@ethics.pt");
        repo.save(member);

        EthicsCommittee committee = new EthicsCommittee("National Ethics Committee");
        committee.addMember(member);
    }

    private void addUsers() {
        AuthenticationRepository authenticationRepository = Repositories.getInstance().getAuthenticationRepository();

        authenticationRepository.addUserRole(AuthenticationController.ROLE_ADMIN, AuthenticationController.ROLE_ADMIN);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_EMPLOYEE, AuthenticationController.ROLE_EMPLOYEE);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_CITIZEN, AuthenticationController.ROLE_CITIZEN);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_POLITICAL_AGENT, AuthenticationController.ROLE_POLITICAL_AGENT);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_PRODUCT_OWNER, AuthenticationController.ROLE_PRODUCT_OWNER);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_JOURNALIST, AuthenticationController.ROLE_JOURNALIST);
        authenticationRepository.addUserRole(AuthenticationController.ROLE_ETHICS_COMMITTEE, AuthenticationController.ROLE_ETHICS_COMMITTEE);

        authenticationRepository.addUserWithRole("Main Administrator", "admin@this.app", "ADM11ad", AuthenticationController.ROLE_ADMIN);
        authenticationRepository.addUserWithRole("Employee", "employee@this.app", "EMP11em", AuthenticationController.ROLE_EMPLOYEE);
        authenticationRepository.addUserWithRole("Active Citizen", "citizen@this.app", "CIT11ci", AuthenticationController.ROLE_CITIZEN);
        authenticationRepository.addUserWithRole("António Félix", "antonio@gov.pt", "AAA11bb", AuthenticationController.ROLE_POLITICAL_AGENT);
        authenticationRepository.addUserWithRole("Product Owner", "po@this.app", "POW11po", AuthenticationController.ROLE_PRODUCT_OWNER);
        authenticationRepository.addUserWithRole("Journalist", "journalist@news.pt", "JRN11jr", AuthenticationController.ROLE_JOURNALIST);
        authenticationRepository.addUserWithRole("Maria Sousa", "maria@ethics.pt", "AAA11bb", AuthenticationController.ROLE_ETHICS_COMMITTEE);
    }

    /**
     * Adds a sample validated declaration for US24 demo purposes.
     */
    private void addValidatedDeclaration() {
        DeclarationRepository declarationRepo = Repositories.getInstance().getDeclarationRepository();

        PoliticalAgent agent = new PoliticalAgent("António Félix", "antonio@gov.pt",
                "12345678", "123456789", new Date(), null);
        Organization parliament = new Organization("Parliament", "public", OrganizationType.POLITICAL_PARTY);

        Declaration d = new Declaration(DeclarationType.INITIAL, agent, new Date());
        d.addPositionEntry(parliament, "Deputy", PositionNature.PUBLIC,
                60000.0, 5000.0, 2000.0, new Date(), null);
        d.addAssetEntry(AssetType.REAL_ESTATE, 250000.0, new RealEstate("Apartment", "Lisbon"));
        d.addAssetEntry(AssetType.VEHICLES, 25000.0, new VehicleAsset("Toyota Corolla"));
        d.setStatus(DeclarationStatus.VALIDATED);
        declarationRepo.save(d);
    }

    /**
     * Adds extra validated declarations for the same political agent so the
     * US10 demo (Analyse Income Evolution) has a chronological series to show.
     */
    private void addUS10IncomeHistory() {
        PoliticalAgentRepository agentRepo = Repositories.getInstance().getPoliticalAgentRepository();
        DeclarationRepository declRepo = Repositories.getInstance().getDeclarationRepository();

        PoliticalAgent agent = agentRepo.getAll().get(0);
        Organization parliament = new Organization("Parliament", "public", OrganizationType.POLITICAL_PARTY);

        Declaration d2023 = new Declaration(DeclarationType.REGULAR, agent, dateOf(2023, Calendar.MARCH, 15));
        d2023.addPositionEntry(parliament, "Deputy", PositionNature.PUBLIC,
                55000.0, 3000.0, 1000.0, dateOf(2022, Calendar.JANUARY, 1), null);
        d2023.addSubsidyEntry(parliament, 1500.0, "Travel allowance", dateOf(2023, Calendar.FEBRUARY, 1));
        d2023.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(d2023);

        Declaration d2024 = new Declaration(DeclarationType.REGULAR, agent, dateOf(2024, Calendar.MARCH, 20));
        d2024.addPositionEntry(parliament, "Deputy", PositionNature.PUBLIC,
                60000.0, 5000.0, 2000.0, dateOf(2022, Calendar.JANUARY, 1), null);
        d2024.addSubsidyEntry(parliament, 2500.0, "Travel allowance", dateOf(2024, Calendar.FEBRUARY, 1));
        d2024.setStatus(DeclarationStatus.VALIDATED);
        declRepo.save(d2024);

        Declaration pending = new Declaration(DeclarationType.REGULAR, agent, dateOf(2024, Calendar.JUNE, 1));
        pending.addPositionEntry(parliament, "Deputy", PositionNature.PUBLIC,
                60000.0, 0.0, 0.0, dateOf(2022, Calendar.JANUARY, 1), null);
        declRepo.save(pending);
    }

    private static Date dateOf(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }
}