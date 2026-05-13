package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.DeclarationStatus;
import pt.ipp.isep.dei.domain.HoldingsCsvExporter;
import pt.ipp.isep.dei.repository.DeclarationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US25 - Export the holdings dataset to a CSV file.
 */
public class ExportHoldingsCsvController {

    private final DeclarationRepository declarationRepository;

    /**
     * Creates a controller with the default repository.
     */
    public ExportHoldingsCsvController() {
        this.declarationRepository = Repositories.getInstance().getDeclarationRepository();
    }

    /**
     * Creates a controller with a given repository.
     *
     * @param declarationRepository the repository to read declarations from.
     */
    public ExportHoldingsCsvController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    /**
     * Exports all validated declarations to a CSV file in the holdings format.
     *
     * @param filePath path where the CSV file will be saved.
     * @return true if the export was successful, false if an error occurred.
     */
    public boolean exportToCsv(String filePath) {
        List<Declaration> validated = declarationRepository.getDeclarationsByStatus(DeclarationStatus.VALIDATED);
        try {
            return HoldingsCsvExporter.export(validated, filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
