package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Function;
import pt.ipp.isep.dei.repository.FunctionRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.Optional;

/**
 * Controller for registering a new Function (US05).
 */
public class RegisterFunctionController {

    private FunctionRepository functionRepository;

    public RegisterFunctionController() {
        getFunctionRepository();
    }

    private FunctionRepository getFunctionRepository() {
        if (functionRepository == null) {
            Repositories repositories = Repositories.getInstance();
            functionRepository = repositories.getFunctionRepository();
        }
        return functionRepository;
    }

    /**
     * Creates and registers a new function.
     *
     * @param designation the designation of the new function
     * @return an Optional containing the created Function if successful, or empty if it fails
     */
    public Optional<Function> registerFunction(String designation) {
        Function newFunction = new Function(designation);
        return getFunctionRepository().add(newFunction);
    }
}