package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Organization {
    private final String vatNumber;
    private final List<Employee> employees;
    private final List<Task> tasks;
    private String name;
    private String website;
    private String phone;
    private String email;

    /**
     * This method is the constructor of the organization.
     *
     * @param vatNumber The vat number of the organization. This is the identity of the organization, therefore it
     *                  cannot be changed.
     */
    public Organization(String vatNumber, String name, String website, String phone, String email) {
    if (vatNumber == null || vatNumber.isBlank())
        throw new IllegalArgumentException("VAT number required");
        
    if (name == null || name.isBlank())
        throw new IllegalArgumentException("Name cannot be null or empty");
    
    if (phone == null || phone.isBlank())
        throw new IllegalArgumentException("Phone required");

    if (email == null || email.isBlank())
        throw new IllegalArgumentException("Email required");
   
    this.vatNumber = vatNumber;
    this.name = name;   
    this.website = website;
    this.phone = phone;
    this.email = email;

    this.employees = new ArrayList<>();
    this.tasks = new ArrayList<>();
    }

public String getVatNumber() {
    return vatNumber;
}

public String getName() {
    return name;
}

public void setName(String name) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    this.name = name;
}

public String getWebsite() {
    return website;
}

public void setWebsite(String website) {
    this.website = website;
}

public String getPhone() {
    return phone;
}

public void setPhone(String phone) {
    this.phone = phone;
}

public String getEmail() {
    return email;
}

public void setEmail(String email) {
    this.email = email;
}

public List<Employee> getEmployees() {
    return List.copyOf(employees);
}

public List<Task> getTasks() {
    return List.copyOf(tasks);
}

@Override
public String toString() {
    return "Organization{" +
            "vatNumber='" + vatNumber + '\'' +
            ", name='" + name + '\'' +
            ", website='" + website + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            '}';
}

    //add employee to organization
    public boolean addEmployee(Employee employee) {
       if (!employees.contains(employee)) {
            employees.add(employee);
        }
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
     * @return
     */
    public Optional<Task> createTask(String reference, String description, String informalDescription,
                                 String technicalDescription, int duration, double cost,
                                 TaskCategory taskCategory, Employee employee) {

    if (!employees.contains(employee)) {
        return Optional.empty();
    }

    Task task = new Task(reference, description, informalDescription,
            technicalDescription, duration, cost, taskCategory, employee);

    if (tasks.contains(task)) {
        return Optional.empty();
    }

    tasks.add(task);
    return Optional.of(task);
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
     * This methos checks if the organization has an employee with the given email.
     *
     * @param email The email to be checked.
     * @return True if the organization has an employee with the given email.
     */
    public boolean anyEmployeeHasEmail(String email) {
        boolean result = false;
        for (Employee employee : employees) {
            if (employee.hasEmail(email)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organization)) {
            return false;
        }
        Organization that = (Organization) o;
        return vatNumber.equals(that.vatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vatNumber);
    }

    //Clone organization
    public Organization clone() {
        Organization clone = new Organization(this.vatNumber);
        clone.name = (this.name);
        clone.website = (this.website);
        clone.phone = (this.phone);
        clone.email = (this.email);

        for (Employee in : this.employees) {
            clone.employees.add(in.clone());
        }


        for (Task in : this.tasks) {
            clone.tasks.add(in.clone());
        }

        return clone;
    }
}
