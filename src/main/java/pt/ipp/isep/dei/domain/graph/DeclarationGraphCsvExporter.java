package pt.ipp.isep.dei.domain.graph;

import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.BusinessParticipation;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PositionEntry;
import pt.ipp.isep.dei.domain.RealEstate;
import pt.ipp.isep.dei.domain.StockAsset;
import pt.ipp.isep.dei.domain.VehicleAsset;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Fabrication responsible for extracting graph entities and relations
 * from a list of {@link Declaration} objects and writing them to CSV files
 * following the formats of US19 (entities) and US20 (relations).
 * <p>
 * Family relationships (US37 AC1) are not stored in the current Declaration
 * domain. A seed list of family ties can be passed in by the caller; this
 * exporter then applies {@link FamilyRelationshipInferrer} to compute the
 * symmetric, inverse and transitive closures before writing the relations
 * CSV.
 */
public class DeclarationGraphCsvExporter {

    private static final String SEP = ";";
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private DeclarationGraphCsvExporter() {}

    /**
     * Exports the entities and relations of the given declarations to the
     * provided file paths.
     *
     * @param declarations    list of declarations to export
     * @param familySeed      seed family relationships (may be empty); the
     *                        inferrer is applied to expand the closures
     * @param entitiesPath    output path for the entities CSV
     * @param relationsPath   output path for the relations CSV
     * @return {@code true} when both files were written successfully
     * @throws IOException if an I/O error occurs while writing either file
     */
    public static boolean export(List<Declaration> declarations,
                                 List<Edge> familySeed,
                                 String entitiesPath,
                                 String relationsPath) throws IOException {
        if (declarations == null) {
            throw new IllegalArgumentException("declarations must not be null");
        }
        Registry registry = buildRegistry(declarations);
        writeEntities(registry, entitiesPath);
        List<Edge> relations = collectRelations(declarations, registry);
        if (familySeed != null && !familySeed.isEmpty()) {
            relations.addAll(FamilyRelationshipInferrer.infer(familySeed));
        }
        writeRelations(relations, relationsPath);
        return true;
    }

    private static Registry buildRegistry(List<Declaration> declarations) {
        Registry r = new Registry();
        for (Declaration d : declarations) {
            r.registerPerson(d.getAgent());
            for (PositionEntry p : d.getPositionEntries()) {
                r.registerOrganization(p.getOrganization());
                r.registerPosition(d.getAgent(), p);
            }
            for (BusinessParticipation b : d.getBusinessParticipations()) {
                r.registerOrganization(b.getOrganization());
            }
            for (AssetEntry a : d.getAssetEntries()) {
                r.registerAsset(d.getAgent(), a);
            }
        }
        return r;
    }

    private static void writeEntities(Registry registry, String path) throws IOException {
        PrintWriter w = new PrintWriter(new FileWriter(path));
        try {
            w.println("# entities — format: category;id;type;startDate;endDate;field1;field2;field3");
            for (PoliticalAgent agent : registry.persons.keySet()) {
                String id = registry.persons.get(agent);
                w.println(joinPerson(id, agent));
            }
            for (String key : registry.organizations.keySet()) {
                OrgInfo o = registry.organizations.get(key);
                w.println(joinOrganization(o));
            }
            for (String key : registry.positions.keySet()) {
                PosInfo p = registry.positions.get(key);
                w.println(joinPosition(p));
            }
            for (String key : registry.assets.keySet()) {
                AssetInfo a = registry.assets.get(key);
                w.println(joinAsset(a));
            }
        } finally {
            w.close();
        }
    }

    private static List<Edge> collectRelations(List<Declaration> declarations, Registry registry) {
        List<Edge> rels = new ArrayList<>();
        for (Declaration d : declarations) {
            String personId = registry.persons.get(d.getAgent());
            for (PositionEntry p : d.getPositionEntries()) {
                String orgId = registry.organizations.get(orgKey(p.getOrganization())).id;
                String posId = registry.positions.get(positionKey(d.getAgent(), p)).id;
                rels.add(new Edge(personId, posId, "holdsPosition", 1.0));
                rels.add(new Edge(posId, orgId, "inOrganization", 1.0));
            }
            for (BusinessParticipation b : d.getBusinessParticipations()) {
                String orgId = registry.organizations.get(orgKey(b.getOrganization())).id;
                rels.add(new Edge(personId, orgId, "partnerOf", b.getCompanyPercentage()));
            }
            for (AssetEntry a : d.getAssetEntries()) {
                String assetId = registry.assets.get(assetKey(d.getAgent(), a)).id;
                rels.add(new Edge(personId, assetId, "ownerOf", a.getAssetValue()));
            }
        }
        return rels;
    }

    private static void writeRelations(List<Edge> relations, String path) throws IOException {
        PrintWriter w = new PrintWriter(new FileWriter(path));
        try {
            w.println("# relations — format: id;type;startDate;endDate;entity1;entity2;weight");
            int i = 1;
            for (Edge e : relations) {
                String id = String.format("R-%03d", i);
                i++;
                w.println(id + SEP + e.getLabel() + SEP + SEP + SEP
                        + e.getFromId() + SEP + e.getToId() + SEP + e.getWeight());
            }
        } finally {
            w.close();
        }
    }

    private static String joinPerson(String id, PoliticalAgent a) {
        return "person" + SEP + id + SEP + "politician" + SEP
                + safeDate(a.getMandateStart()) + SEP + safeDate(a.getMandateEnd()) + SEP
                + safe(a.getName()) + SEP + SEP;
    }

    private static String joinOrganization(OrgInfo o) {
        return "organization" + SEP + o.id + SEP + safe(o.type) + SEP + SEP + SEP
                + safe(o.name) + SEP + safe(o.nature) + SEP + "Portugal";
    }

    private static String joinPosition(PosInfo p) {
        return "position" + SEP + p.id + SEP + safe(p.nature) + SEP
                + safeDate(p.startDate) + SEP + safeDate(p.endDate) + SEP
                + safe(p.functionDesignation) + SEP + safe(p.nature) + SEP + p.organizationId;
    }

    private static String joinAsset(AssetInfo a) {
        return "asset" + SEP + a.id + SEP + safe(a.type) + SEP + SEP + SEP
                + safe(a.type) + SEP + safe(a.detail) + SEP + a.value;
    }

    private static String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace(SEP, " ").replace("\n", " ").trim();
    }

    private static String safeDate(Date d) {
        if (d == null) {
            return "";
        }
        return DATE_FMT.format(d);
    }

    private static String orgKey(pt.ipp.isep.dei.domain.Organization o) {
        return o.getVatNumber();
    }

    private static String positionKey(PoliticalAgent agent, PositionEntry p) {
        return agent.getTaxIdentificationNumber() + "|"
                + p.getOrganization().getVatNumber() + "|"
                + (p.getFunctionDesignation() == null ? "" : p.getFunctionDesignation());
    }

    private static String assetKey(PoliticalAgent agent, AssetEntry a) {
        String detail = "";
        if (a.getRealEstate() != null) {
            detail = a.getRealEstate().getDescription();
        } else if (a.getVehicleAsset() != null) {
            detail = a.getVehicleAsset().getDescription();
        } else if (a.getStockAsset() != null) {
            detail = a.getStockAsset().getDescription();
        }
        return agent.getTaxIdentificationNumber() + "|"
                + (a.getAssetType() == null ? "" : a.getAssetType().name()) + "|"
                + detail;
    }

    private static class Registry {
        final Map<PoliticalAgent, String> persons = new LinkedHashMap<>();
        final Map<String, OrgInfo> organizations = new LinkedHashMap<>();
        final Map<String, PosInfo> positions = new LinkedHashMap<>();
        final Map<String, AssetInfo> assets = new LinkedHashMap<>();
        private int nextPerson = 1;
        private int nextOrg = 1;
        private int nextPos = 1;
        private int nextAsset = 1;

        void registerPerson(PoliticalAgent a) {
            if (a == null || persons.containsKey(a)) {
                return;
            }
            persons.put(a, String.format("P-%03d", nextPerson));
            nextPerson++;
        }

        void registerOrganization(pt.ipp.isep.dei.domain.Organization o) {
            if (o == null) {
                return;
            }
            String key = o.getVatNumber();
            if (organizations.containsKey(key)) {
                return;
            }
            OrgInfo info = new OrgInfo();
            info.id = String.format("O-%03d", nextOrg);
            info.name = o.getName();
            info.nature = o.getNature();
            info.type = o.getType() == null ? "" : o.getType().name().toLowerCase();
            organizations.put(key, info);
            nextOrg++;
        }

        void registerPosition(PoliticalAgent a, PositionEntry p) {
            if (p == null) {
                return;
            }
            String key = positionKey(a, p);
            if (positions.containsKey(key)) {
                return;
            }
            PosInfo info = new PosInfo();
            info.id = String.format("J-%03d", nextPos);
            info.functionDesignation = p.getFunctionDesignation();
            info.nature = p.getNature() == null ? "" : p.getNature().name().toLowerCase();
            info.startDate = p.getStartDate();
            info.endDate = p.getEndDate();
            info.organizationId = organizations.get(p.getOrganization().getVatNumber()).id;
            positions.put(key, info);
            nextPos++;
        }

        void registerAsset(PoliticalAgent a, AssetEntry e) {
            if (e == null) {
                return;
            }
            String key = assetKey(a, e);
            if (assets.containsKey(key)) {
                return;
            }
            AssetInfo info = new AssetInfo();
            info.id = String.format("A-%03d", nextAsset);
            info.type = e.getAssetType() == null ? "" : e.getAssetType().name().toLowerCase();
            info.value = e.getAssetValue();
            if (e.getRealEstate() != null) {
                RealEstate r = e.getRealEstate();
                info.detail = (r.getDescription() == null ? "" : r.getDescription())
                        + (r.getMunicipality() == null ? "" : " (" + r.getMunicipality() + ")");
            } else if (e.getVehicleAsset() != null) {
                VehicleAsset v = e.getVehicleAsset();
                info.detail = v.getDescription() == null ? "" : v.getDescription();
            } else if (e.getStockAsset() != null) {
                StockAsset s = e.getStockAsset();
                info.detail = s.getDescription() == null ? "" : s.getDescription();
            } else {
                info.detail = "";
            }
            assets.put(key, info);
            nextAsset++;
        }
    }

    private static class OrgInfo {
        String id;
        String name;
        String type;
        String nature;
    }

    private static class PosInfo {
        String id;
        String functionDesignation;
        String nature;
        Date startDate;
        Date endDate;
        String organizationId;
    }

    private static class AssetInfo {
        String id;
        String type;
        double value;
        String detail;
    }
}
