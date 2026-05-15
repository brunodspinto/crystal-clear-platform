package pt.ipp.isep.dei.domain.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads relations between entities from a CSV file and produces a list of
 * Edge instances. Format: from_id;to_id;label;weight
 * <p>
 * Lines that are empty or start with '#' are ignored. Lines that have less
 * than 4 fields, or whose weight cannot be parsed as a double, are skipped.
 */
public class RelationCsvParser {

    private RelationCsvParser() {}

    /**
     * Parse list.
     *
     * @param filePath the file path
     * @return the list
     * @throws IOException the io exception
     */
    public static List<Edge> parse(String filePath) throws IOException {
        List<Edge> edges = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                Edge edge = parseLine(line);
                if (edge != null) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    private static Edge parseLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 4) {
            return null;
        }
        String fromId = parts[0].trim();
        String toId = parts[1].trim();
        String label = parts[2].trim();
        double weight;
        try {
            weight = Double.parseDouble(parts[3].trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (fromId.isBlank() || toId.isBlank() || label.isBlank()) {
            return null;
        }
        return new Edge(fromId, toId, label, weight);
    }
}
