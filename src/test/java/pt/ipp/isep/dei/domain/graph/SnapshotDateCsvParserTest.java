package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotDateCsvParserTest {

    @TempDir
    Path tempDir;

    private String writeFile(String name, String[] lines) throws IOException {
        Path file = tempDir.resolve(name);
        PrintWriter writer = new PrintWriter(new FileWriter(file.toFile()));
        try {
            for (String line : lines) {
                writer.println(line);
            }
        } finally {
            writer.close();
        }
        return file.toString();
    }

    @Test
    void ensureParsesFileWithHeaderAndDates() throws IOException {
        String path = writeFile("dates.csv", new String[]{
                "SnapshotDate", "2010-01-01", "2011-02-28", "2012-03-12"});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertEquals(3, dates.size());
        assertEquals("2010-01-01", dates.get(0));
        assertEquals("2011-02-28", dates.get(1));
        assertEquals("2012-03-12", dates.get(2));
    }

    @Test
    void ensureParsesFileWithoutHeader() throws IOException {
        String path = writeFile("dates.csv", new String[]{
                "2010-01-01", "2011-02-28"});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertEquals(2, dates.size());
    }

    @Test
    void ensureSkipsEmptyAndInvalidLines() throws IOException {
        String path = writeFile("dates.csv", new String[]{
                "SnapshotDate", "", "not-a-date", "2010-13-40", "2010-01-01"});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertEquals(1, dates.size());
        assertEquals("2010-01-01", dates.get(0));
    }

    @Test
    void ensureDuplicatedDatesAreReturnedOnce() throws IOException {
        String path = writeFile("dates.csv", new String[]{
                "SnapshotDate", "2010-01-01", "2010-01-01", "2011-02-28"});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertEquals(2, dates.size());
    }

    @Test
    void ensureAcceptsLinesWithTrailingSeparator() throws IOException {
        String path = writeFile("dates.csv", new String[]{
                "SnapshotDate", "2010-01-01,", "2011-02-28;"});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertEquals(2, dates.size());
        assertEquals("2010-01-01", dates.get(0));
        assertEquals("2011-02-28", dates.get(1));
    }

    @Test
    void ensureEmptyFileReturnsEmptyList() throws IOException {
        String path = writeFile("dates.csv", new String[]{});

        List<String> dates = SnapshotDateCsvParser.parse(path);

        assertTrue(dates.isEmpty());
    }

    @Test
    void ensureMissingFileThrowsIOException() {
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                SnapshotDateCsvParser.parse(tempDir.resolve("missing.csv").toString());
            }
        });
    }
}
