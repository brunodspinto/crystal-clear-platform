package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import pt.ipp.isep.dei.controller.SubmitComplaintController;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PoliticalFunction;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * GUI controller for submitting a complaint about a political agent (US12).
 * The citizen picks the agent and the function held at the time, writes a
 * description, chooses the date of the behaviour, and submits. The logged-in
 * citizen is taken from the active session by the controller.
 */
public class SubmitComplaintFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgent> agentCombo;
    @FXML private ComboBox<PoliticalFunction> functionCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final SubmitComplaintController controller = new SubmitComplaintController();

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
        messageLabel.setText("");
    }

    @FXML
    private void handleSubmit() {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText("");

        PoliticalAgent agent = agentCombo.getValue();
        PoliticalFunction function = functionCombo.getValue();
        LocalDate date = datePicker.getValue();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        if (agent == null || function == null || date == null || description.isBlank()) {
            messageLabel.setText("Select an agent, a function, a date and write a description.");
            return;
        }

        Date complaintDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        boolean success;
        try {
            success = controller.submitComplaint(description, complaintDate, agent, function);
        } catch (IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());
            return;
        }

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Complaint successfully submitted!");
            clearForm();
        } else {
            messageLabel.setText("Complaint not submitted.");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        messageLabel.setText("");
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showCitizenMenu();
        }
    }

    private void clearForm() {
        agentCombo.getSelectionModel().clearSelection();
        functionCombo.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        descriptionArea.clear();
    }
}
