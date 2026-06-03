package pt.ipp.isep.dei.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import pt.ipp.isep.dei.Bootstrap;

/**
 * JavaFX entry point for the citizen and journalist features of the
 * Crystal Clear platform.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new Bootstrap().run();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.showLogin();

        Scene scene = new Scene(root, 720, 480);
        stage.setTitle("Crystal Clear");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
