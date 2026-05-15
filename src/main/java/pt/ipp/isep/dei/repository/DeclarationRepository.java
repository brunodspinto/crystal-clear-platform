package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        return new ArrayList<>(result);
    }

    /**
     * Returns an unmodifiable list of all declarations.
     *
     * @return list of all declarations.
     */
    public List<Declaration> getAll() {
        return new ArrayList<>(declarations);
    }

    /**
     * Returns all validated declarations of the given agent submitted on or before the reference date.
     * Used by US09 to compute the integrated situation of a political agent on a given date.
     *
     * @param agent         the political agent to filter by.
     * @param referenceDate the latest acceptable submission date (inclusive).
     * @return a new list of matching declarations; never null.
     * @throws IllegalArgumentException if any argument is null.
     */
    public List<Declaration> getValidatedDeclarationsForAgentUpTo(PoliticalAgent agent, Date referenceDate) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (referenceDate == null) {
            throw new IllegalArgumentException("Reference date cannot be null.");
        }
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            if (d.getStatus() == DeclarationStatus.VALIDATED
                    && d.getAgent().equals(agent)
                    && !d.getSubmissionDate().after(referenceDate)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Gets validated declarations for agent between.
     *
     * @param agent     the agent
     * @param startDate the start date
     * @param endDate   the end date
     * @return the validated declarations for agent between
     */
// US10 - validated declarations of an agent between startDate and endDate (inclusive), ordered chronologically.
    public List<Declaration> getValidatedDeclarationsForAgentBetween(PoliticalAgent agent,
                                                                     Date startDate,
                                                                     Date endDate) {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            Date sd = d.getSubmissionDate();
            if (d.getStatus() == DeclarationStatus.VALIDATED
                    && d.getAgent().equals(agent)
                    && !sd.before(startDate)
                    && !sd.after(endDate)) {
                result.add(d);
            }
        }
        // chronological order by submission date (AC4)
        Collections.sort(result, new Comparator<Declaration>() {
            @Override
            public int compare(Declaration d1, Declaration d2) {
                return d1.getSubmissionDate().compareTo(d2.getSubmissionDate());
            }
        });
        return result;
    }
}
