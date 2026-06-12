package pt.ipp.isep.dei.domain.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads snapshot dates from a CSV file for US32.
 * <p>
 * The expected format is a "SnapshotDate" header line followed by one date
 * per line in yyyy-MM-dd format. The header, empty lines and lines that do
 * not hold a valid date are ignored, so files with or without the header
 * are both accepted.
 */
public class SnapshotDateCsvParser {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    private SnapshotDateCsvParser() {}

    /**
     * Parses the given CSV file and returns the valid dates found in it.
     * Duplicated dates are returned only once.
     *
     * @param filePath the file path
     * @return the list of dates in yyyy-MM-dd format
     * @throws IOException if the file cannot be read
     */
    public static List<String> parse(String filePath) throws IOException {
        List<String> dates = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String value = firstField(line);
                if (isValidDate(value) && !dates.contains(value)) {
                    dates.add(value);
                }
            }
        } finally {
            reader.close();
        }
        return dates;
    }

    /**
     * Returns the first field of the line, so lines that end with a
     * separator (',' or ';') are also accepted.
     */
    private static String firstField(String line) {
        String trimmed = line.trim();
        int cut = trimmed.length();
        int comma = trimmed.indexOf(',');
        if (comma >= 0 && comma < cut) {
            cut = comma;
        }
        int semi = trimmed.indexOf(';');
        if (semi >= 0 && semi < cut) {
            cut = semi;
        }
        return trimmed.substring(0, cut).trim();
    }

    /**
     * Returns true when the value matches the yyyy-MM-dd pattern and is a
     * real calendar date.
     */
    private static boolean isValidDate(String value) {
        if (!value.matches(DATE_PATTERN)) {
            return false;
        }
        try {
            LocalDate.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
