package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.ExportDeclarationCsvUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Product Owner users.
 */
public class ProductOwnerUI implements Runnable {

    public ProductOwnerUI() {}

    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Export Declaration (CSV)", new ExportDeclarationCsvUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- PRODUCT OWNER MENU -----------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
