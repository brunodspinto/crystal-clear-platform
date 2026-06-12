package pt.ipp.isep.dei.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class ValidationCommentTest {

    @Test
    void ensureCreationWorks() {
        ValidationComment vc = new ValidationComment("Assets", "Missing vehicle description.");
        assertNotNull(vc);
    }

    @Test
    void ensureCreationFailsWithNullSection() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ValidationComment(null, "Some comment.");
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankSection() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ValidationComment("   ", "Some comment.");
            }
        });
    }

    @Test
    void ensureCreationFailsWithNullComment() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ValidationComment("Assets", null);
            }
        });
    }

    @Test
    void ensureCreationFailsWithBlankComment() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new ValidationComment("Assets", "   ");
            }
        });
    }

    @Test
    void ensureGettersReturnCorrectValues() {
        ValidationComment vc = new ValidationComment("Positions", "Incorrect start date.");
        assertEquals("Positions", vc.getSection());
        assertEquals("Incorrect start date.", vc.getComment());
    }
}
