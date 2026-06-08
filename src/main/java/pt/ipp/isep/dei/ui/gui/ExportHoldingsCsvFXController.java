package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import pt.ipp.isep.dei.controller.ExportHoldingsCsvController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for US25 - Export Holdings Dataset to CSV.
 */
public class ExportHoldingsCsvFXController implements Initializable {

    @FXML private TextField filePathField;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ExportHoldingsCsvController controller = new ExportHoldingsCsvController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
    }

    @FXML
    private void handleBrowse() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Holdings CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        chooser.setInitialFileName("holdings.csv");
        File file = chooser.showSaveDialog(
                mainController != null ? mainController.getStage() : null);
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleExport() {
        clearMessage();
        String path = filePathField.getText() == null ? "" : filePathField.getText().trim();
        if (path.isBlank()) {
            showError("Enter or browse to an output file path.");
            return;
        }
        boolean ok = controller.exportToCsv(path);
        if (ok) {
            showSuccess("Export successful: " + path);
        } else {
            showError("Export failed. Check the file path and try again.");
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
