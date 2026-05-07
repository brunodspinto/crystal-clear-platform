package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.repository.*;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller responsible for handling the validation of a Declaration of Interests (US08).
 */
public class ValidateDeclarationController {

    private final DeclarationRepository declarationRepository;
    private final ValidationRecordRepository validationRecordRepository;
    private final EthicsCommitteeMemberRepository ethicsCommitteeMemberRepository;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Creates a controller using the singleton repositories.
     */
    public ValidateDeclarationController() {
        Repositories repos = Repositories.getInstance();
        this.declarationRepository = repos.getDeclarationRepository();
        this.validationRecordRepository = repos.getValidationRecordRepository();
        this.ethicsCommitteeMemberRepository = repos.getEthicsCommitteeMemberRepository();
        this.authenticationRepository = repos.getAuthenticationRepository();
    }

    /**
     * Creates a controller with injected repositories (used in tests).
     */
    public ValidateDeclarationController(DeclarationRepository declarationRepository,
                                          ValidationRecordRepository validationRecordRepository,
                                          EthicsCommitteeMemberRepository ethicsCommitteeMemberRepository,
                                          AuthenticationRepository authenticationRepository) {
        this.declarationRepository = declarationRepository;
        this.validationRecordRepository = validationRecordRepository;
        this.ethicsCommitteeMemberRepository = ethicsCommitteeMemberRepository;
        this.authenticationRepository = authenticationRepository;
    }

    /**
     * Returns all declarations with PENDING status.
     *
     * @return list of pending {@link Declaration} instances.
     */
    public List<Declaration> getPendingDeclarations() {
        return declarationRepository.getDeclarationsByStatus(DeclarationStatus.PENDING);
    }

    /**
     * Returns the full details of the selected declaration as a formatted string.
     *
     * @param declaration the declaration to inspect.
     * @return the details string.
     */
    public String getDeclarationDetails(Declaration declaration) {
        return declaration.getDetails();
    }

    /**
     * Returns all available validation outcomes.
     *
     * @return list of {@link ValidationOutcome} values.
     */
    public List<ValidationOutcome> getValidationOutcomes() {
        return Arrays.asList(ValidationOutcome.values());
    }

    /**
     * Processes the validation of a declaration (US08).
     * AC3: the declaration must be in PENDING status — enforced before any mutation.
     * The declaration status is updated and a ValidationRecord is created and persisted.
     * If the outcome is RETURNED_FOR_CORRECTION, all provided comments are added to the record.
     *
     * @param declaration the declaration to validate.
     * @param outcome     the validation outcome.
     * @param comments    list of comment arrays [String section, String comment];
     *                    only used when outcome is RETURNED_FOR_CORRECTION.
     * @return {@code true} if successful; {@code false} if the declaration is not PENDING
     *         or the authenticated member was not found.
     */
    public boolean processValidation(Declaration declaration, ValidationOutcome outcome,
                                      List<Object[]> comments) {
        if (declaration.getStatus() != DeclarationStatus.PENDING) {
            return false;
        }

        EthicsCommitteeMember member = getCurrentEthicsCommitteeMember();
        if (member == null) {
            return false;
        }

        declaration.setStatus(outcome);
        declarationRepository.save(declaration);

        ValidationRecord record = new ValidationRecord(member, declaration, new Date(), outcome);

        if (outcome == ValidationOutcome.RETURNED_FOR_CORRECTION && comments != null) {
            for (Object[] c : comments) {
                record.addComment((String) c[0], (String) c[1]);
            }
        }

        return validationRecordRepository.save(record);
    }

    /**
     * Retrieves the currently authenticated Ethics Committee Member from the session.
     *
     * @return the {@link EthicsCommitteeMember}, or {@code null} if not found.
     */
    private EthicsCommitteeMember getCurrentEthicsCommitteeMember() {
        Email email = authenticationRepository.getCurrentUserSession().getUserId();
        Optional<EthicsCommitteeMember> member =
                ethicsCommitteeMemberRepository.getByEmail(email.getEmail());
        return member.orElse(null);
    }
}
