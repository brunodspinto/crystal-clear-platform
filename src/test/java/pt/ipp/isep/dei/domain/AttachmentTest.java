package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AttachmentTest {

    private static final Date NOW = new Date();

    @Test
    void ensureAttachmentCreationWorks() {
        Attachment att = new Attachment("document.pdf", NOW);
        assertNotNull(att);
    }

    @Test
    void ensureAttachmentFailsWithNullFileName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Attachment(null, NOW));
    }

    @Test
    void ensureAttachmentFailsWithBlankFileName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Attachment("   ", NOW));
    }

    @Test
    void ensureAttachmentFailsWithNullUploadDate() {
        assertThrows(IllegalArgumentException.class, () ->
                new Attachment("document.pdf", null));
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        Attachment att = new Attachment("report.pdf", NOW);
        assertEquals("report.pdf", att.getFileName());
        assertEquals(NOW, att.getUploadDate());
    }
}
