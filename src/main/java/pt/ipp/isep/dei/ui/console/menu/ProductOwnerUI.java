package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.ExportDeclarationCsvUI;
import pt.ipp.isep.dei.ui.console.ExportGraphSvgUI;
import pt.ipp.isep.dei.ui.console.ExportHoldingsCsvUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Product Owner users.
 *
 * Per the forum clarifications, the Product Owner is a Scrum role rather than
 * a system user. The features the PO requested are exposed to the actual
 * actors (administrators, journalists, etc.) in their own menus. This menu is
 * kept for the export utilities currently associated with the PO role.
 */
public class ProductOwnerUI implements Runnable {

    public ProductOwnerUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Export Declaration (CSV)", new ExportDeclarationCsvUI()));
        options.add(new MenuItem("Export Holdings (CSV)", new ExportHoldingsCsvUI()));
        options.add(new MenuItem("Export Graph (SVG with hyperlinks)", new ExportGraphSvgUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PRODUCT OWNER MENU -----------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
