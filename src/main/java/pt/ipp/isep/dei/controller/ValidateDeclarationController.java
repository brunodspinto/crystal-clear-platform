package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.mapper.DeclarationMapper;
import pt.ipp.isep.dei.repository.*;
import pt.isep.lei.esoft.auth.domain.model.Email;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
     *
     * @param declarationRepository           the declaration repository
     * @param validationRecordRepository      the validation record repository
     * @param ethicsCommitteeMemberRepository the ethics committee member repository
     * @param authenticationRepository        the authentication repository
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
     * Returns all declarations with PENDING status, converted to DTOs so the
     * UI does not depend on the domain layer (ESOFT — DTO pattern).
     *
     * @return list of pending declarations as {@link DeclarationDTO}.
     */
    public List<DeclarationDTO> getPendingDeclarations() {
        DeclarationMapper mapper = new DeclarationMapper();
        return mapper.toDTO(declarationRepository.getDeclarationsByStatus(DeclarationStatus.PENDING));
    }

    /**
     * Returns the full details of the selected declaration as a formatted string.
     *
     * @param declarationId the id of the declaration to inspect.
     * @return the details string, or an empty string if the id is unknown.
     */
    public String getDeclarationDetails(String declarationId) {
        Declaration declaration = declarationRepository.getById(declarationId);
        if (declaration == null) {
            return "";
        }
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
     * @param declarationId the id of the declaration to validate.
     * @param outcome       the validation outcome.
     * @param comments      list of comment arrays [String section, String comment];                    only used when outcome is RETURNED_FOR_CORRECTION.
     * @return {@code true} if successful; {@code false} if the id is unknown, the declaration         is not PENDING or the authenticated member was not found.
     */
    public boolean processValidation(String declarationId, ValidationOutcome outcome,
                                      List<Object[]> comments) {
        Declaration declaration = declarationRepository.getById(declarationId);
        if (declaration == null || declaration.getStatus() != DeclarationStatus.PENDING) {
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
        return ethicsCommitteeMemberRepository.getByEmail(email.getEmail());
    }
}
