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
                .filter(e -> e.id().equals("P-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("politician", person.type());
        assertEquals("2020-01-01", person.startDate());
        assertEquals("2024-01-01", person.endDate());
        assertEquals("Alice Smith", person.name());
        assertEquals("1980-05-10", person.birthDate());
        assertEquals("Portuguese", person.nationality());
    }

    @Test
    void ensureOrganizationsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Organization org = (Organization) entities.stream()
                .filter(e -> e.id().equals("O-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("company", org.type());
        assertEquals("Acme Corp", org.name());
        assertEquals("private", org.organizationType());
        assertEquals("Portugal", org.country());
    }

    @Test
    void ensurePositionsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Position pos = (Position) entities.stream()
                .filter(e -> e.id().equals("J-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("public", pos.type());
        assertEquals("Minister of Finance", pos.positionTitle());
        assertEquals("government", pos.positionType());
        assertEquals("O-001", pos.organizationId());
    }

    @Test
    void ensureAssetsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Asset asset = (Asset) entities.stream()
                .filter(e -> e.id().equals("A-001"))
                .findFirst()
                .orElseThrow();
        assertEquals("property", asset.type());
        assertEquals("real_estate", asset.assetType());
        assertEquals("Portugal", asset.country());
        assertEquals(500000.0, asset.estimatedValue());
    }

    @Test
    void ensureCommentsAndBlankLinesAreIgnored() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertTrue(entities.stream().noneMatch(e -> e.id().startsWith("#")));
    }

    @Test
    void ensureInvalidCategoryIsSkipped() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertEquals(8, entities.size());
    }
}
