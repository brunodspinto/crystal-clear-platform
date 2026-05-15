package pt.ipp.isep.dei.domain.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generates an SVG file visualising a heterogeneous multi-relational graph.
 * <p>
 * Entities are placed in a circular layout; each entity type uses a distinct
 * shape and colour. Every entity node and every edge label is wrapped in an
 * SVG hyperlink (<a href="#detail-ID">) so that clicking navigates to a
 * detail card at the bottom of the file (AC1 of US26).
 * Hovering also shows a <title> tooltip with full details.
 */
public class GraphSvgExporter {

    private static final int WIDTH = 1100;
    private static final int GRAPH_AREA_HEIGHT = 820;
    private static final int CX = WIDTH / 2;
    private static final int CY = 430;
    private static final int RADIUS = 340;
    private static final int NODE_SIZE = 28;
    private static final int DETAIL_ROW_H = 90;
    private static final int DETAIL_START_Y = GRAPH_AREA_HEIGHT + 30;
    private static final int DETAIL_COLS = 4;
    private static final int DETAIL_COL_W = 270;
    private static final int EDGE_LABEL_STAGGER = 14;

    private GraphSvgExporter() {}

    /**
     * Export.
     *
     * @param entities the entities
     * @param edges    the edges
     * @param filePath the file path
     * @throws IOException the io exception
     */
    public static void export(List<Entity> entities, List<Edge> edges, String filePath) throws IOException {
        int detailRows = (int) Math.ceil((double) entities.size() / DETAIL_COLS);
        int totalHeight = DETAIL_START_Y + detailRows * DETAIL_ROW_H + 40;

        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            writeHeader(w, totalHeight);
            writeStyles(w);
            writeTitle(w);
            writeEdges(w, entities, edges);
            writeNodes(w, entities);
            writeDetailSection(w, entities, edges);
            writeFooter(w);
        }
    }

    // -------------------------------------------------------------------------

    private static void writeHeader(PrintWriter w, int totalHeight) {
        w.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        w.printf("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                + "width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">%n", WIDTH, totalHeight, WIDTH, totalHeight);
    }

    private static void writeStyles(PrintWriter w) {
        w.println("<defs>");
        w.println("  <marker id=\"arrow\" markerWidth=\"8\" markerHeight=\"8\" refX=\"6\" refY=\"3\" orient=\"auto\">");
        w.println("    <path d=\"M0,0 L0,6 L8,3 z\" fill=\"#666\"/>");
        w.println("  </marker>");
        w.println("</defs>");
        w.println("<style>");
        w.println("  text { font-family: Arial, sans-serif; }");
        w.println("  .node-label { font-size: 11px; fill: #222; text-anchor: middle; pointer-events: none; }");
        w.println("  .edge-label { font-size: 10px; fill: #555; text-anchor: middle; }");
        w.println("  .detail-title { font-size: 13px; font-weight: bold; fill: #333; }");
        w.println("  .detail-body { font-size: 11px; fill: #555; }");
        w.println("  .section-title { font-size: 15px; font-weight: bold; fill: #222; }");
        w.println("  a:hover > circle, a:hover > rect, a:hover > polygon { opacity: 0.75; }");
        w.println("</style>");
    }

    private static void writeTitle(PrintWriter w) {
        w.printf("<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" fill=\"#f8f9fa\"/>%n",
                WIDTH, GRAPH_AREA_HEIGHT);
        w.printf("<text x=\"%d\" y=\"30\" class=\"section-title\" text-anchor=\"middle\">"
                + "Heterogeneous Multi-Relational Graph</text>%n", WIDTH / 2);
    }

    private static void writeEdges(PrintWriter w, List<Entity> entities, List<Edge> edges) {
        Map<String, double[]> pos = positions(entities);
        Map<String, Integer> pairCounter = new HashMap<>();
        for (Edge edge : edges) {
            double[] from = pos.get(edge.getFromId());
            double[] to = pos.get(edge.getToId());
            if (from == null || to == null) continue;

            String pairKey = pairKey(edge.getFromId(), edge.getToId());
            int idx = pairCounter.getOrDefault(pairKey, 0);
            pairCounter.put(pairKey, idx + 1);

            double mx = (from[0] + to[0]) / 2.0;
            double my = (from[1] + to[1]) / 2.0;
            double dx = to[0] - from[0];
            double dy = to[1] - from[1];
            double len = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));
            double nx = -dy / len;
            double ny = dx / len;
            int sign = (idx % 2 == 0) ? 1 : -1;
            double offset = sign * ((idx + 1) / 2) * EDGE_LABEL_STAGGER;
            double lx = mx + nx * offset;
            double ly = my + ny * offset;

            String edgeId = "rel-" + sanitize(edge.getFromId()) + "-" + sanitize(edge.getToId())
                    + "-" + sanitize(edge.getLabel());

            w.printf(Locale.ROOT, "<line x1=\"%.1f\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%.1f\" "
                    + "stroke=\"#999\" stroke-width=\"1.5\" marker-end=\"url(#arrow)\"/>%n",
                    from[0], from[1], to[0], to[1]);

            w.printf("<a href=\"#detail-%s\" xlink:href=\"#detail-%s\">%n", edgeId, edgeId);
            w.printf(Locale.ROOT, "  <title>%s → %s | %s (weight: %.2f)</title>%n",
                    xmlEscape(edge.getFromId()), xmlEscape(edge.getToId()),
                    xmlEscape(edge.getLabel()), edge.getWeight());
            w.printf(Locale.ROOT, "  <text x=\"%.1f\" y=\"%.1f\" class=\"edge-label\">%s</text>%n",
                    lx, ly, xmlEscape(edge.getLabel()));
            w.println("</a>");
        }
    }

    private static String pairKey(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "|" + b : b + "|" + a;
    }

    private static void writeNodes(PrintWriter w, List<Entity> entities) {
        Map<String, double[]> pos = positions(entities);
        for (Entity entity : entities) {
            double[] p = pos.get(entity.getId());
            if (p == null) continue;
            double x = p[0];
            double y = p[1];
            String color = colorFor(entity);
            String detailId = "detail-" + sanitize(entity.getId());
            String tooltip = buildTooltip(entity);

            w.printf("<a href=\"#%s\" xlink:href=\"#%s\">%n", detailId, detailId);
            w.printf("  <title>%s</title>%n", xmlEscape(tooltip));
            drawShape(w, entity, x, y, color);
            w.printf(Locale.ROOT, "  <text x=\"%.1f\" y=\"%.1f\" class=\"node-label\">%s</text>%n",
                    x, y + NODE_SIZE + 13, xmlEscape(shortLabel(entity)));
            w.println("</a>");
        }
    }

    private static void drawShape(PrintWriter w, Entity entity, double x, double y, String color) {
        String cat = entityCategory(entity);
        int s = NODE_SIZE;
        switch (cat) {
            case "person":
                w.printf(Locale.ROOT, "  <circle cx=\"%.1f\" cy=\"%.1f\" r=\"%d\" fill=\"%s\" stroke=\"#555\" stroke-width=\"1.5\"/>%n",
                        x, y, s, color);
                break;
            case "organization":
                w.printf(Locale.ROOT, "  <rect x=\"%.1f\" y=\"%.1f\" width=\"%d\" height=\"%d\" rx=\"4\" fill=\"%s\" stroke=\"#555\" stroke-width=\"1.5\"/>%n",
                        x - s, y - s, s * 2, s * 2, color);
                break;
            case "position":
                double px = x, py = y;
                String pts = String.format(Locale.ROOT, "%.1f,%.1f %.1f,%.1f %.1f,%.1f %.1f,%.1f",
                        px, py - s, px + s, py, px, py + s, px - s, py);
                w.printf("  <polygon points=\"%s\" fill=\"%s\" stroke=\"#555\" stroke-width=\"1.5\"/>%n", pts, color);
                break;
            case "asset":
            default:
                String tpts = String.format(Locale.ROOT, "%.1f,%.1f %.1f,%.1f %.1f,%.1f",
                        x, y - s, x + s, y + s, x - s, y + s);
                w.printf("  <polygon points=\"%s\" fill=\"%s\" stroke=\"#555\" stroke-width=\"1.5\"/>%n", tpts, color);
                break;
        }
    }

    private static void writeDetailSection(PrintWriter w, List<Entity> entities, List<Edge> edges) {
        w.printf("<line x1=\"20\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"#ccc\" stroke-width=\"1\"/>%n",
                DETAIL_START_Y - 20, WIDTH - 20, DETAIL_START_Y - 20);
        w.printf("<text x=\"20\" y=\"%d\" class=\"section-title\">Entity &amp; Relation Details</text>%n",
                DETAIL_START_Y + 10);

        int col = 0;
        int row = 0;
        for (Entity entity : entities) {
            String detailId = "detail-" + sanitize(entity.getId());
            int bx = 20 + col * DETAIL_COL_W;
            int by = DETAIL_START_Y + 30 + row * DETAIL_ROW_H;
            w.printf("<g id=\"%s\">%n", detailId);
            w.printf("  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" rx=\"5\" "
                    + "fill=\"white\" stroke=\"%s\" stroke-width=\"1.5\"/>%n",
                    bx, by, DETAIL_COL_W - 10, DETAIL_ROW_H - 8, colorFor(entity));
            w.printf("  <text x=\"%d\" y=\"%d\" class=\"detail-title\">%s</text>%n",
                    bx + 8, by + 18, xmlEscape("[" + entityCategory(entity).toUpperCase() + "] " + entity.getId()));
            int lineY = by + 34;
            for (String line : detailLines(entity)) {
                w.printf("  <text x=\"%d\" y=\"%d\" class=\"detail-body\">%s</text>%n",
                        bx + 8, lineY, xmlEscape(line));
                lineY += 14;
            }
            w.println("</g>");
            col++;
            if (col >= DETAIL_COLS) { col = 0; row++; }
        }

        int edgeDetailY = DETAIL_START_Y + 30 + ((int) Math.ceil((double) entities.size() / DETAIL_COLS)) * DETAIL_ROW_H + 20;
        w.printf("<text x=\"20\" y=\"%d\" class=\"section-title\">Relations</text>%n", edgeDetailY);
        edgeDetailY += 20;
        for (Edge edge : edges) {
            String edgeId = "rel-" + sanitize(edge.getFromId()) + "-" + sanitize(edge.getToId())
                    + "-" + sanitize(edge.getLabel());
            w.printf("<g id=\"detail-%s\">%n", edgeId);
            w.printf(Locale.ROOT, "  <text x=\"20\" y=\"%d\" class=\"detail-body\">%s → %s | label: %s | weight: %.2f</text>%n",
                    edgeDetailY,
                    xmlEscape(edge.getFromId()), xmlEscape(edge.getToId()),
                    xmlEscape(edge.getLabel()), edge.getWeight());
            w.println("</g>");
            edgeDetailY += 18;
        }
    }

    private static void writeFooter(PrintWriter w) {
        w.println("</svg>");
    }

    // -------------------------------------------------------------------------

    private static Map<String, double[]> positions(List<Entity> entities) {
        Map<String, double[]> map = new HashMap<>();
        int n = entities.size();
        if (n == 0) return map;
        List<Entity> sorted = new ArrayList<>(entities);
        sorted.sort(Comparator
                .comparingInt((Entity e) -> categoryOrder(e))
                .thenComparing(Entity::getId));
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = CX + RADIUS * Math.cos(angle);
            double y = CY + RADIUS * Math.sin(angle);
            map.put(sorted.get(i).getId(), new double[]{x, y});
        }
        return map;
    }

    private static int categoryOrder(Entity entity) {
        switch (entityCategory(entity)) {
            case "person":       return 0;
            case "organization": return 1;
            case "position":     return 2;
            case "asset":        return 3;
            default:             return 4;
        }
    }

    private static String colorFor(Entity entity) {
        switch (entityCategory(entity)) {
            case "person":       return "#7ec8e3";
            case "organization": return "#f9c74f";
            case "position":     return "#90be6d";
            case "asset":        return "#f8961e";
            default:             return "#aaa";
        }
    }

    private static String entityCategory(Entity entity) {
        if (entity instanceof Person)       return "person";
        if (entity instanceof Organization) return "organization";
        if (entity instanceof Position)     return "position";
        if (entity instanceof Asset)        return "asset";
        return "unknown";
    }

    private static String shortLabel(Entity entity) {
        if (entity instanceof Person)       return ((Person) entity).getName();
        if (entity instanceof Organization) return ((Organization) entity).getName();
        if (entity instanceof Position)     return ((Position) entity).getPositionTitle();
        if (entity instanceof Asset)        return ((Asset) entity).getAssetType();
        return entity.getId();
    }

    private static String buildTooltip(Entity entity) {
        return String.join(" | ", detailLines(entity));
    }

    private static String[] detailLines(Entity entity) {
        String base = "type: " + entity.getType()
                + " | start: " + entity.getStartDate()
                + " | end: " + entity.getEndDate();
        if (entity instanceof Person) {
            Person p = (Person) entity;
            return new String[]{
                    "Name: " + p.getName(),
                    "Born: " + p.getBirthDate() + " | " + p.getNationality(),
                    base
            };
        }
        if (entity instanceof Organization) {
            Organization o = (Organization) entity;
            return new String[]{
                    "Name: " + o.getName(),
                    "OrgType: " + o.getOrganizationType() + " | " + o.getCountry(),
                    base
            };
        }
        if (entity instanceof Position) {
            Position pos = (Position) entity;
            return new String[]{
                    "Title: " + pos.getPositionTitle(),
                    "PosType: " + pos.getPositionType() + " | Org: " + pos.getOrganizationId(),
                    base
            };
        }
        if (entity instanceof Asset) {
            Asset a = (Asset) entity;
            return new String[]{
                    "AssetType: " + a.getAssetType(),
                    "Country: " + a.getCountry() + " | Value: " + a.getEstimatedValue(),
                    base
            };
        }
        return new String[]{base};
    }

    private static String sanitize(String id) {
        return id.replaceAll("[^A-Za-z0-9_-]", "_");
    }

    private static String xmlEscape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
