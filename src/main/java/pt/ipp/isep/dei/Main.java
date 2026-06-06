package pt.ipp.isep.dei;

import pt.ipp.isep.dei.repository.Repositories;
import pt.ipp.isep.dei.repository.RepositoriesFile;
import pt.ipp.isep.dei.ui.console.menu.MainMenuUI;

/**
 * The type Main.
 */
public class Main {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Repositories loaded = new RepositoriesFile().load();
        if (loaded != null) {
            Repositories.setInstance(loaded);
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();
        System.out.println(".");
        try {
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new RepositoriesFile().save(Repositories.getInstance());
        }
    }
}