package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.Locale;

/**
 * Represents a political or administrative function in the system.
 */
public class Function implements Serializable {

    private String designation;

    /**
     * Instantiates a new Function.
     *
     * @param designation the designation of the function
     */
    public Function(String designation) {
        setDesignation(designation);
    }

    private void setDesignation(String designation) {
        if (designation == null || designation.trim().isEmpty()) {
            throw new IllegalArgumentException("Designation cannot be null or empty.");
        }
        this.designation = designation;
    }

    /**
     * Gets the designation of the function.
     *
     * @return the designation
     */
    public String getDesignation() {
        return designation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return normalizeDesignation(designation).equals(normalizeDesignation(function.designation));
    }

    private String normalizeDesignation(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    /**
     * Clones the current Function instance.
     *
     * @return A clone of the current Function
     */
    public Function clone() {
        return new Function(this.designation);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Function{");
        sb.append("designation='").append(designation).append('\'');
        sb.append('}');
        return sb.toString();
    }
}