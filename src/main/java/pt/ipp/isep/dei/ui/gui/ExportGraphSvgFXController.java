package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import pt.ipp.isep.dei.controller.ExportGraphSvgController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for US26 - Export the heterogeneous graph as an interactive
 * SVG file with hyperlinks. The administrator picks the entities CSV, the
 * relations CSV and the output SVG file, then exports.
 * Reuses the same {@link ExportGraphSvgController} as the console UI.
 */
public class ExportGraphSvgFXController implements Initializable {

    @FXML private TextField entitiesPathField;
    @FXML private TextField relationsPathField;
    @FXML private TextField outputPathField;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ExportGraphSvgController controller = new ExportGraphSvgController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
    }

    @FXML
    private void handleBrowseEntities() {
        File file = chooseOpenFile("Select Entities CSV", "CSV files", "*.csv");
        if (file != null) {
            entitiesPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowseRelations() {
        File file = chooseOpenFile("Select Relations CSV", "CSV files", "*.csv");
        if (file != null) {
            relationsPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowseOutput() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Graph SVG");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG files", "*.svg"));
        chooser.setInitialFileName("graph.svg");
        File file = chooser.showSaveDialog(
                mainController != null ? mainController.getStage() : null);
        if (file != null) {
            outputPathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleExport() {
        clearMessage();
        String entitiesPath = textOf(entitiesPathField);
        String relationsPath = textOf(relationsPathField);
        String outputPath = textOf(outputPathField);

        if (entitiesPath.isBlank() || relationsPath.isBlank() || outputPath.isBlank()) {
            showError("Fill in the entities CSV, relations CSV and output SVG paths.");
            return;
        }

        try {
            int count = controller.exportToSvg(entitiesPath, relationsPath, outputPath);
            showSuccess("SVG exported successfully with " + count + " entities. File: " + outputPath);
        } catch (Exception e) {
            showError("Export failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private File chooseOpenFile(String title, String filterName, String filterPattern) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName, filterPattern));
        return chooser.showOpenDialog(
                mainController != null ? mainController.getStage() : null);
    }

    private String textOf(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
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
