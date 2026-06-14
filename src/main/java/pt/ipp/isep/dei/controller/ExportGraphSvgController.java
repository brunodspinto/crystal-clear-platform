package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.domain.graph.GraphDotExporter;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Controller for US26 - Export the graph as an SVG file using Graphviz.
 */
public class ExportGraphSvgController {

    /**
     * Creates a controller.
     */
    public ExportGraphSvgController() {}

    /**
     * Builds the graph from two CSV files and exports it as an SVG file via Graphviz.
     *
     * @param entitiesCsvPath  path to the CSV file with entities.
     * @param relationsCsvPath path to the CSV file with relations.
     * @param outputSvgPath    path where the SVG file will be saved.
     * @return the number of entities in the graph.
     * @throws IOException if any file cannot be read or written.
     */
    public int exportToSvg(String entitiesCsvPath, String relationsCsvPath, String outputSvgPath)
            throws IOException {
        List<Entity> entities = EntityCsvParser.parse(entitiesCsvPath);
        List<Edge> edges = RelationCsvParser.parse(relationsCsvPath);

        String dotPath;
        if (outputSvgPath.endsWith(".svg")) {
            dotPath = outputSvgPath.substring(0, outputSvgPath.length() - 4) + ".dot";
        } else {
            dotPath = outputSvgPath + ".dot";
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
        try {
            writer.write(GraphDotExporter.export(entities, edges));
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
                        + ". Is Graphviz installed? On macOS: brew install graphviz");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Graph rendering was interrupted.", e);
        }

        return entities.size();
    }
}
