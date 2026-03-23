package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTest {

    @Test
    void ensureFunctionIsCreatedWithValidDesignation() {
        // Arrange
        String designation = "Minister";

        // Act
        Function function = new Function(designation);

        // Assert
        assertEquals(designation, function.getDesignation(), "The designation should match the one provided.");
    }

    @Test
    void ensureExceptionIsThrownWhenDesignationIsNull() {
        // Arrange
        String invalidDesignation = null;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Function(invalidDesignation);
        });

        assertEquals("Designation cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensureExceptionIsThrownWhenDesignationIsEmpty() {
        // Arrange
        String invalidDesignation = "   ";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Function(invalidDesignation);
        });

        assertEquals("Designation cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensureEqualsWorksCorrectlyForSameDesignation() {
        // Arrange
        Function f1 = new Function("Mayor");
        Function f2 = new Function("Mayor");
        Function f3 = new Function("mayor"); // Testar case-insensitivity

        // Act & Assert
        assertEquals(f1, f2, "Functions with the same designation should be equal.");
        assertEquals(f1, f3, "Functions should be equal regardless of case.");
    }

    @Test
    void ensureEqualsFailsForDifferentDesignation() {
        // Arrange
        Function f1 = new Function("Mayor");
        Function f2 = new Function("Deputy");

        // Act & Assert
        assertNotEquals(f1, f2, "Functions with different designations should not be equal.");
    }

    @Test
    void ensureCloneCreatesIndependentCopy() {
        // Arrange
        Function original = new Function("President");

        // Act
        Function clone = original.clone();

        // Assert
        assertNotSame(original, clone, "The clone should be a different object in memory.");
        assertEquals(original, clone, "The clone should have the same data as the original.");
    }
}