package pt.ipp.isep.dei.domain.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Entity csv parser.
 */
public class EntityCsvParser {

    private EntityCsvParser() {}

    /**
     * Parse list.
     *
     * @param filePath the file path
     * @return the list
     * @throws IOException the io exception
     */
    public static List<Entity> parse(String filePath) throws IOException {
        List<Entity> entities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                Entity entity = parseLine(line);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    private static Entity parseLine(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 5) {
            return null;
        }
        String category = parts[0].trim().toLowerCase();
        String id = parts[1].trim();
        String type = parts[2].trim();
        String startDate = parts[3].trim();
        String endDate = parts[4].trim();

        switch (category) {
            case "person":
                if (parts.length < 8) return null;
                return new Person(id, type, startDate, endDate,
                        parts[5].trim(), parts[6].trim(), parts[7].trim());
            case "organization":
                if (parts.length < 8) return null;
                return new Organization(id, type, startDate, endDate,
                        parts[5].trim(), parts[6].trim(), parts[7].trim());
            case "position":
                if (parts.length < 8) return null;
                return new Position(id, type, startDate, endDate,
                        parts[5].trim(), parts[6].trim(), parts[7].trim());
            case "asset":
                if (parts.length < 8) return null;
                double value;
                try {
                    value = Double.parseDouble(parts[7].trim());
                } catch (NumberFormatException e) {
                    value = 0.0;
                }
                return new Asset(id, type, startDate, endDate,
                        parts[5].trim(), parts[6].trim(), value);
            default:
                return null;
        }
    }
}
