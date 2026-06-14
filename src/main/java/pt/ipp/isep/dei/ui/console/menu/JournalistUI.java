package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.AnalyseIncomeEvolutionUI;
import pt.ipp.isep.dei.ui.console.ConsultAssetsUI;
import pt.ipp.isep.dei.ui.console.NetworkDynamicsUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Journalists.
 */
public class JournalistUI implements Runnable {

    /**
     * Instantiates a new Journalist ui.
     */
    public JournalistUI() {}

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Analyse Income Evolution", new AnalyseIncomeEvolutionUI()));
        options.add(new MenuItem("Consult Assets", new ConsultAssetsUI()));
        options.add(new MenuItem("Network Dynamics Over Time (US32)", new NetworkDynamicsUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- JOURNALIST MENU -------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
