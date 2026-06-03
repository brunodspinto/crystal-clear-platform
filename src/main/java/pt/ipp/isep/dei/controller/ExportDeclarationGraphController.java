package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.graph.DeclarationGraphCsvExporter;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.FamilyRelationshipInferrer;
import pt.ipp.isep.dei.domain.graph.FamilyRelationshipType;
import pt.ipp.isep.dei.domain.graph.RelationCsvParser;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US37 - Build the declaration interest graph and export its
 * entities and relations to CSV, inferring the family ties (symmetric,
 * inverse and transitive closures) from a seed list of declared kinships.
 *
 * <p>The heavy lifting is delegated to {@link DeclarationGraphCsvExporter},
 * which extracts the entities and relations from the validated declarations
 * and appends the family relationships produced by
 * {@link FamilyRelationshipInferrer}.</p>
 */
public class ExportDeclarationGraphController {

    private final DeclarationRepository declarationRepository;

    /**
     * Creates the controller using the default {@link DeclarationRepository}
     * from {@link Repositories}.
     */
    public ExportDeclarationGraphController() {
        this.declarationRepository = Repositories.getInstance().getDeclarationRepository();
    }

    /**
     * Creates the controller with an explicit repository, intended for testing.
     *
     * @param declarationRepository the declaration repository to use
     */
    public ExportDeclarationGraphController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    /**
     * Returns true if there is at least one validated declaration to export.
     *
     * @return true when validated declarations are available
     */
    public boolean hasData() {
        return !declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED).isEmpty();
    }

    /**
     * Reads a seed list of family relationships from a CSV file, keeping only
     * the edges whose label matches a {@link FamilyRelationshipType}. Any other
     * relation found in the file is ignored.
     *
     * @param filePath path of the relations CSV file with the declared kinships
     * @return the family seed edges found in the file
     * @throws IOException if the file cannot be read
     */
    public List<Edge> loadFamilySeed(String filePath) throws IOException {
        List<Edge> seed = new ArrayList<>();
        for (Edge e : RelationCsvParser.parse(filePath)) {
            if (FamilyRelationshipType.fromLabel(e.getLabel()) != null) {
                seed.add(e);
            }
        }
        return seed;
    }

    /**
     * Applies the symmetric, inverse and transitive closures to a seed list of
     * family relationships, returning the seed plus every inferred tie.
     *
     * @param familySeed the declared family relationships
     * @return the seed together with the inferred relationships
     */
    public List<Edge> inferFamilyRelationships(List<Edge> familySeed) {
        return FamilyRelationshipInferrer.infer(familySeed);
    }

    /**
     * Exports the entities and relations of every validated declaration to the
     * given CSV files. The provided family seed is expanded with the inferred
     * relationships before the relations CSV is written.
     *
     * @param familySeed    the declared family relationships (may be empty)
     * @param entitiesPath  output path for the entities CSV
     * @param relationsPath output path for the relations CSV
     * @return {@code true} if the export succeeded; {@code false} on I/O error
     */
    public boolean export(List<Edge> familySeed, String entitiesPath, String relationsPath) {
        List<Declaration> validated =
                declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        try {
            return DeclarationGraphCsvExporter.export(validated, familySeed, entitiesPath, relationsPath);
        } catch (IOException e) {
            return false;
        }
    }
}
