package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.domain.graph.GraphSvgExporter;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US26 - Export the graph as an SVG file with clickable hyperlinks.
 */
public class ExportGraphSvgController {

    /**
     * Creates a controller.
     */
    public ExportGraphSvgController() {}

    /**
     * Builds the graph from two CSV files and exports it as an SVG file.
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
        GraphSvgExporter.export(entities, edges, outputSvgPath);
        return entities.size();
    }
}
