package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.ExportDeclarationCsvController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for exporting the validated declarations dataset to CSV (US24).
 * The administrator chooses a destination file through a {@link FileChooser}
 * and triggers the export, which reuses the same
 * {@link ExportDeclarationCsvController} as the console UI.
 */
public class ExportDeclarationCsvFXController implements Initializable {

    private static final String DEFAULT_FILE_NAME = "declarations.csv";

    @FXML private TextField pathField;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ExportDeclarationCsvController controller = new ExportDeclarationCsvController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
    }

    @FXML
    private void handleBrowse() {
        clearMessage();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Declarations Dataset");
        chooser.setInitialFileName(DEFAULT_FILE_NAME);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File selected = chooser.showSaveDialog(currentStage());
        if (selected != null) {
            pathField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleExport() {
        clearMessage();

        String path = pathField.getText() == null ? "" : pathField.getText().trim();
        if (path.isBlank()) {
            showError("Choose a destination file first.");
            return;
        }

        boolean exported = controller.exportToCsv(path);
        if (exported) {
            showSuccess("Validated declarations exported to:\n" + path);
        } else {
            showError("Export failed. Check the path and try again.");
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private Stage currentStage() {
        if (mainController != null && mainController.getStage() != null) {
            return mainController.getStage();
        }
        return null;
    }

    private void showError(String message) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.getStyleClass().setAll("message-success");
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }
}
