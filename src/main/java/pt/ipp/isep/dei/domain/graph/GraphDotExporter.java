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
            dot.append(", style=filled");
            dot.append(", tooltip=\"").append(escape(tooltipFor(entity))).append("\"");
            if (!entity.getUrl().isEmpty()) {
                dot.append(", URL=\"").append(escape(entity.getUrl())).append("\"");
            }
            dot.append("];\n");
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            boolean symmetric = GraphBuilder.isSymmetricLabel(edge.getLabel());
            // Symmetric ties (friendOf, relativeOf, associatedWith) hold in both
            // directions, so they are drawn as a single arc with an arrowhead on
            // each end. When the data already lists the reverse edge, only the
            // first occurrence is drawn so the pair is not rendered twice.
            boolean alreadyDrawnAsBidirectional = symmetric && reverseAppearsBefore(edges, i);
            if (!alreadyDrawnAsBidirectional) {
                dot.append("  \"").append(escape(edge.getFromId())).append("\" -> \"");
                dot.append(escape(edge.getToId())).append("\"");
                dot.append(" [label=\"").append(escape(edge.getLabel())).append("\"");
                if (symmetric) {
                    dot.append(", dir=both");
                }
                dot.append(", tooltip=\"").append(escape(tooltipFor(edge))).append("\"");
                dot.append("];\n");
            }
        }

        dot.append("}\n");
        return dot.toString();
    }

    /**
     * Tells whether the reverse of the edge at {@code index} (same label, with
     * the endpoints swapped) appears earlier in the list. Used to avoid drawing
     * a symmetric relation twice.
     *
     * @param edges the edges being exported.
     * @param index the position of the edge to check.
     * @return {@code true} if the reverse edge appears before {@code index}.
     */
    private static boolean reverseAppearsBefore(List<Edge> edges, int index) {
        Edge e = edges.get(index);
        for (int j = 0; j < index; j++) {
            Edge other = edges.get(j);
            if (other.getFromId().equals(e.getToId())
                    && other.getToId().equals(e.getFromId())
                    && other.getLabel().equals(e.getLabel())) {
                return true;
            }
        }
        return false;
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

    private static String tooltipFor(Entity entity) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(entity.getId());
        sb.append(" | Type: ").append(entity.getType());
        if (!entity.getStartDate().isEmpty()) sb.append(" | Start: ").append(entity.getStartDate());
        if (!entity.getEndDate().isEmpty()) sb.append(" | End: ").append(entity.getEndDate());
        sb.append(" | ").append(entity.getDetails());
        return sb.toString();
    }

    private static String tooltipFor(Edge edge) {
        return "Relation: " + edge.getLabel()
                + " | From: " + edge.getFromId()
                + " | To: " + edge.getToId()
                + " | Weight: " + edge.getWeight();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
