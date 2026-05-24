package pt.ipp.isep.dei.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a supporting document attached to a Declaration of Interests.
 */
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String fileName;
    private final Date uploadDate;

    /**
     * Creates a new Attachment.
     *
     * @param fileName   the name of the uploaded file; cannot be null or blank.
     * @param uploadDate the date the file was uploaded; cannot be null.
     * @throws IllegalArgumentException if any argument is invalid.
     */
    public Attachment(String fileName, Date uploadDate) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }
        if (uploadDate == null) {
            throw new IllegalArgumentException("Upload date cannot be null.");
        }
        this.fileName = fileName;
        this.uploadDate = uploadDate;
    }

    /**
     * Gets file name.
     *
     * @return the name of the uploaded file.
     */
    public String getFileName() { return fileName; }

    /**
     * Gets upload date.
     *
     * @return the date the file was uploaded.
     */
    public Date getUploadDate() { return uploadDate; }

    @Override
    public String toString() {
        return String.format("Attachment{fileName='%s'}", fileName);
    }
}
