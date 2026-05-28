package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;
import pt.ipp.isep.dei.domain.graph.SupportGraph;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

/**
 * Controller for US33 – build the global adjacency matrix of the support
 * graph (undirected, unweighted) for a given temporal snapshot date.
 *
 * <p>The support graph is derived on demand from a {@link NetworkSnapshot}
 * built for the requested date. Every directed edge active at that date is
 * treated as a single undirected link; edge labels and weights are dropped.</p>
 */
public class GlobalSupportMatrixController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller using the singleton repository.
     */
    public GlobalSupportMatrixController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository. Used in tests.
     *
     * @param graphRepository the graph repository
     */
    public GlobalSupportMatrixController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    /**
     * Returns true if the repository has entities loaded.
     *
     * @return true when entities are available
     */
    public boolean hasData() {
        return !graphRepository.getAll().isEmpty();
    }

    /**
     * Builds the support graph (undirected, unweighted) for the network state
     * active at the given date.
     *
     * @param date the snapshot date in yyyy-MM-dd format
     * @return the support graph built from the snapshot
     * @throws IllegalArgumentException if date is null or blank
     */
    public SupportGraph buildFor(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("date must not be blank");
        }
        NetworkSnapshot snapshot = NetworkSnapshot.of(
                date, graphRepository.getAll(), graphRepository.getEdges());
        return new SupportGraph(snapshot.getGraph());
    }
}
