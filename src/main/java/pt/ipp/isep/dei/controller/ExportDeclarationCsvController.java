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

    public ExportDeclarationCsvController() {
        this.declarationRepository = Repositories.getInstance().getDeclarationRepository();
    }

    public ExportDeclarationCsvController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    public boolean exportToCsv(String filePath) {
        List<Declaration> validated = declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        try {
            return DeclarationCsvExporter.export(validated, filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
