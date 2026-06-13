package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Person;
import pt.ipp.isep.dei.domain.graph.SupportGraph;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalSupportMatrixControllerTest {

    private GraphRepository repoWith(List<Person> people, List<Edge> edges) {
        GraphRepository repo = new GraphRepository();
        repo.addAll(new ArrayList<>(people));
        repo.setEdges(edges);
        return repo;
    }

    private Person person(String id, String start, String end) {
        return new Person(id, "politician", start, end, "Person " + id, "1980-01-01", "PT");
    }

    @Test
    void ensureHasDataReturnsFalseWhenEmpty() {
        GlobalSupportMatrixController controller =
                new GlobalSupportMatrixController(new GraphRepository());
        assertFalse(controller.hasData());
    }

    @Test
    void ensureHasDataReturnsTrueWhenEntitiesLoaded() {
        GraphRepository repo = repoWith(
                Arrays.asList(person("p1", "", "")),
                new ArrayList<>());
        assertTrue(new GlobalSupportMatrixController(repo).hasData());
    }

    @Test
    void ensureBuildForBlankDateThrows() {
        GlobalSupportMatrixController controller =
                new GlobalSupportMatrixController(new GraphRepository());
        assertThrows(IllegalArgumentException.class, () -> controller.buildFor(""));
        assertThrows(IllegalArgumentException.class, () -> controller.buildFor(null));
    }

    @Test
    void ensureBuildForReturnsSupportGraphSizedToActiveEntities() {
        GraphRepository repo = repoWith(
                Arrays.asList(
                        person("p1", "", ""),
                        person("p2", "", ""),
                        person("p3", "", "")),
                new ArrayList<>());

        SupportGraph sg = new GlobalSupportMatrixController(repo).buildFor("2026-05-27");
        assertEquals(3, sg.size());
    }

    @Test
    void ensureMatrixIsSymmetric() {
        List<Person> people = Arrays.asList(
                person("p1", "", ""),
                person("p2", "", ""));
        List<Edge> edges = Arrays.asList(
                new Edge("p1", "p2", "relativeOf", 1.0));
        GraphRepository repo = repoWith(people, edges);

        SupportGraph sg = new GlobalSupportMatrixController(repo).buildFor("2026-05-27");
        boolean[][] m = sg.getAdjacencyMatrix();
        int n = sg.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                assertEquals(m[i][j], m[j][i],
                        "matrix must be symmetric at [" + i + "][" + j + "]");
            }
        }
    }

    @Test
    void ensureMatrixHasNoSelfLoops() {
        List<Person> people = Arrays.asList(
                person("p1", "", ""),
                person("p2", "", ""));
        List<Edge> edges = Arrays.asList(
                new Edge("p1", "p1", "relativeOf", 1.0),
                new Edge("p1", "p2", "relativeOf", 1.0));
        GraphRepository repo = repoWith(people, edges);

        SupportGraph sg = new GlobalSupportMatrixController(repo).buildFor("2026-05-27");
        boolean[][] m = sg.getAdjacencyMatrix();
        for (int i = 0; i < sg.size(); i++) {
            assertFalse(m[i][i], "no self-loop expected at index " + i);
        }
    }

    @Test
    void ensureReverseDirectionEdgeProducesSameAdjacency() {
        List<Person> people = Arrays.asList(
                person("a", "", ""),
                person("b", "", ""));
        List<Edge> edgesAB = Arrays.asList(new Edge("a", "b", "relativeOf", 1.0));
        List<Edge> edgesBA = Arrays.asList(new Edge("b", "a", "relativeOf", 1.0));

        SupportGraph ab = new GlobalSupportMatrixController(repoWith(people, edgesAB))
                .buildFor("2026-05-27");
        SupportGraph ba = new GlobalSupportMatrixController(repoWith(people, edgesBA))
                .buildFor("2026-05-27");

        assertTrue(ab.isAdjacent("a", "b"));
        assertTrue(ab.isAdjacent("b", "a"));
        assertTrue(ba.isAdjacent("a", "b"));
        assertTrue(ba.isAdjacent("b", "a"));
    }

    @Test
    void ensureEdgesInactiveAtDateAreExcluded() {
        List<Person> people = Arrays.asList(
                person("p1", "", ""),
                person("p2", "", ""));
        List<Edge> edges = Arrays.asList(
                new Edge("p1", "p2", "relativeOf", 1.0, "2020-01-01", "2021-01-01"));
        GraphRepository repo = repoWith(people, edges);

        SupportGraph sg = new GlobalSupportMatrixController(repo).buildFor("2026-05-27");
        assertFalse(sg.isAdjacent("p1", "p2"));
    }

    @Test
    void ensureEntitiesInactiveAtDateAreExcluded() {
        List<Person> people = Arrays.asList(
                person("p1", "", ""),
                person("p2", "2020-01-01", "2021-01-01"));
        GraphRepository repo = repoWith(people, new ArrayList<>());

        SupportGraph sg = new GlobalSupportMatrixController(repo).buildFor("2026-05-27");
        assertEquals(1, sg.size());
    }
}
