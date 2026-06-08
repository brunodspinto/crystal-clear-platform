package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;
import pt.ipp.isep.dei.controller.AssessComplaintController;
import pt.ipp.isep.dei.domain.Complaint;
import pt.ipp.isep.dei.domain.ComplaintItem;
import pt.ipp.isep.dei.domain.ComplaintOutcome;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * GUI controller for US27 - Assess a citizen complaint. The Ethics Committee member
 * selects a complaint, reviews its details and grievances, chooses an outcome
 * (valid/invalid) and confirms. A reason is mandatory when the outcome is invalid.
 */
public class AssessComplaintFXController implements Initializable {

    @FXML private ComboBox<Complaint> complaintCombo;
    @FXML private Label detailAgent;
    @FXML private Label detailCitizen;
    @FXML private Label detailSubmitted;
    @FXML private ListView<ComplaintItem> grievancesList;
    @FXML private ComboBox<ComplaintOutcome> outcomeCombo;
    @FXML private TextArea reasonArea;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final AssessComplaintController controller = new AssessComplaintController();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadComplaints();

        complaintCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Complaint c) { return c == null ? "" : c.toString(); }
            @Override public Complaint fromString(String s) { return null; }
        });
        complaintCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> showDetails(selected));

        outcomeCombo.setItems(FXCollections.observableArrayList(ComplaintOutcome.values()));

        clearDetails();
        clearMessage();
    }

    private void loadComplaints() {
        complaintCombo.setItems(FXCollections.observableArrayList(controller.getComplaints()));
    }

    private void showDetails(Complaint c) {
        if (c == null) {
            clearDetails();
            return;
        }
        detailAgent.setText(c.getPoliticalAgent().getName());
        detailCitizen.setText(c.getCitizen().getName());
        detailSubmitted.setText(DATE_FMT.format(c.getSubmissionDate()));
        grievancesList.setItems(FXCollections.observableArrayList(c.getItems()));
        clearMessage();
    }

    @FXML
    private void handleAssess() {
        clearMessage();

        Complaint complaint = complaintCombo.getValue();
        if (complaint == null) {
            showError("Select a complaint to assess.");
            return;
        }

        ComplaintOutcome outcome = outcomeCombo.getValue();
        if (outcome == null) {
            showError("Select an outcome.");
            return;
        }

        String reason = reasonArea.getText() == null ? "" : reasonArea.getText().trim();
        if (outcome == ComplaintOutcome.INVALID && reason.isBlank()) {
            showError("A reason is mandatory when the complaint is invalid.");
            return;
        }

        try {
            boolean success = controller.assessComplaint(complaint, outcome, reason.isBlank() ? null : reason);
            if (success) {
                showSuccess("Complaint assessed as " + outcome + ".");
                resetForm();
            } else {
                showError("Operation failed: authenticated member not found.");
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showEthicsCommitteeMenu();
        }
    }

    private void resetForm() {
        loadComplaints();
        complaintCombo.getSelectionModel().clearSelection();
        outcomeCombo.getSelectionModel().clearSelection();
        clearDetails();
    }

    private void clearDetails() {
        detailAgent.setText("");
        detailCitizen.setText("");
        detailSubmitted.setText("");
        grievancesList.getItems().clear();
        reasonArea.clear();
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
