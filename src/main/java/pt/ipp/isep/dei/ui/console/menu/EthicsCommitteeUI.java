package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.AssetEvolutionUI;
import pt.ipp.isep.dei.ui.console.ShowTextUI;
import pt.ipp.isep.dei.ui.console.ConsultIntegratedSituationUI;
import pt.ipp.isep.dei.ui.console.ValidateDeclarationUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Ethics Committee Members.
 */
public class EthicsCommitteeUI implements Runnable {

    public EthicsCommitteeUI() {}

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Validate Declaration of Interests", new ValidateDeclarationUI()));
        options.add(new MenuItem("Examine Asset & Net Worth Evolution", new AssetEvolutionUI()));
        options.add(new MenuItem("Consult Integrated Situation", new ConsultIntegratedSituationUI()));
        options.add(new MenuItem("Option 4", new ShowTextUI("You have chosen Option 3.")));
        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options, "\n\n--- ETHICS COMMITTEE MENU -------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
