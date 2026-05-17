package pt.ipp.isep.dei.domain.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads entities from a CSV file and produces a list of Entity instances.
 * <p>
 * Auto-detects the field separator (',' or ';'), an optional header line,
 * and whether the first column holds the entity category. When the category
 * column is absent it is inferred from the identifier prefix:
 * P then person, O then organization, J then position, A then asset.
 * Lines that are empty or start with '#' are ignored.
 */
public class EntityCsvParser {

    private EntityCsvParser() {}

    /**
     * Parses the given CSV file and returns the list of entities.
     *
     * @param filePath the file path
     * @return the list of entities found in the file
     * @throws IOException if the file cannot be read
     */
    public static List<Entity> parse(String filePath) throws IOException {
        List<Entity> entities = new ArrayList<>();
        ArrayList<String> dataLines = readDataLines(filePath);
        if (dataLines.isEmpty()) return entities;

        char separator = detectSeparator(dataLines.get(0));
        int start = 0;
        if (looksLikeHeader(dataLines.get(0), separator)) {
            start = 1;
        }
        for (int i = start; i < dataLines.size(); i++) {
            Entity entity = parseLine(dataLines.get(i), separator);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
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
     * (such as "id", "category", "type") rather than a data value.
     */
    private static boolean looksLikeHeader(String line, char separator) {
        int sepIdx = line.indexOf(separator);
        String firstField;
        if (sepIdx == -1) firstField = line;
        else firstField = line.substring(0, sepIdx);
        firstField = firstField.trim().toLowerCase();
        if (isKnownCategory(firstField)) return false;
        return firstField.equals("id") || firstField.equals("category")
                || firstField.equals("type") || firstField.equals("name");
    }

    /**
     * Parses one data line into an Entity, or returns null if the line is
     * malformed or refers to an unknown category.
     */
    private static Entity parseLine(String line, char separator) {
        String[] parts = line.split(String.valueOf(separator), -1);
        if (parts.length < 5) {
            return null;
        }

        String firstField = parts[0].trim().toLowerCase();
        boolean hasCategoryColumn = isKnownCategory(firstField);

        int offset;
        String category;
        if (hasCategoryColumn) {
            category = firstField;
            offset = 1;
        } else {
            category = inferCategoryFromId(parts[0].trim());
            offset = 0;
        }
        if (category == null) return null;
        if (parts.length < offset + 7) return null;

        String id = parts[offset].trim();
        String type = parts[offset + 1].trim();
        String startDate = parts[offset + 2].trim();
        String endDate = parts[offset + 3].trim();
        String url = parts.length > offset + 7 ? parts[offset + 7].trim() : "";

        Entity entity;
        switch (category) {
            case "person":
                entity = new Person(id, type, startDate, endDate,
                        parts[offset + 4].trim(), parts[offset + 5].trim(), parts[offset + 6].trim());
                break;
            case "organization":
                entity = new Organization(id, type, startDate, endDate,
                        parts[offset + 4].trim(), parts[offset + 5].trim(), parts[offset + 6].trim());
                break;
            case "position":
                entity = new Position(id, type, startDate, endDate,
                        parts[offset + 4].trim(), parts[offset + 5].trim(), parts[offset + 6].trim());
                break;
            case "asset":
                double value;
                try {
                    value = Double.parseDouble(parts[offset + 6].trim());
                } catch (NumberFormatException e) {
                    value = 0.0;
                }
                entity = new Asset(id, type, startDate, endDate,
                        parts[offset + 4].trim(), parts[offset + 5].trim(), value);
                break;
            default:
                return null;
        }
        if (!url.isEmpty()) {
            entity.setUrl(url);
        }
        return entity;
    }

    /**
     * Returns true when the lowercased value matches a known entity category.
     */
    private static boolean isKnownCategory(String value) {
        return value.equals("person") || value.equals("organization")
                || value.equals("position") || value.equals("asset");
    }

    /**
     * Maps the first character of an identifier to a known category, or
     * returns null when no convention matches.
     */
    private static String inferCategoryFromId(String id) {
        if (id.isEmpty()) return null;
        char prefix = Character.toUpperCase(id.charAt(0));
        switch (prefix) {
            case 'P': return "person";
            case 'O': return "organization";
            case 'J': return "position";
            case 'A': return "asset";
            default:  return null;
        }
    }
}
