package pt.ipp.isep.dei.domain.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.function.Executable;
class GraphBuilderTest {

    private Person person(String id) {
        return new Person(id, "person", "", "", id + "-name", "1980-01-01", "Portuguese");
    }

    @Test
    void ensureAllEntitiesAreRegisteredAsNodes() {
        List<Entity> entities = Arrays.asList(person("A"), person("B"), person("C"));
        List<Edge> edges = new ArrayList<>();

        RelationGraph g = GraphBuilder.build(entities, edges);

        assertEquals(3, g.nodeCount());
        assertTrue(g.nodes().containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    void ensureEdgesAreAdded() {
        List<Entity> entities = Arrays.asList(person("A"), person("B"));
        List<Edge> edges = Arrays.asList(new Edge("A", "B", "kinship", 1.0));

        RelationGraph g = GraphBuilder.build(entities, edges);

        assertEquals(1, g.neighbors("A").size());
        assertEquals("B", g.neighbors("A").get(0).getToId());
    }

    @Test
    void ensureIsolatedEntitiesStillAppearAsNodes() {
        List<Entity> entities = Arrays.asList(person("A"), person("B"), person("C"));
        List<Edge> edges = Arrays.asList(new Edge("A", "B", "kinship", 1.0));

        RelationGraph g = GraphBuilder.build(entities, edges);

        assertTrue(g.nodes().contains("C"));
        assertTrue(g.neighbors("C").isEmpty());
    }

    @Test
    void ensureEdgeWithUnknownEntityIsStillAddedAsNode() {
        List<Entity> entities = Arrays.asList(person("A"));
        List<Edge> edges = Arrays.asList(new Edge("A", "Z", "ownership", 1.0));

        RelationGraph g = GraphBuilder.build(entities, edges);

        assertTrue(g.nodes().contains("Z"));
    }

    @Test
    void ensureNullEntitiesIsRejected() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                GraphBuilder.build(null, new ArrayList<>());
            }
        });
    }

    @Test
    void ensureNullEdgesIsRejected() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                GraphBuilder.build(new ArrayList<>(), null);
            }
        });
    }

    @Test
    void ensureEmptyInputsGiveEmptyGraph() {
        RelationGraph g = GraphBuilder.build(new ArrayList<>(), new ArrayList<>());
        assertEquals(0, g.nodeCount());
    }
}
