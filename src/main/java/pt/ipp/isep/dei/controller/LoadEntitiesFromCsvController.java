package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.domain.graph.GraphDotExporter;
import pt.ipp.isep.dei.repository.GraphRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * Renders the loaded entities as an SVG file using Graphviz.
     * Produces a nodes-only graph (no edges) showing all entity types with their distinct shapes.
     *
     * @param outputSvgPath path where the SVG should be written.
     * @return the path of the generated SVG file.
     * @throws IOException if writing the DOT file fails.
     */
    public String renderEntitiesToSvg(String outputSvgPath) throws IOException {
        List<Entity> entities = graphRepository.getAll();
        String dot = GraphDotExporter.export(entities, new ArrayList<>());

        String dotPath;
        if (outputSvgPath.endsWith(".svg")) {
            dotPath = outputSvgPath.substring(0, outputSvgPath.length() - 4) + ".dot";
        } else {
            dotPath = outputSvgPath + ".dot";
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
        try {
            writer.write(dot);
        } finally {
            writer.close();
        }

        ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", dotPath, "-o", outputSvgPath);
        // Discard dot's stdout/stderr so a chatty process cannot fill the pipe
        // buffer and deadlock waitFor(); the SVG itself is written to the file.
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        pb.redirectError(ProcessBuilder.Redirect.DISCARD);
        try {
            Process process = pb.start();
            int code = process.waitFor();
            if (code != 0) {
                throw new RuntimeException("Graphviz 'dot' failed with exit code " + code
                        + ". Is Graphviz installed?");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Graph rendering was interrupted.", e);
        }
        return outputSvgPath;
    }
}
