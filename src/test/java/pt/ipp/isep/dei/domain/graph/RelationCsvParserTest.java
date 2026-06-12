package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;
class RelationCsvParserTest {

    @Test
    void ensureSampleFileIsParsed() throws IOException {
        List<Edge> edges = RelationCsvParser.parse("src/test/resources/graph/relations_sample.csv");
        assertEquals(14, edges.size());
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
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                RelationCsvParser.parse("src/test/resources/graph/does-not-exist.csv");
            }
        });
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

    @Test
    void ensureExtendedSchemaWithCommasIsParsed() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp,
                "id,type,startDate,endDate,entity1,entity2,weight\n"
                + "E1,relativeOf,2010-01-01,2030-01-01,P-001,P-002,1.0\n"
                + "E2,ownerOf,2015-05-01,,P-005,A-001,0.5\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(2, edges.size());
            Edge first = edges.get(0);
            assertEquals("P-001", first.getFromId());
            assertEquals("P-002", first.getToId());
            assertEquals("relativeOf", first.getLabel());
            assertEquals(1.0, first.getWeight());
            Edge second = edges.get(1);
            assertEquals("P-005", second.getFromId());
            assertEquals("A-001", second.getToId());
            assertEquals("ownerOf", second.getLabel());
            assertEquals(0.5, second.getWeight());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureExtendedSchemaHeaderStartingWithIdIsDetected() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp,
                "id;type;startDate;endDate;entity1;entity2;weight\n"
                + "E1;relativeOf;2010-01-01;;P-001;P-002;1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
            assertEquals("P-001", edges.get(0).getFromId());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureExtendedSchemaWithBadWeightIsSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp,
                "E1,relativeOf,2010-01-01,,P-001,P-002,not-a-number\n"
                + "E2,relativeOf,2010-01-01,,P-003,P-004,1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
            assertEquals("P-003", edges.get(0).getFromId());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    void ensureExtendedSchemaWithBlankEndpointsIsSkipped() throws IOException {
        Path tmp = Files.createTempFile("rel", ".csv");
        Files.writeString(tmp,
                "E1,relativeOf,2010-01-01,, ,P-002,1.0\n"
                + "E2,relativeOf,2010-01-01,,P-003, ,1.0\n"
                + "E3, ,2010-01-01,,P-005,P-006,1.0\n"
                + "E4,relativeOf,2010-01-01,,P-007,P-008,1.0\n");
        try {
            List<Edge> edges = RelationCsvParser.parse(tmp.toString());
            assertEquals(1, edges.size());
            assertEquals("P-007", edges.get(0).getFromId());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
