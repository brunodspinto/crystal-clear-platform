package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationCsvExporter;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US24 - Export Declaration Dataset to CSV.
 */
public class ExportDeclarationCsvController {

    private final DeclarationRepository declarationRepository;

    /**
     * Creates the controller using the default {@link DeclarationRepository} from {@link Repositories}.
     */
    public ExportDeclarationCsvController() {
        this.declarationRepository = Repositories.getInstance().getDeclarationRepository();
    }

    /**
     * Creates the controller with an explicit repository, intended for testing.
     *
     * @param declarationRepository the declaration repository to use
     */
    public ExportDeclarationCsvController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    /**
     * Exports all validated declarations to a CSV file at the given path.
     *
     * @param filePath path of the output CSV file
     * @return {@code true} if the export succeeded; {@code false} if an I/O error occurred
     */
    public boolean exportToCsv(String filePath) {
        List<Declaration> validated = declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        try {
            return DeclarationCsvExporter.export(validated, filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
