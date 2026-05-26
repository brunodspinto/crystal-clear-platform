package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.ui.console.AssetEvolutionUI;
import pt.ipp.isep.dei.ui.console.ConsultIntegratedSituationUI;
import pt.ipp.isep.dei.ui.console.DetectConflictsUI;
import pt.ipp.isep.dei.ui.console.DetectNepotismUI;
import pt.ipp.isep.dei.ui.console.FindPathUI;
import pt.ipp.isep.dei.ui.console.SubnetworkUI;
import pt.ipp.isep.dei.ui.console.ValidateDeclarationUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu for authenticated Ethics Committee Members.
 */
public class EthicsCommitteeUI implements Runnable {

    /**
     * Instantiates a new Ethics committee ui.
     */
    public EthicsCommitteeUI() {}

    @Override
    public void run() {
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Validate Declaration of Interests",
                new ValidateDeclarationUI()));
        options.add(new MenuItem("Examine Asset & Net Worth Evolution",
                new AssetEvolutionUI()));
        options.add(new MenuItem("Consult Integrated Situation",
                new ConsultIntegratedSituationUI()));
        options.add(new MenuItem(
                "Detect Direct Nepotism – appointed by relative/friend/associate (US22)",
                new DetectNepotismUI()));
        options.add(new MenuItem(
                "Detect Indirect Nepotism & Conflicts of Interest via relation chains (US23)",
                new DetectConflictsUI()));
        options.add(new MenuItem(
                "Verify Pathway Between Two Entities (US34)",
                new FindPathUI()));
        options.add(new MenuItem(
                "Visualise Influence Subnetwork for an Entity (US36)",
                new SubnetworkUI()));

        int option = 0;
        do {
            option = Utils.showAndSelectIndex(options,
                    "\n\n--- ETHICS COMMITTEE MENU -------------------------");
            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }
}
