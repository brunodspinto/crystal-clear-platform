package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the heterogeneous network at a single point in time.
 * Entities and edges active at the snapshot date are included; all others are excluded.
 */
public class NetworkSnapshot {

    private final String date;
    private final List<Entity> activeEntities;
    private final List<Edge> activeEdges;
    private final RelationGraph graph;

    private NetworkSnapshot(String date, List<Entity> activeEntities,
                            List<Edge> activeEdges, RelationGraph graph) {
        this.date = date;
        this.activeEntities = activeEntities;
        this.activeEdges = activeEdges;
        this.graph = graph;
    }

    /**
     * Builds a snapshot for the given date from all known entities and edges.
     * An entity or edge with no startDate is treated as always active;
     * one with no endDate is treated as still active.
     *
     * @param date       the snapshot date in yyyy-MM-dd format
     * @param allEntities all entities in the repository
     * @param allEdges    all edges in the repository
     * @return the snapshot for that date
     * @throws IllegalArgumentException if date is null or blank
     */
    public static NetworkSnapshot of(String date, List<Entity> allEntities, List<Edge> allEdges) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("date must not be blank");
        }
        List<Entity> activeEntities = new ArrayList<>();
        for (Entity e : allEntities) {
            if (e.isActiveAt(date)) {
                activeEntities.add(e);
            }
        }
        List<Edge> activeEdges = new ArrayList<>();
        for (Edge e : allEdges) {
            if (e.isActiveAt(date)
                    && containsId(activeEntities, e.getFromId())
                    && containsId(activeEntities, e.getToId())) {
                activeEdges.add(e);
            }
        }
        RelationGraph graph = GraphBuilder.build(activeEntities, activeEdges);
        return new NetworkSnapshot(date, activeEntities, activeEdges, graph);
    }

    /**
     * Returns true if the list contains an entity with the given id.
     *
     * @param entities the entities to search
     * @param id        the entity id to look for
     * @return true when an entity with that id is present
     */
    private static boolean containsId(List<Entity> entities, String id) {
        for (Entity e : entities) {
            if (e.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the snapshot date.
     *
     * @return the date in yyyy-MM-dd format
     */
    public String getDate() {
        return date;
    }

    /**
     * Gets active entities.
     *
     * @return a defensive copy of the list of entities active at this date
     */
    public List<Entity> getActiveEntities() {
        return new ArrayList<>(activeEntities);
    }

    /**
     * Gets active edges.
     *
     * @return a defensive copy of the list of edges active at this date
     */
    public List<Edge> getActiveEdges() {
        return new ArrayList<>(activeEdges);
    }

    /**
     * Gets the relation graph for this snapshot.
     *
     * @return the graph built from active entities and edges
     */
    public RelationGraph getGraph() {
        return graph;
    }

    /**
     * Returns the number of active entities.
     *
     * @return entity count
     */
    public int getEntityCount() {
        return activeEntities.size();
    }

    /**
     * Returns the number of active edges.
     *
     * @return edge count
     */
    public int getEdgeCount() {
        return activeEdges.size();
    }
}
