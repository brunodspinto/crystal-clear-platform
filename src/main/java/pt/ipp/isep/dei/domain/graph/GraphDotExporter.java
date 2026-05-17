package pt.ipp.isep.dei.domain.graph;

import java.util.List;

/**
 * Builds a Graphviz DOT string from a list of entities and edges.
 * Node shapes and fill colours reflect entity type.
 * The output is intended to be piped into {@code dot -Tsvg}.
 */
public class GraphDotExporter {

    private GraphDotExporter() {}

    /**
     * Produces a DOT-format string for the given entities and edges.
     *
     * @param entities the entities to include as nodes.
     * @param edges    the edges to include as directed arcs.
     * @return a valid Graphviz DOT string.
     */
    public static String export(List<Entity> entities, List<Edge> edges) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [fontname=\"Helvetica\", fontsize=10];\n");
        dot.append("  edge [fontname=\"Helvetica\", fontsize=9];\n");

        for (Entity entity : entities) {
            dot.append("  \"").append(escape(entity.getId())).append("\"");
            dot.append(" [label=\"").append(escape(shortLabel(entity))).append("\"");
            dot.append(", shape=").append(shapeFor(entity));
            dot.append(", fillcolor=\"").append(colorFor(entity)).append("\"");
            dot.append(", style=filled];\n");
        }

        for (Edge edge : edges) {
            dot.append("  \"").append(escape(edge.getFromId())).append("\" -> \"");
            dot.append(escape(edge.getToId())).append("\"");
            dot.append(" [label=\"").append(escape(edge.getLabel())).append("\"];\n");
        }

        dot.append("}\n");
        return dot.toString();
    }

    private static String shapeFor(Entity entity) {
        if (entity instanceof Person)       return "ellipse";
        if (entity instanceof Organization) return "box";
        if (entity instanceof Position)     return "diamond";
        if (entity instanceof Asset)        return "triangle";
        return "box";
    }

    private static String colorFor(Entity entity) {
        if (entity instanceof Person)       return "#7ec8e3";
        if (entity instanceof Organization) return "#f9c74f";
        if (entity instanceof Position)     return "#90be6d";
        if (entity instanceof Asset)        return "#f8961e";
        return "#aaaaaa";
    }

    private static String shortLabel(Entity entity) {
        if (entity instanceof Person)       return ((Person) entity).getName();
        if (entity instanceof Organization) return ((Organization) entity).getName();
        if (entity instanceof Position)     return ((Position) entity).getPositionTitle();
        if (entity instanceof Asset)        return ((Asset) entity).getAssetType();
        return entity.getId();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
