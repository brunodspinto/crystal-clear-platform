package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import pt.ipp.isep.dei.controller.FindPathController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the pathway verification between two entities (US34).
 * The administrator picks the source and target entities and the screen shows
 * whether they are connected in the support graph and, if so, the shortest
 * distance (number of edges). Reuses the same {@link FindPathController} as
 * the console UI.
 */
public class FindPathFXController implements Initializable {

    @FXML private ComboBox<String> sourceCombo;
    @FXML private ComboBox<String> targetCombo;
    @FXML private Label resultLabel;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final FindPathController controller = new FindPathController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
        resultLabel.setText("");

        List<String> entityIds;
        try {
            entityIds = controller.getEntityIds();
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            sourceCombo.setDisable(true);
            targetCombo.setDisable(true);
            return;
        }

        if (entityIds.size() < 2) {
            showError("The graph must contain at least two entities.");
            sourceCombo.setDisable(true);
            targetCombo.setDisable(true);
            return;
        }

        sourceCombo.getItems().addAll(entityIds);
        targetCombo.getItems().addAll(entityIds);
    }

    @FXML
    private void handleFindPath() {
        clearMessage();
        resultLabel.setText("");

        String sourceId = sourceCombo.getSelectionModel().getSelectedItem();
        String targetId = targetCombo.getSelectionModel().getSelectedItem();
        if (sourceId == null || targetId == null) {
            showError("Select both the source and the target entity first.");
            return;
        }

        FindPathController.PathResult result;
        try {
            result = controller.findPath(sourceId, targetId);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        if (result.hasPath()) {
            resultLabel.setText("Path EXISTS between " + result.getSourceId()
                    + " and " + result.getTargetId()
                    + " — distance: " + result.getDistance() + " edge(s).");
            showSuccess("Pathway verified on the support graph (undirected, unweighted).");
        } else {
            resultLabel.setText("No path found between " + result.getSourceId()
                    + " and " + result.getTargetId() + ".");
            clearMessage();
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
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
