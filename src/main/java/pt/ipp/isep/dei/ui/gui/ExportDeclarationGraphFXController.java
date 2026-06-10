package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.ExportDeclarationGraphController;
import pt.ipp.isep.dei.domain.graph.Edge;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for exporting the declaration interest graph (US37). The
 * administrator optionally picks a family relationships seed CSV, chooses the
 * two output files (entities and relations) and triggers the export, which
 * infers family ties from the seed. Reuses the same
 * {@link ExportDeclarationGraphController} as the console UI.
 */
public class ExportDeclarationGraphFXController implements Initializable {

    @FXML private TextField familySeedField;
    @FXML private TextField entitiesField;
    @FXML private TextField relationsField;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ExportDeclarationGraphController controller = new ExportDeclarationGraphController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
    }

    @FXML
    private void handleBrowseFamilySeed() {
        clearMessage();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Family Relationships CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File selected = chooser.showOpenDialog(currentStage());
        if (selected != null) {
            familySeedField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleClearFamilySeed() {
        clearMessage();
        familySeedField.setText("");
    }

    @FXML
    private void handleBrowseEntities() {
        clearMessage();
        File selected = saveDialog("Save Entities CSV", "entities.csv");
        if (selected != null) {
            entitiesField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowseRelations() {
        clearMessage();
        File selected = saveDialog("Save Relations CSV", "relations.csv");
        if (selected != null) {
            relationsField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleExport() {
        clearMessage();

        if (!controller.hasData()) {
            showError("No validated declarations to export.");
            return;
        }

        String entitiesPath = entitiesField.getText() == null ? "" : entitiesField.getText().trim();
        String relationsPath = relationsField.getText() == null ? "" : relationsField.getText().trim();
        if (entitiesPath.isBlank() || relationsPath.isBlank()) {
            showError("Choose both output files first.");
            return;
        }

        List<Edge> familySeed = new ArrayList<>();
        String seedPath = familySeedField.getText() == null ? "" : familySeedField.getText().trim();
        if (!seedPath.isBlank()) {
            try {
                familySeed = controller.loadFamilySeed(seedPath);
            } catch (IOException e) {
                showError("Could not read family seed file: " + e.getMessage());
                return;
            }
        }

        int inferred = controller.inferFamilyRelationships(familySeed).size() - familySeed.size();
        boolean success = controller.export(familySeed, entitiesPath, relationsPath);

        if (success) {
            StringBuilder sb = new StringBuilder();
            sb.append("Export successful.\n");
            sb.append("Entities : ").append(entitiesPath).append("\n");
            sb.append("Relations: ").append(relationsPath);
            if (!familySeed.isEmpty()) {
                sb.append("\nFamily ties: ").append(familySeed.size())
                        .append(" seed + ").append(inferred).append(" inferred.");
            }
            showSuccess(sb.toString());
        } else {
            showError("Export failed (I/O error). Check the paths and try again.");
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private File saveDialog(String title, String initialName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.setInitialFileName(initialName);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        return chooser.showSaveDialog(currentStage());
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
