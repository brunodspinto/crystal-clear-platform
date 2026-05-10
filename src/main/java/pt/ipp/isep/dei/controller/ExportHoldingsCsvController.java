package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.HoldingsCsvExporter;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US25 - Export Holdings Dataset to CSV.
 */
public class ExportHoldingsCsvController {

    private final DeclarationRepository declarationRepository;

    public ExportHoldingsCsvController() {
        this.declarationRepository = Repositories.getInstance().getDeclarationRepository();
    }

    public ExportHoldingsCsvController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    public boolean exportToCsv(String filePath) {
        List<Declaration> validated = declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        try {
            return HoldingsCsvExporter.export(validated, filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
