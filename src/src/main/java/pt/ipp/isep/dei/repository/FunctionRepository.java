package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Functions.
 */
public class FunctionRepository implements Serializable {

    private final List<Function> functions;

    public FunctionRepository() {
        functions = new ArrayList<>();
    }

    /**
     * Adds a new Function to the repository if it doesn't already exist.
     *
     * @param function the function to add
     * @return an Optional containing the added Function, or empty if it already exists
     */
    public Optional<Function> add(Function function) {
        Optional<Function> newFunction = Optional.empty();
        boolean operationSuccess = false;

        if (validateFunction(function)) {
            newFunction = Optional.of(function.clone());
            operationSuccess = functions.add(newFunction.get());
        }

        if (!operationSuccess) {
            newFunction = Optional.empty();
        }

        return newFunction;
    }

    private boolean validateFunction(Function function) {
        return !functions.contains(function);
    }

    /**
     * Returns a defensive copy of the list of functions.
     *
     * @return The list of functions.
     */
    public List<Function> getFunctions() {
        return List.copyOf(functions);
    }
}