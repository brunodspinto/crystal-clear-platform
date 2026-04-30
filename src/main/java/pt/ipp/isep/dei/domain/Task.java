package pt.ipp.isep.dei.domain;

public class Task {
    private final String reference;
    private String description;
    private String informalDescription;
    private String technicalDescription;
    private int duration;
    private double cost;

    private TaskCategory taskCategory;

    private Employee employee;

    public Task(String reference, String description, String informalDescription, String technicalDescription,
                int duration, double cost, TaskCategory taskCategory, Employee employee) {

        validateReference(reference);
        this.reference = reference;
        this.description = description;
        this.informalDescription = informalDescription;
        this.technicalDescription = technicalDescription;
        this.duration = duration;
        this.cost = cost;
        this.taskCategory = taskCategory;
        this.employee = employee;
    }

    private void validateReference(String reference) {
        if (reference == null || reference.isEmpty()) {
            throw new IllegalArgumentException("Reference cannot be null or empty.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task task = (Task) o;
        return reference.equals(task.reference) && employee.equals(task.employee);
    }

    /**
     * Clone method.
     *
     * @return A clone of the current instance.
     */
    public Task clone() {
        return new Task(this.reference, this.description, this.informalDescription, this.technicalDescription,
                this.duration, this.cost, this.taskCategory.clone(), this.employee.clone());
    }
}