package pt.ipp.isep.dei.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import pt.ipp.isep.dei.Bootstrap;
import pt.ipp.isep.dei.repository.Repositories;
import pt.ipp.isep.dei.repository.RepositoriesFile;

/**
 * JavaFX entry point for the citizen and journalist features of the
 * Crystal Clear platform.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Repositories loaded = new RepositoriesFile().load();
        if (loaded != null) {
            Repositories.setInstance(loaded);
        }
        new Bootstrap().run();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.setHostServices(getHostServices());
        mainController.showLogin();

        Scene scene = new Scene(root, 720, 480);
        stage.setTitle("Crystal Clear");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Called by JavaFX when the application is shutting down. Persists the
     * repositories so the data is available on the next run.
     */
    @Override
    public void stop() throws Exception {
        new RepositoriesFile().save(Repositories.getInstance());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
