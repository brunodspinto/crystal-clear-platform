package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.ShowTextUI;
import pt.ipp.isep.dei.ui.console.SubmitDeclarationUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Political Agents.
 */
public class PoliticalAgentUI implements Runnable {

    public PoliticalAgentUI() {}

    /**
     * Displays the political agent menu and dispatches to the selected option.
     */
    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Submit Declaration of Interests", new SubmitDeclarationUI()));
        options.add(new MenuItem("Option 2", new ShowTextUI("You have chosen Option 2.")));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- POLITICAL AGENT MENU -------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
