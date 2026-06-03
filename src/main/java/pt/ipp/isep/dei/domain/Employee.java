package pt.ipp.isep.dei.domain;

import java.io.Serializable;
/**
 * The type Employee.
 */
public class Employee implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private final String email;

    /**
     * Instantiates a new Employee.
     *
     * @param email the email
     */
    public Employee(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) o;
        return email.equals(employee.email);
    }

    /**
     * Has email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public boolean hasEmail(String email) {
        return this.email.equals(email);
    }

    /**
     * Clone method.
     *
     * @return A clone of the current instance.
     */
    public Employee clone() {
        return new Employee(this.email);
    }
}