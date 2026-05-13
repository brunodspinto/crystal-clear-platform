package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US19 - Load entities from a CSV file into the graph.
 */
public class LoadEntitiesFromCsvController {

    private final GraphRepository graphRepository;

    /**
     * Creates a controller with the default repository.
     */
    public LoadEntitiesFromCsvController() {
        this.graphRepository = Repositories.getInstance().getGraphRepository();
    }

    /**
     * Creates a controller with a given repository.
     *
     * @param graphRepository the repository to store entities in.
     */
    public LoadEntitiesFromCsvController(GraphRepository graphRepository) {
        this.graphRepository = graphRepository;
    }

    /**
     * Reads entities from a CSV file and saves them to the graph.
     *
     * @param filePath path to the CSV file.
     * @return the number of entities loaded.
     * @throws IOException if the file cannot be read.
     */
    public int loadEntities(String filePath) throws IOException {
        List<Entity> entities = EntityCsvParser.parse(filePath);
        graphRepository.addAll(entities);
        return entities.size();
    }
}
