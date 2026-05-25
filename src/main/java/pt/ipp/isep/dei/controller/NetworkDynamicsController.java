package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US32: produces a sequence of discrete-time network snapshots
 * for a user-provided list of dates.
 */
public class NetworkDynamicsController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller using the singleton repository.
     */
    public NetworkDynamicsController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with an injected repository (used in tests).
     *
     * @param graphRepository the graph repository
     */
    public NetworkDynamicsController(GraphRepository graphRepository) {
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
     * Builds a snapshot of the network for each provided date.
     * The dates are processed in the order given; callers should sort them first.
     *
     * @param dates list of snapshot dates in yyyy-MM-dd format
     * @return list of snapshots, one per date
     * @throws IllegalArgumentException if dates is null or empty
     */
    public List<NetworkSnapshot> buildSnapshots(List<String> dates) {
        if (dates == null || dates.isEmpty()) {
            throw new IllegalArgumentException("At least one date must be provided.");
        }
        List<Entity> allEntities = graphRepository.getAll();
        List<Edge> allEdges = graphRepository.getEdges();
        List<NetworkSnapshot> snapshots = new ArrayList<>();
        for (String date : dates) {
            snapshots.add(NetworkSnapshot.of(date, allEntities, allEdges));
        }
        return snapshots;
    }
}
