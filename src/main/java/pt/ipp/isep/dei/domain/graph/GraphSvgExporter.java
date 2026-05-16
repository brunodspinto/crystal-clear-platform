package pt.ipp.isep.dei.domain.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private static final int WIDTH = 1400;
    private static final int GRAPH_AREA_HEIGHT = 1180;
    private static final int CX = WIDTH / 2;
    private static final int CY = 600;
    private static final int[] RING_RADII = {140, 280, 400, 520};
    private static final int NODE_SIZE = 24;
    private static final int LABEL_RADIAL_OFFSET = 36;
    private static final int DETAIL_ROW_H = 90;
    private static final int DETAIL_START_Y = GRAPH_AREA_HEIGHT + 30;
    private static final int DETAIL_COLS = 4;
    private static final int DETAIL_COL_W = 290;
    private static final int EDGE_LABEL_STAGGER = 22;
    private static final int TITLE_BAND_HEIGHT = 60;

    private GraphSvgExporter() {}

    /**
     * Stores the position of a node on the SVG canvas.
     */
    private static class NodePos {
        private final String id;
        private final double x;
        private final double y;

        NodePos(String id, double x, double y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Stores how many edges already exist for the same ordered pair of endpoints.
     * Used to stagger labels when several edges share the same midpoint.
     */
    private static class PairCount {
        private final String pair;
        private int count;

        PairCount(String pair, int count) {
            this.pair = pair;
            this.count = count;
        }
    }

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
        w.println("  .node-label { font-size: 11px; fill: #222; pointer-events: none; }");
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
        ArrayList<NodePos> pos = positions(entities);
        ArrayList<PairCount> pairCounter = new ArrayList<>();
        for (Edge edge : edges) {
            NodePos from = findPos(pos, edge.getFromId());
            NodePos to = findPos(pos, edge.getToId());
            if (from == null || to == null) continue;

            String pairKey = pairKey(edge.getFromId(), edge.getToId());
            int idx = incrementPairCount(pairCounter, pairKey);

            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double len = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));
            double ux = dx / len;
            double uy = dy / len;

            double x1 = from.x + ux * NODE_SIZE;
            double y1 = from.y + uy * NODE_SIZE;
            double x2 = to.x - ux * NODE_SIZE;
            double y2 = to.y - uy * NODE_SIZE;

            double mx = (x1 + x2) / 2.0;
            double my = (y1 + y2) / 2.0;
            double nx = -uy;
            double ny = ux;
            int sign = (idx % 2 == 0) ? 1 : -1;
            double offset = sign * (10 + ((idx + 1) / 2) * EDGE_LABEL_STAGGER);
            double lx = mx + nx * offset;
            double ly = my + ny * offset - 3;

            double angleDeg = 0.0;

            String edgeId = "rel-" + sanitize(edge.getFromId()) + "-" + sanitize(edge.getToId())
                    + "-" + sanitize(edge.getLabel());

            w.printf(Locale.ROOT, "<line x1=\"%.1f\" y1=\"%.1f\" x2=\"%.1f\" y2=\"%.1f\" "
                    + "stroke=\"#999\" stroke-width=\"1.5\" marker-end=\"url(#arrow)\"/>%n",
                    x1, y1, x2, y2);

            w.printf("<a href=\"#detail-%s\" xlink:href=\"#detail-%s\">%n", edgeId, edgeId);
            w.printf(Locale.ROOT, "  <title>%s → %s | %s (weight: %.2f)</title>%n",
                    xmlEscape(edge.getFromId()), xmlEscape(edge.getToId()),
                    xmlEscape(edge.getLabel()), edge.getWeight());
            w.printf(Locale.ROOT, "  <text x=\"%.1f\" y=\"%.1f\" class=\"edge-label\" "
                            + "transform=\"rotate(%.1f %.1f %.1f)\">%s</text>%n",
                    lx, ly, angleDeg, lx, ly, xmlEscape(edge.getLabel()));
            w.println("</a>");
        }
    }

    private static String pairKey(String a, String b) {
        return (a.compareTo(b) < 0) ? a + "|" + b : b + "|" + a;
    }

    private static void writeNodes(PrintWriter w, List<Entity> entities) {
        ArrayList<NodePos> pos = positions(entities);
        for (Entity entity : entities) {
            NodePos p = findPos(pos, entity.getId());
            if (p == null) continue;
            double x = p.x;
            double y = p.y;
            String color = colorFor(entity);
            String detailId = "detail-" + sanitize(entity.getId());
            String tooltip = buildTooltip(entity);

            double dx = x - CX;
            double dy = y - CY;
            double dist = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));
            double offset = NODE_SIZE + LABEL_RADIAL_OFFSET;
            double labelX = x + (dx / dist) * offset;
            double labelY = y + (dy / dist) * offset + 4;
            if (labelY < TITLE_BAND_HEIGHT) {
                labelY = TITLE_BAND_HEIGHT;
            }
            if (labelY > GRAPH_AREA_HEIGHT - 30) {
                labelY = GRAPH_AREA_HEIGHT - 30;
            }
            String anchor = "middle";
            if (dx / dist > 0.4) anchor = "start";
            else if (dx / dist < -0.4) anchor = "end";

            w.printf("<a href=\"#%s\" xlink:href=\"#%s\">%n", detailId, detailId);
            w.printf("  <title>%s</title>%n", xmlEscape(tooltip));
            drawShape(w, entity, x, y, color);
            w.printf(Locale.ROOT, "  <text x=\"%.1f\" y=\"%.1f\" class=\"node-label\" "
                            + "text-anchor=\"%s\">%s</text>%n",
                    labelX, labelY, anchor, xmlEscape(shortLabel(entity)));
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

    /**
     * Places entities on concentric rings, one ring per category.
     * Inner ring holds persons, then organizations, positions and assets outward.
     * This avoids overlap between categories and reduces edge crossings.
     */
    private static ArrayList<NodePos> positions(List<Entity> entities) {
        ArrayList<NodePos> list = new ArrayList<>();
        if (entities.isEmpty()) return list;

        List<Entity> persons = new ArrayList<>();
        List<Entity> orgs = new ArrayList<>();
        List<Entity> positionList = new ArrayList<>();
        List<Entity> assets = new ArrayList<>();
        List<Entity> other = new ArrayList<>();
        for (Entity e : entities) {
            String cat = entityCategory(e);
            if (cat.equals("person")) persons.add(e);
            else if (cat.equals("organization")) orgs.add(e);
            else if (cat.equals("position")) positionList.add(e);
            else if (cat.equals("asset")) assets.add(e);
            else other.add(e);
        }
        sortById(persons);
        sortById(orgs);
        sortById(positionList);
        sortById(assets);
        sortById(other);

        placeRing(list, persons, RING_RADII[0]);
        placeRing(list, orgs, RING_RADII[1]);
        placeRing(list, positionList, RING_RADII[2]);

        List<Entity> outer = new ArrayList<>();
        outer.addAll(assets);
        outer.addAll(other);
        placeRing(list, outer, RING_RADII[3]);

        return list;
    }

    /**
     * Sorts a list of entities by their identifier using insertion sort.
     */
    private static void sortById(List<Entity> list) {
        for (int i = 1; i < list.size(); i++) {
            Entity current = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).getId().compareTo(current.getId()) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, current);
        }
    }

    /**
     * Distributes the given entities evenly around a circle of the given radius
     * centred at (CX, CY) and appends each resulting NodePos to the list.
     */
    private static void placeRing(ArrayList<NodePos> list, List<Entity> ring, int radius) {
        int n = ring.size();
        if (n == 0) return;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = CX + radius * Math.cos(angle);
            double y = CY + radius * Math.sin(angle);
            list.add(new NodePos(ring.get(i).getId(), x, y));
        }
    }

    /**
     * Returns the NodePos with the given id, or null if not present.
     */
    private static NodePos findPos(List<NodePos> list, String id) {
        for (NodePos np : list) {
            if (np.id.equals(id)) return np;
        }
        return null;
    }

    /**
     * Increments the count for the given pair key and returns the previous count.
     * If the key is not present, adds a new entry with count 1 and returns 0.
     */
    private static int incrementPairCount(List<PairCount> counts, String pair) {
        for (PairCount pc : counts) {
            if (pc.pair.equals(pair)) {
                int previous = pc.count;
                pc.count = previous + 1;
                return previous;
            }
        }
        counts.add(new PairCount(pair, 1));
        return 0;
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
