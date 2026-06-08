package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.dto.ComplaintItemDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;
import pt.ipp.isep.dei.domain.PoliticalFunction;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * GUI controller for submitting a complaint about a political agent (US12).
 * A complaint targets a single political agent but may contain several
 * grievances. The citizen picks the agent, then adds one or more grievances
 * (function held at the time, description and date); the agent is locked after
 * the first grievance is added. The whole complaint is persisted once, when the
 * citizen presses "Submit complaint".
 *
 * <p>The UI works only with DTOs ({@link PoliticalAgentDTO}, {@link ComplaintItemDTO})
 * and primitives; the complaint being built is kept by the controller, so the UI
 * never touches domain objects (ESOFT &mdash; DTO pattern).</p>
 */
public class SubmitComplaintFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgentDTO> agentCombo;
    @FXML private ComboBox<PoliticalFunction> functionCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private ListView<ComplaintItemDTO> grievancesList;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final SubmitComplaintController controller = new SubmitComplaintController();
    private boolean complaintStarted;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentCombo.setItems(FXCollections.observableArrayList(controller.getPoliticalAgents()));
        functionCombo.setItems(FXCollections.observableArrayList(controller.getPoliticalFunctions()));
        clearMessage();
    }

    @FXML
    private void handleAddGrievance() {
        clearMessage();

        PoliticalAgentDTO agent = agentCombo.getValue();
        PoliticalFunction function = functionCombo.getValue();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        if (agent == null || function == null || date == null || description.isBlank()) {
            showError("Select an agent, a function, a date and write a description.");
            return;
        }

        Date complaintDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (!complaintStarted) {
            if (!controller.startComplaint(agent)) {
                showError("Could not start the complaint (could not identify the citizen or agent).");
                return;
            }
            complaintStarted = true;
            agentCombo.setDisable(true);
        }

        try {
            controller.addGrievance(description, complaintDate, function);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        refreshGrievanceList();
        clearGrievanceFields();
        showSuccess("Grievance added (" + controller.getCurrentGrievanceCount() + " in this complaint).");
    }

    @FXML
    private void handleSubmit() {
        clearMessage();

        if (controller.getCurrentGrievanceCount() == 0) {
            showError("Add at least one grievance before submitting.");
            return;
        }

        int count = controller.getCurrentGrievanceCount();
        if (controller.submitComplaint()) {
            resetAll();
            showSuccess("Complaint with " + count + " grievance(s) successfully submitted!");
        } else {
            showError("Complaint not submitted.");
        }
    }

    @FXML
    private void handleClear() {
        resetAll();
        clearMessage();
    }

    @FXML
    private void handleBack() {
        controller.cancelComplaint();
        if (mainController != null) {
            mainController.showCitizenMenu();
        }
    }

    private void refreshGrievanceList() {
        grievancesList.setItems(FXCollections.observableArrayList(controller.getCurrentGrievances()));
    }

    private void clearGrievanceFields() {
        functionCombo.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        descriptionArea.clear();
    }

    private void resetAll() {
        controller.cancelComplaint();
        complaintStarted = false;
        agentCombo.setDisable(false);
        agentCombo.getSelectionModel().clearSelection();
        clearGrievanceFields();
        grievancesList.getItems().clear();
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
