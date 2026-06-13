package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.ComplaintAssessment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing {@link ComplaintAssessment} records.
 */
public class ComplaintAssessmentRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<ComplaintAssessment> assessments;

    /**
     * Creates an empty ComplaintAssessmentRepository.
     */
    public ComplaintAssessmentRepository() {
        assessments = new ArrayList<>();
    }

    /**
     * Saves a complaint assessment.
     *
     * @param assessment the assessment to save.
     * @return {@code true} always.
     */
    public boolean save(ComplaintAssessment assessment) {
        return assessments.add(assessment);
    }

    /**
     * Returns an unmodifiable copy of all assessments.
     *
     * @return list of all complaint assessments.
     */
    public List<ComplaintAssessment> getAssessments() {
        return new ArrayList<>(assessments);
    }
}
