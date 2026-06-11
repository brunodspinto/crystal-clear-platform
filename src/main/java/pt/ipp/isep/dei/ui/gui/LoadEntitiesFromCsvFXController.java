package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import pt.ipp.isep.dei.controller.LoadEntitiesFromCsvController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for US19 - Load entities from a CSV file into the graph.
 * The administrator picks the CSV file and loads it; after a successful load
 * the entity graph can also be rendered to an SVG file using Graphviz.
 * Reuses the same {@link LoadEntitiesFromCsvController} as the console UI.
 */
public class LoadEntitiesFromCsvFXController implements Initializable {

    private static final String DEFAULT_OUTPUT_SVG = "docs/system-documentation/US19/us19_entities_graph.svg";

    @FXML private TextField filePathField;
    @FXML private Button renderButton;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final LoadEntitiesFromCsvController controller = new LoadEntitiesFromCsvController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
        renderButton.setDisable(true);
    }

    @FXML
    private void handleBrowse() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Entities CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showOpenDialog(
                mainController != null ? mainController.getStage() : null);
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleLoad() {
        clearMessage();
        String path = filePathField.getText() == null ? "" : filePathField.getText().trim();
        if (path.isBlank()) {
            showError("Enter or browse to an entities CSV file.");
            return;
        }
        try {
            int count = controller.loadEntities(path);
            renderButton.setDisable(false);
            showSuccess(count + " entities loaded successfully.");
        } catch (IOException e) {
            showError("Failed to load entities: " + e.getMessage());
        }
    }

    @FXML
    private void handleRender() {
        clearMessage();
        try {
            String svgPath = controller.renderEntitiesToSvg(DEFAULT_OUTPUT_SVG);
            showSuccess("Graph rendered: " + svgPath);
        } catch (IOException e) {
            showError("Failed to write the DOT file: " + e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private void showError(String msg) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.getStyleClass().setAll("message-success");
        messageLabel.setText(msg);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }
}
