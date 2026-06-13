package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import pt.ipp.isep.dei.controller.SubnetworkController;
import pt.ipp.isep.dei.domain.graph.SubnetworkExtractor.SubnetworkResult;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the influence subnetwork visualisation (US36). The user
 * picks an origin entity and the screen lists every entity in the connected
 * component that contains it. Reuses the same {@link SubnetworkController}
 * as the console UI; the SVG export remains available on the console side.
 */
public class SubnetworkFXController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> originCombo;
    @FXML private ListView<String> nodesList;
    @FXML private Label resultLabel;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final SubnetworkController controller = new SubnetworkController();
    private final ObservableList<String> nodeItems = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nodesList.setItems(nodeItems);
        nodesList.setPlaceholder(new Label("No subnetwork has been extracted yet."));
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
        nodeItems.clear();
        originCombo.getItems().clear();

        List<String> entityIds;
        try {
            entityIds = date == null
                    ? controller.getEntityIds()
                    : controller.getEntityIds(date.toString());
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            originCombo.setDisable(true);
            return;
        }

        if (entityIds.isEmpty()) {
            showError("No entities available"
                    + (date != null ? " on " + date + "." : " in the current graph."));
            originCombo.setDisable(true);
            return;
        }

        originCombo.setDisable(false);
        originCombo.getItems().addAll(entityIds);
        originCombo.setPromptText("Select entity…");
    }

    @FXML
    private void handleExtract() {
        clearMessage();
        resultLabel.setText("");
        nodeItems.clear();

        String originId = originCombo.getSelectionModel().getSelectedItem();
        if (originId == null) {
            showError("Select the origin entity first.");
            return;
        }

        LocalDate date = datePicker.getValue();
        SubnetworkResult result;
        try {
            result = date == null
                    ? controller.extractSubnetwork(originId)
                    : controller.extractSubnetwork(date.toString(), originId);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            return;
        }

        for (String nodeId : result.getNodeIds()) {
            nodeItems.add(nodeId);
        }
        resultLabel.setText("Subnetwork of " + result.getOriginId() + ": "
                + result.size() + " entity(ies).");
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

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }
}
