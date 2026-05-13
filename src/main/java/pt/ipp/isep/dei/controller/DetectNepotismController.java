package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.NepotismDetector;
import pt.ipp.isep.dei.domain.graph.NepotismDetector.NepotismPair;
import pt.ipp.isep.dei.domain.graph.RelationGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.List;

/**
 * Controller for US22 – Find out if someone was appointed by a relative,
 * friend, or associate.
 *
 * <p>Retrieves the current {@link RelationGraph} from the repository and
 * delegates detection to {@link NepotismDetector}. AC1 is satisfied by
 * returning <em>all</em> matching pairs.</p>
 */
public class DetectNepotismController {

    private final GraphRepository graphRepository;
    private final NepotismDetector detector;

    public DetectNepotismController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
        this.detector = new NepotismDetector();
    }

    /** Constructor used in unit tests to inject dependencies. */
    public DetectNepotismController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
        this.detector = new NepotismDetector();
    }

    /**
     * Runs the nepotism detection and returns all pairs where the appointer
     * has a personal relationship (relative, friend, or associate) with the
     * appointed person.
     *
     * @return list of {@link NepotismPair} objects (may be empty)
     * @throws IllegalStateException if no relations graph has been built yet
     */
    public List<NepotismPair> detect() {
        RelationGraph graph = graphRepository.getRelationGraph();
        if (graph == null) {
            throw new IllegalStateException(
                    "No relations graph available. Build the graph first (US20).");
        }
        return detector.detect(graph);
    }
}
