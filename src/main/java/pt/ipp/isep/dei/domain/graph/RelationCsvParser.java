package pt.ipp.isep.dei.domain.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads relations between entities from a CSV file and produces a list of
 * Edge instances.
 * <p>
 * Auto-detects the field separator (',' or ';') and an optional header line.
 * Two schemas are accepted:
 * the legacy four-column form (from_id, to_id, label, weight) and the
 * seven-column form (id, type, startDate, endDate, entity1, entity2, weight)
 * in which entity1 and entity2 are read as the edge endpoints, type as the
 * label, and weight as the edge weight.
 * Lines that are empty or start with '#' are ignored. Lines with too few
 * fields, blank ids or labels, or an unparseable weight are skipped.
 */
public class RelationCsvParser {

    private RelationCsvParser() {}

    /**
     * Parses the given CSV file and returns the list of edges.
     *
     * @param filePath the file path
     * @return the list of edges found in the file
     * @throws IOException if the file cannot be read
     */
    public static List<Edge> parse(String filePath) throws IOException {
        List<Edge> edges = new ArrayList<>();
        ArrayList<String> dataLines = readDataLines(filePath);
        if (dataLines.isEmpty()) return edges;

        char separator = detectSeparator(dataLines.get(0));
        int start = 0;
        if (looksLikeHeader(dataLines.get(0), separator)) {
            start = 1;
        }
        for (int i = start; i < dataLines.size(); i++) {
            Edge edge = parseLine(dataLines.get(i), separator);
            if (edge != null) {
                edges.add(edge);
            }
        }
        return edges;
    }

    /**
     * Reads the file and returns every line that is not empty or a comment.
     */
    private static ArrayList<String> readDataLines(String filePath) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                lines.add(trimmed);
            }
        } finally {
            reader.close();
        }
        return lines;
    }

    /**
     * Returns ';' when the line has at least as many semicolons as commas,
     * otherwise returns ','.
     */
    private static char detectSeparator(String line) {
        int semis = 0;
        int commas = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == ';') semis++;
            else if (c == ',') commas++;
        }
        if (semis >= commas) return ';';
        return ',';
    }

    /**
     * Returns true when the first field of the line looks like a column name
     * for a relations file.
     */
    private static boolean looksLikeHeader(String line, char separator) {
        int sepIdx = line.indexOf(separator);
        String firstField;
        if (sepIdx == -1) firstField = line;
        else firstField = line.substring(0, sepIdx);
        firstField = firstField.trim().toLowerCase();
        return firstField.equals("from_id") || firstField.equals("from")
                || firstField.equals("source") || firstField.equals("src")
                || firstField.equals("origin") || firstField.equals("id");
    }

    /**
     * Parses one data line into an Edge, or returns null if the line is
     * malformed or any required field is blank. Four-column lines use the
     * legacy schema; lines with seven or more columns use the extended
     * schema where parts[4] is fromId, parts[5] is toId, parts[1] is the
     * label, and parts[6] is the weight.
     */
    private static Edge parseLine(String line, char separator) {
        String[] parts = line.split(String.valueOf(separator), -1);
        if (parts.length < 4) {
            return null;
        }

        String fromId;
        String toId;
        String label;
        double weight;
        if (parts.length >= 7) {
            fromId = parts[4].trim();
            toId = parts[5].trim();
            label = parts[1].trim();
            try {
                weight = Double.parseDouble(parts[6].trim());
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            fromId = parts[0].trim();
            toId = parts[1].trim();
            label = parts[2].trim();
            try {
                weight = Double.parseDouble(parts[3].trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (fromId.isBlank() || toId.isBlank() || label.isBlank()) {
            return null;
        }
        return new Edge(fromId, toId, label, weight);
    }
}
