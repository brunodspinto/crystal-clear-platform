package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityCsvParserTest {

    private String resourcePath(String name) {
        URL url = getClass().getClassLoader().getResource(name);
        assertNotNull(url, "Test resource not found: " + name);
        try {
            return Paths.get(url.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Entity findById(List<Entity> entities, String id) {
        for (Entity e : entities) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        fail("Entity not found: " + id);
        return null;
    }

    @Test
    void ensureSampleFileLoadsAllEntities() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertEquals(12, entities.size());
    }

    @Test
    void ensurePersonsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Person person = (Person) findById(entities, "P-001");
        assertEquals("politician", person.getType());
        assertEquals("2018-10-26", person.getStartDate());
        assertEquals("2024-03-10", person.getEndDate());
        assertEquals("António Silva", person.getName());
        assertEquals("1970-04-12", person.getBirthDate());
        assertEquals("Portuguese", person.getNationality());
    }

    @Test
    void ensureOrganizationsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Organization org = (Organization) findById(entities, "O-001");
        assertEquals("public", org.getType());
        assertEquals("Ministério da Economia", org.getName());
        assertEquals("public", org.getOrganizationType());
        assertEquals("Portugal", org.getCountry());
    }

    @Test
    void ensurePositionsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Position pos = (Position) findById(entities, "J-001");
        assertEquals("public", pos.getType());
        assertEquals("Secretário de Estado", pos.getPositionTitle());
        assertEquals("government", pos.getPositionType());
        assertEquals("O-001", pos.getOrganizationId());
    }

    @Test
    void ensureAssetsAreParsedCorrectly() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        Asset asset = (Asset) findById(entities, "A-001");
        assertEquals("property", asset.getType());
        assertEquals("real_estate", asset.getAssetType());
        assertEquals("Lisboa", asset.getCountry());
        assertEquals(450000.0, asset.getEstimatedValue());
    }

    @Test
    void ensureCommentsAndBlankLinesAreIgnored() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        for (Entity e : entities) {
            assertFalse(e.getId().startsWith("#"));
        }
    }

    @Test
    void ensureInvalidCategoryIsSkipped() throws IOException {
        List<Entity> entities = EntityCsvParser.parse(resourcePath("graph/entities_sample.csv"));
        assertEquals(12, entities.size());
    }
}
