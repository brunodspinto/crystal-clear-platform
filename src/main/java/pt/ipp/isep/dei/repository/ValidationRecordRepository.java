package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.ValidationRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving {@link ValidationRecord} instances.
 */
public class ValidationRecordRepository {

    private final List<ValidationRecord> records;

    public ValidationRecordRepository() {
        records = new ArrayList<>();
    }

    /**
     * Saves a validation record.
     *
     * @param record the record to save; cannot be null.
     * @return {@code true} if saved successfully.
     * @throws IllegalArgumentException if record is null.
     */
    public boolean save(ValidationRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Validation record cannot be null.");
        }
        return records.add(record);
    }

    /**
     * Returns all validation records.
     *
     * @return a defensive copy of all records.
     */
    public List<ValidationRecord> getAll() {
        return new ArrayList<>(records);
    }
}
