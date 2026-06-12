package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
class EdgeTest {

    @Test
    void ensureFieldsAreKept() {
        Edge e = new Edge("a", "b", "kinship", 0.5);
        assertEquals("a", e.getFromId());
        assertEquals("b", e.getToId());
        assertEquals("kinship", e.getLabel());
        assertEquals(0.5, e.getWeight());
    }

    @Test
    void ensureBlankIdsAreRejected() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Edge("", "b", "kinship", 1);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Edge("a", " ", "kinship", 1);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Edge("a", "b", "", 1);
            }
        });
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                new Edge(null, "b", "kinship", 1);
            }
        });
    }

    @Test
    void ensureEqualityIsValueBased() {
        Edge a = new Edge("a", "b", "kinship", 0.5);
        Edge b = new Edge("a", "b", "kinship", 0.5);
        assertEquals(a, b);
    }
}
