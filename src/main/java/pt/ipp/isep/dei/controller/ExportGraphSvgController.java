package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.EntityCsvParser;
import pt.ipp.isep.dei.domain.graph.GraphSvgExporter;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US26 - Export heterogeneous graph as interactive SVG with hyperlinks.
 */
public class ExportGraphSvgController {

    public ExportGraphSvgController() {}

    public int exportToSvg(String entitiesCsvPath, String relationsCsvPath, String outputSvgPath)
            throws IOException {
        List<Entity> entities = EntityCsvParser.parse(entitiesCsvPath);
        List<Edge> edges = RelationCsvParser.parse(relationsCsvPath);
        GraphSvgExporter.export(entities, edges, outputSvgPath);
        return entities.size();
    }
}
