package pt.ipp.isep.dei.controller;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateAdjacencyMatricesControllerTest {

    @Test
    void ensureThrowsWhenGraphIsMissing() {
        GraphRepository repo = new GraphRepository();
        GenerateAdjacencyMatricesController controller = new GenerateAdjacencyMatricesController(repo);

        assertThrows(IllegalStateException.class, controller::generate);
    }

    @Test
    void ensureMatricesAreGroupedByLabel() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("A", "B", "kinship", 1.0));
        graph.addEdge(new Edge("B", "C", "employment", 0.5));
        graph.addEdge(new Edge("A", "C", "kinship", 0.8));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        GenerateAdjacencyMatricesController controller = new GenerateAdjacencyMatricesController(repo);

        List<LabeledAdjacencyMatrix> matrices = controller.generate();
        assertEquals(2, matrices.size());
        assertTrue(containsLabel(matrices, "kinship"));
        assertTrue(containsLabel(matrices, "employment"));
    }

    @Test
    void ensureNonZeroEntryCountMatchesEdges() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("A", "B", "kinship", 1.0));
        graph.addEdge(new Edge("B", "C", "kinship", 1.0));
        graph.addEdge(new Edge("C", "D", "employment", 0.7));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        GenerateAdjacencyMatricesController controller = new GenerateAdjacencyMatricesController(repo);

        List<LabeledAdjacencyMatrix> matrices = controller.generate();
        assertEquals(2, controller.countNonZeroEntries(matrixFor(matrices, "kinship")));
        assertEquals(1, controller.countNonZeroEntries(matrixFor(matrices, "employment")));
    }

    @Test
    void ensureAllMatricesShareTheSameSize() {
        RelationGraph graph = new RelationGraph();
        graph.addEdge(new Edge("A", "B", "kinship", 1.0));
        graph.addEdge(new Edge("C", "D", "employment", 1.0));

        GraphRepository repo = new GraphRepository();
        repo.setRelationGraph(graph);
        GenerateAdjacencyMatricesController controller = new GenerateAdjacencyMatricesController(repo);

        List<LabeledAdjacencyMatrix> matrices = controller.generate();
        int expectedSize = graph.nodeCount();
        for (LabeledAdjacencyMatrix entry : matrices) {
            assertEquals(expectedSize, entry.getMatrix().getSize());
        }
    }

    private boolean containsLabel(List<LabeledAdjacencyMatrix> matrices, String label) {
        for (LabeledAdjacencyMatrix entry : matrices) {
            if (entry.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    private AdjacencyMatrix matrixFor(List<LabeledAdjacencyMatrix> matrices, String label) {
        for (LabeledAdjacencyMatrix entry : matrices) {
            if (entry.getLabel().equals(label)) {
                return entry.getMatrix();
            }
        }
        throw new IllegalArgumentException("matrix not found: " + label);
    }
}
