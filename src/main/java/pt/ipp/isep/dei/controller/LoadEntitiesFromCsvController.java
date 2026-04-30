package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.List;

public class LoadEntitiesFromCsvController {

    private final GraphRepository graphRepository;

    public LoadEntitiesFromCsvController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    public LoadEntitiesFromCsvController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    public int loadEntities(String filePath) throws IOException {
        List<Entity> entities = EntityCsvParser.parse(filePath);
        graphRepository.addAll(entities);
        return entities.size();
    }
}
