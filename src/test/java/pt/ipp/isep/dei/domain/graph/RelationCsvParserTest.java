package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RelationCsvParserTest {

    @Test
    void ensureSampleFileIsParsed() throws IOException {
        List<Edge> edges = RelationCsvParser.parse("src/test/resources/graph/relations_sample.csv");
        assertEquals(6, edges.size());
    }

    @Test
    void ensureCommentLinesAreSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "# this is a comment\nP-1;P-2;kinship;1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureBlankLinesAreSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "\nP-1;P-2;kinship;1.0\n\n\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureLineWithTooFewFieldsIsSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "P-1;P-2;kinship\nP-1;P-2;kinship;1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureLineWithBadWeightIsSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "P-1;P-2;kinship;not-a-number\nP-1;P-2;kinship;1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureLineWithBlankIdsIsSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, " ;P-2;kinship;1.0\nP-1; ;kinship;1.0\nP-1;P-2; ;1.0\nP-1;P-2;kinship;1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureFieldsAreReadIntoTheEdge() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "A;B;ownership;0.75\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            Edge e = edges.get(0);
            assertEquals("A", e.getFromId());
            assertEquals("B", e.getToId());
            assertEquals("ownership", e.getLabel());
            assertEquals(0.75, e.getWeight());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureMissingFileThrows() {
        assertThrows(IOException.class, () -> RelationCsvParser.parse("src/test/resources/graph/does-not-exist.csv"));
    }

    @Test
    void ensureEmptyFileGivesEmptyList() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp, "");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertTrue(edges.isEmpty());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
