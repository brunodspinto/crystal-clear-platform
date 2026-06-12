package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Repository for storing and retrieving {@link Declaration} instances.
 */
public class DeclarationRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Declaration> declarations;

    /**
     * Creates an empty DeclarationRepository.
     */
    public DeclarationRepository() {
        declarations = new ArrayList<>();
    }

    /**
     * After loading the persisted declarations, advances the id counter past the
     * highest id already in use so new declarations do not collide with the ones
     * restored from disk.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        for (Declaration d : declarations) {
            Declaration.registerLoadedId(d.getId());
        }
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
     * Returns all declarations submitted by a given agent, in any status (US06 AC2/AC3).
     *
     * @param agent the political agent who owns the declarations.
     * @return list of the agent's declarations, possibly empty.
     */
    public List<Declaration> getDeclarationsByAgent(PoliticalAgent agent) {
        List<Declaration> result = new ArrayList<>();
        if (agent == null) {
            return result;
        }
        for (Declaration d : declarations) {
            if (agent.equals(d.getAgent())) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Returns the declaration with the given id, or {@code null} if none exists.
     *
     * @param declarationId the declaration id (format {@code DECL-<n>}).
     * @return the matching declaration, or {@code null}.
     */
    public Declaration getById(String declarationId) {
        if (declarationId == null) {
            return null;
        }
        for (Declaration d : declarations) {
            if (declarationId.equals(d.getId())) {
                return d;
            }
        }
        return null;
    }

    /**
     * Returns all declarations of a given agent with a given status.
     *
     * @param agent  the political agent who owns the declarations.
     * @param status the status to filter by.
     * @return list of the agent's declarations matching the status.
     */
    public List<Declaration> getDeclarationsForAgentByStatus(PoliticalAgent agent,
                                                             DeclarationStatus status) {
        List<Declaration> result = new ArrayList<>();
        if (agent == null) {
            return result;
        }
        for (Declaration d : declarations) {
            if (d.getStatus() == status && agent.equals(d.getAgent())) {
                result.add(d);
            }
        }
        return result;
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
        Date endOfDay = endOfDay(referenceDate);
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            if (d.getStatus() == DeclarationStatus.VALIDATED
                    && d.getAgent().equals(agent)
                    && !d.getSubmissionDate().after(endOfDay)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Returns the last instant (23:59:59.999) of the day the given date falls
     * on. The reference date is typed without a time of day, so it is parsed at
     * midnight; rolling it to the end of the day makes the comparison inclusive
     * of declarations submitted later on that same day.
     *
     * @param date the date to roll to the end of its day.
     * @return a new date at the last millisecond of that day.
     */
    private Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
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
        Date endOfDay = endOfDay(endDate);
        List<Declaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            Date sd = d.getSubmissionDate();
            if (d.getStatus() == DeclarationStatus.VALIDATED
                    && d.getAgent().equals(agent)
                    && !sd.before(startDate)
                    && !sd.after(endOfDay)) {
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
