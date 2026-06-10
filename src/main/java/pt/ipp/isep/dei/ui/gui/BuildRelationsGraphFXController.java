package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.BuildRelationsGraphController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for building the relations graph from a CSV file (US20).
 * The administrator picks the relations CSV through a {@link FileChooser},
 * builds the graph and can then render it to an SVG using Graphviz. Reuses
 * the same {@link BuildRelationsGraphController} as the console UI.
 */
public class BuildRelationsGraphFXController implements Initializable {

    private static final String DEFAULT_SVG_NAME = "us20_relations_graph.svg";

    @FXML private TextField pathField;
    @FXML private Label messageLabel;
    @FXML private Button renderButton;

    private MainController mainController;
    private final BuildRelationsGraphController controller = new BuildRelationsGraphController();

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
        clearMessage();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Relations CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File selected = chooser.showOpenDialog(currentStage());
        if (selected != null) {
            pathField.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleBuild() {
        clearMessage();

        String path = pathField.getText() == null ? "" : pathField.getText().trim();
        if (path.isBlank()) {
            showError("Choose the relations CSV file first.");
            return;
        }

        try {
            BuildRelationsGraphController.BuildResult result = controller.buildFromCsv(path);
            StringBuilder sb = new StringBuilder();
            sb.append("Relations graph built successfully.\n");
            sb.append("Edges loaded: ").append(result.getEdgeCount());
            sb.append("  |  Nodes: ").append(result.getNodeCount());
            sb.append("  |  Relation labels: ").append(result.getLabels().size()).append("\n");
            for (String label : result.getLabels()) {
                sb.append("  - ").append(label).append("\n");
            }
            showSuccess(sb.toString());
            renderButton.setDisable(false);
        } catch (IOException e) {
            showError("Failed to load relations: " + e.getMessage());
            renderButton.setDisable(true);
        }
    }

    @FXML
    private void handleRenderSvg() {
        clearMessage();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Graph SVG");
        chooser.setInitialFileName(DEFAULT_SVG_NAME);
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg"));

        File selected = chooser.showSaveDialog(currentStage());
        if (selected == null) {
            return;
        }

        try {
            String svgPath = controller.renderGraphToSvg(selected.getAbsolutePath());
            showSuccess("Graph rendered to:\n" + svgPath);
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
