package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.graph.Entity;

import java.util.ArrayList;
import java.util.List;

public class GraphRepository {

    private final List<Entity> entities;

    public GraphRepository() {
        entities = new ArrayList<>();
    }

    public void addAll(List<Entity> newEntities) {
        entities.addAll(newEntities);
    }

    public List<Entity> getAll() {
        return List.copyOf(entities);
    }
}
