package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.BuildRelationsGraphUI;
import pt.ipp.isep.dei.ui.console.CreateTaskUI;
import pt.ipp.isep.dei.ui.console.ExportDeclarationCsvUI;
import pt.ipp.isep.dei.ui.console.ExportGraphSvgUI;
import pt.ipp.isep.dei.ui.console.ExportHoldingsCsvUI;
import pt.ipp.isep.dei.ui.console.GenerateAdjacencyMatricesUI;
import pt.ipp.isep.dei.ui.console.LoadEntitiesFromCsvUI;
import pt.ipp.isep.dei.ui.console.RegisterOrganizationUI;
import pt.ipp.isep.dei.ui.console.ReviewRegistrationUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated administrators.
 */
public class AdminUI implements Runnable {

    /**
     * Instantiates a new Admin ui.
     */
    public AdminUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<MenuItem>();

        options.add(new MenuItem("Create Task", new CreateTaskUI()));
        options.add(new MenuItem("Register Organization", new RegisterOrganizationUI()));
        options.add(new MenuItem("Load Entities from CSV", new LoadEntitiesFromCsvUI()));
        options.add(new MenuItem("Build Relations Graph", new BuildRelationsGraphUI()));
        options.add(new MenuItem("Generate Adjacency Matrices", new GenerateAdjacencyMatricesUI()));
        options.add(new MenuItem("Review Registration Requests", new ReviewRegistrationUI()));
        options.add(new MenuItem("Export Declaration (CSV)", new ExportDeclarationCsvUI()));
        options.add(new MenuItem("Export Holdings (CSV)", new ExportHoldingsCsvUI()));
        options.add(new MenuItem("Export Graph (SVG)", new ExportGraphSvgUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- ADMIN MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
