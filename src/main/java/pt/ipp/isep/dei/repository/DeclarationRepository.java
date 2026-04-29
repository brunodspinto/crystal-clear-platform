package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link Declaration} instances.
 */
public class DeclarationRepository {

    private final List<Declaration> declarations;

    /**
     * Creates an empty DeclarationRepository.
     */
    public DeclarationRepository() {
        declarations = new ArrayList<>();
    }

    /**
     * Saves a declaration.
     *
     * @param declaration the declaration to save; cannot be null.
     * @return {@code true} if saved successfully.
     * @throws IllegalArgumentException if declaration is null.
     */
    public boolean save(Declaration declaration) {
        if (declaration == null) {
            throw new IllegalArgumentException("Declaration cannot be null.");
        }
        return declarations.add(declaration);
    }

    /**
     * Returns all declarations with the given status.
     *
     * @param status the status to filter by.
     * @return list of declarations matching the status.
     */
    public List<Declaration> getDeclarationsByStatus(DeclarationStatus status) {
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            if (d.getStatus() == status) {
                result.add(d);
            }
        }
        return List.copyOf(result);
    }

    /**
     * Returns an unmodifiable list of all declarations.
     *
     * @return list of all declarations.
     */
    public List<Declaration> getAll() {
        return List.copyOf(declarations);
    }
}
