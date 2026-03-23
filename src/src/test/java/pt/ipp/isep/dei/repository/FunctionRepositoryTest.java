package pt.ipp.isep.dei.repository;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.Function;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FunctionRepositoryTest {

    @Test
    void ensureFunctionIsAddedSuccessfully() {
        // Arrange
        FunctionRepository repository = new FunctionRepository();
        Function newFunction = new Function("Minister");

        // Act
        Optional<Function> addedFunction = repository.add(newFunction);

        // Assert
        assertTrue(addedFunction.isPresent(), "The function should be added successfully.");
        assertEquals(1, repository.getFunctions().size(), "Repository should have 1 item.");
        assertEquals("Minister", addedFunction.get().getDesignation());
    }

    @Test
    void ensureDuplicateFunctionIsNotAdded() {
        // Arrange
        FunctionRepository repository = new FunctionRepository();
        Function firstFunction = new Function("Minister");
        Function duplicateFunction = new Function("Minister");

        repository.add(firstFunction); // Adicionamos a primeira vez

        // Act
        Optional<Function> result = repository.add(duplicateFunction); // Tentamos adicionar o duplicado

        // Assert
        assertFalse(result.isPresent(), "The duplicate function should not be added (Optional should be empty).");
        assertEquals(1, repository.getFunctions().size(), "Repository should still only have 1 item.");
    }

    @Test
    void ensureGetFunctionsReturnsDefensiveCopy() {
        // Arrange
        FunctionRepository repository = new FunctionRepository();
        repository.add(new Function("Mayor"));

        // Act
        int initialSize = repository.getFunctions().size();

        // Try to maliciously modify the returned list
        try {
            repository.getFunctions().add(new Function("Hacker"));
        } catch (UnsupportedOperationException e) {
            // Expected exception since List.copyOf() creates an unmodifiable list
        }

        // Assert
        assertEquals(initialSize, repository.getFunctions().size(), "The repository list should not be modified externally.");
    }
}