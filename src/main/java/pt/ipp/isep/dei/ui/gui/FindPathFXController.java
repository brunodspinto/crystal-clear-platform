package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import pt.ipp.isep.dei.controller.FindPathController;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the pathway verification between two entities (US34).
 * The Ethics Committee Member picks the source and target entities and the screen shows
 * whether they are connected in the support graph and, if so, the shortest
 * distance (number of edges). Reuses the same {@link FindPathController} as
 * the console UI.
 */
public class FindPathFXController implements Initializable {

    @FXML private DatePicker datePicker;
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

        loadEntities(null);

        // changing the snapshot date re-lists the entities active on that date
        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> obs,
                                LocalDate old, LocalDate now) {
                loadEntities(now);
            }
        });
    }

    private void loadEntities(LocalDate date) {
        clearMessage();
        resultLabel.setText("");
        sourceCombo.getItems().clear();
        targetCombo.getItems().clear();

        List<String> entityIds;
        try {
            entityIds = date == null
                    ? controller.getEntityIds()
                    : controller.getEntityIds(date.toString());
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            sourceCombo.setDisable(true);
            targetCombo.setDisable(true);
            return;
        }

        if (entityIds.size() < 2) {
            showError("The graph must contain at least two entities"
                    + (date != null ? " active on " + date + "." : "."));
            sourceCombo.setDisable(true);
            targetCombo.setDisable(true);
            return;
        }

        sourceCombo.setDisable(false);
        targetCombo.setDisable(false);
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

        LocalDate date = datePicker.getValue();
        FindPathController.PathResult result;
        try {
            result = date == null
                    ? controller.findPath(sourceId, targetId)
                    : controller.findPath(date.toString(), sourceId, targetId);
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
            mainController.showEthicsCommitteeMenu();
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
