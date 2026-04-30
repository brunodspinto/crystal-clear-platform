package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityCsvParserTest {

    private String resourcePath(String name) {
        URL url = getClass().getClassLoader().getResource(name);
        assertNotNull(url, "Test resource not found: " + name);
        return url.getPath();
    }

    @Test
    void ensureSampleFileLoadsAllEntities() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertEquals(8, entities.size());
    }

    @Test
    void ensurePersonsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Person person = (Person) entities.stream()
                .filter(e -> e.getId().equals("P-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("politician", person.getType());
        assertEquals("2020-01-01", person.getStartDate());
        assertEquals("2024-01-01", person.getEndDate());
        assertEquals("Alice Smith", person.getName());
        assertEquals("1980-05-10", person.getBirthDate());
        assertEquals("Portuguese", person.getNationality());
    }

    @Test
    void ensureOrganizationsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Organization org = (Organization) entities.stream()
                .filter(e -> e.getId().equals("O-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("company", org.getType());
        assertEquals("Acme Corp", org.getName());
        assertEquals("private", org.getOrganizationType());
        assertEquals("Portugal", org.getCountry());
    }

    @Test
    void ensurePositionsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Position pos = (Position) entities.stream()
                .filter(e -> e.getId().equals("J-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("public", pos.getType());
        assertEquals("Minister of Finance", pos.getPositionTitle());
        assertEquals("government", pos.getPositionType());
        assertEquals("O-001", pos.getOrganizationId());
    }

    @Test
    void ensureAssetsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Asset asset = (Asset) entities.stream()
                .filter(e -> e.getId().equals("A-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("property", asset.getType());
        assertEquals("real_estate", asset.getAssetType());
        assertEquals("Portugal", asset.getCountry());
        assertEquals(500000.0, asset.getEstimatedValue());
    }

    @Test
    void ensureCommentsAndBlankLinesAreIgnored() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertTrue(entities.stream().noneMatch(e -> e.getId().startsWith("#")));
    }

    @Test
    void ensureInvalidCategoryIsSkipped() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertEquals(8, entities.size());
    }
}
