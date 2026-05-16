package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Organization.
 */
public class Organization {
    private static int nextGeneratedVat = 1;

    private final String vatNumber;
    private final List<Employee> employees;
    private final List<Task> tasks;
    private String name;
    private String website;
    private String phone;
    private String email;
    private OrganizationType type;
    private String nature;

    /**
     * Constructor for US04 — registers an organization with a name, nature and type.
     * The vatNumber is generated internally using a static counter.
     *
     * @param name   the name
     * @param nature the nature
     * @param type   the type
     */
    public Organization(String name, String nature, OrganizationType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (nature == null || nature.isBlank()) {
            throw new IllegalArgumentException("Nature cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.vatNumber = "GEN-" + nextGeneratedVat;
        nextGeneratedVat = nextGeneratedVat + 1;
        this.name = name;
        this.nature = nature;
        this.type = type;
        this.website = null;
        this.phone = null;
        this.email = null;
        this.employees = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    /**
     * This method is the constructor of the organization.
     *
     * @param vatNumber The vat number of the organization. This is the identity of the organization, therefore it cannot be changed.
     * @param name      The name of the organization.
     * @param website   The website of the organization.
     * @param phone     The phone of the organization.
     * @param email     The email of the organization.
     */
    public Organization(String vatNumber, String name, String website, String phone, String email) {
        if (vatNumber == null || vatNumber.isBlank()) {
            throw new IllegalArgumentException("VAT number required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }

        this.vatNumber = vatNumber;
        this.name = name;
        this.website = website;
        this.phone = phone;
        this.email = email;

        this.employees = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    /**
     * Returns the type of the organization.
     *
     * @return the organization type.
     */
    public OrganizationType getType() {
        return type;
    }

    /**
     * Returns the legal nature of the organization (e.g. "public", "private", "social").
     *
     * @return the nature of the organization.
     */
    public String getNature() {
        return nature;
    }

    /**
     * Sets the legal nature of the organization.
     *
     * @param nature the nature to set.
     */
    public void setNature(String nature) {
        this.nature = nature;
    }

    /**
     * Gets vat number.
     *
     * @return the VAT number of the organization.
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Gets name.
     *
     * @return the name of the organization.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the organization.
     *
     * @param name the new name; cannot be null or blank.
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Gets website.
     *
     * @return the website URL of the organization, or {@code null} if not set.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the website URL of the organization.
     *
     * @param website the website URL.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Gets phone.
     *
     * @return the phone number of the organization, or {@code null} if not set.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the organization.
     *
     * @param phone the phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets email.
     *
     * @return the email address of the organization, or {@code null} if not set.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the organization.
     *
     * @param email the email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns an unmodifiable copy of the organization's employee list.
     *
     * @return list of employees.
     */
    public List<Employee> getEmployees() {
        return List.copyOf(employees);
    }

    /**
     * Returns an unmodifiable copy of the organization's task list.
     *
     * @return list of tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    @Override
    public String toString() {
        if (type != null) {
            return name + " (" + type + ")";
        }
        return "Organization{" +
                "vatNumber='" + vatNumber + '\'' +
                ", name='" + name + '\'' +
                ", website='" + website + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Adds an employee to the organization.
     *
     * @param employee The employee to add.
     * @return True if the employee was successfully added, false otherwise.
     */
    public boolean addEmployee(Employee employee) {
        if (!employees.contains(employee)) {
            return employees.add(employee);
        }
        return false;
    }

    /**
     * This method checks if an employee works for the organization.
     *
     * @param employee The employee to be checked.
     * @return True if the employee works for the organization.
     */
    public boolean employs(Employee employee) {
        return employees.contains(employee);
    }

    /**
     * This method creates a new task.
     *
     * @param reference            The reference of the task to be created.
     * @param description          The description of the task to be created.
     * @param informalDescription  The informal description of the task to be created.
     * @param technicalDescription The technical description of the task to be created.
     * @param duration             The duration of the task to be created.
     * @param cost                 The cost of the task to be created.
     * @param taskCategory         The task category of the task to be created.
     * @param employee             The employee of the task to be created.
     * @return the created Task, or {@code null} if the employee does not belong to the
     *         organization or the task already exists.
     */
    public Task createTask(String reference, String description, String informalDescription,
                           String technicalDescription, int duration, double cost,
                           TaskCategory taskCategory, Employee employee) {

        if (!employees.contains(employee)) {
            return null;
        }

        Task task = new Task(reference, description, informalDescription,
                technicalDescription, duration, cost, taskCategory, employee);

        if (tasks.contains(task)) {
            return null;
        }

        tasks.add(task);
        return task;
    }

    /**
     * This method checks if the task is already in the list of tasks.
     *
     * @param task The task to be checked.
     * @return True if the task is not in the list of tasks.
     */
    private boolean tasksDoNotContain(Task task) {
        return !tasks.contains(task);
    }

    /**
     * This method checks if the organization has an employee with the given email.
     *
     * @param email The email to be checked.
     * @return True if the organization has an employee with the given email.
     */
    public boolean anyEmployeeHasEmail(String email) {
        for (Employee employee : employees) {
            if (employee.hasEmail(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organization that = (Organization) o;
        return vatNumber.equals(that.vatNumber);
    }

    /**
     * Creates a clone of the organization.
     *
     * @return a deep clone preserving the original vatNumber and all fields.
     */
    public Organization clone() {
        if (type != null) {
            return new Organization(this.vatNumber, this.name, this.nature, this.type);
        }
        Organization clone = new Organization(this.vatNumber, this.name, this.website, this.phone, this.email);
        for (Employee in : this.employees) {
            clone.employees.add(in.clone());
        }
        for (Task in : this.tasks) {
            clone.tasks.add(in.clone());
        }
        return clone;
    }

    /**
     * Private constructor for cloning US04 organizations, preserving the original vatNumber.
     */
    private Organization(String vatNumber, String name, String nature, OrganizationType type) {
        this.vatNumber = vatNumber;
        this.name = name;
        this.nature = nature;
        this.type = type;
        this.website = null;
        this.phone = null;
        this.email = null;
        this.employees = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }
}