package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.ComplaintItem;
import pt.ipp.isep.dei.domain.PoliticalAgent;
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
 * citizen presses "Submit complaint". The logged-in citizen is taken from the
 * active session by the controller.
 */
public class SubmitComplaintFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgent> agentCombo;
    @FXML private ComboBox<PoliticalFunction> functionCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private ListView<ComplaintItem> grievancesList;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final SubmitComplaintController controller = new SubmitComplaintController();
    private Complaint currentComplaint;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentCombo.setItems(FXCollections.observableArrayList(controller.getPoliticalAgents()));
        agentCombo.setConverter(new StringConverter<>() {
            @Override public String toString(PoliticalAgent a) { return a == null ? "" : a.getName(); }
            @Override public PoliticalAgent fromString(String s) { return null; }
        });

        functionCombo.setItems(FXCollections.observableArrayList(controller.getPoliticalFunctions()));
        clearMessage();
    }

    @FXML
    private void handleAddGrievance() {
        clearMessage();

        PoliticalAgent agent = agentCombo.getValue();
        PoliticalFunction function = functionCombo.getValue();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        if (agent == null || function == null || date == null || description.isBlank()) {
            showError("Select an agent, a function, a date and write a description.");
            return;
        }

        Date complaintDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (currentComplaint == null) {
            currentComplaint = controller.createComplaint(agent);
            if (currentComplaint == null) {
                showError("Could not identify the logged-in citizen.");
                return;
            }
        }

        try {
            controller.addGrievance(currentComplaint, description, complaintDate, function);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        agentCombo.setDisable(true);
        refreshGrievanceList();
        clearGrievanceFields();
        showSuccess("Grievance added (" + currentComplaint.getItemCount() + " in this complaint).");
    }

    @FXML
    private void handleSubmit() {
        clearMessage();

        if (currentComplaint == null || currentComplaint.getItemCount() == 0) {
            showError("Add at least one grievance before submitting.");
            return;
        }

        int count = currentComplaint.getItemCount();
        if (controller.saveComplaint(currentComplaint)) {
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
        if (mainController != null) {
            mainController.showCitizenMenu();
        }
    }

    private void refreshGrievanceList() {
        if (currentComplaint == null) {
            grievancesList.getItems().clear();
        } else {
            grievancesList.setItems(FXCollections.observableArrayList(currentComplaint.getItems()));
        }
    }

    private void clearGrievanceFields() {
        functionCombo.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        descriptionArea.clear();
    }

    private void resetAll() {
        currentComplaint = null;
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
