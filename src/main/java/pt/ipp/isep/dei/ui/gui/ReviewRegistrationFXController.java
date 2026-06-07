package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import pt.ipp.isep.dei.controller.ReviewRegistrationController;
import pt.ipp.isep.dei.domain.RegistrationRequest;
import pt.ipp.isep.dei.domain.UserRole;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for US02 - Accept/Reject Registration Requests.
 */
public class ReviewRegistrationFXController implements Initializable {

    @FXML private ListView<RegistrationRequest> requestList;
    @FXML private Label detailName;
    @FXML private Label detailEmail;
    @FXML private Label detailRole;
    @FXML private Label detailDate;
    @FXML private Label detailDoc;
    @FXML private TextArea reasonArea;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ReviewRegistrationController controller = new ReviewRegistrationController();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRequests();

        requestList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> showDetails(selected));

        requestList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(RegistrationRequest r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null : r.getFullName() + " (" + r.getRole() + ")");
            }
        });
    }

    private void loadRequests() {
        List<RegistrationRequest> pending = controller.getPendingRequests();
        requestList.setItems(FXCollections.observableArrayList(pending));
        clearDetails();
        clearMessage();
    }

    private void showDetails(RegistrationRequest r) {
        if (r == null) {
            clearDetails();
            return;
        }
        detailName.setText(r.getFullName());
        detailEmail.setText(r.getEmail());
        detailRole.setText(r.getRole().toString());
        detailDate.setText(DATE_FMT.format(r.getSubmissionDate()));
        if (r.getRole() == UserRole.JOURNALIST) {
            detailDoc.setText("Press card: " + r.getIdentificationDocument());
        } else if (r.getRole() == UserRole.CITIZEN) {
            detailDoc.setText("National ID: " + r.getIdentificationDocument());
        } else {
            detailDoc.setText("");
        }
        acceptButton.setDisable(false);
        rejectButton.setDisable(false);
        clearMessage();
    }

    private void clearDetails() {
        detailName.setText("");
        detailEmail.setText("");
        detailRole.setText("");
        detailDate.setText("");
        detailDoc.setText("");
        reasonArea.clear();
        acceptButton.setDisable(true);
        rejectButton.setDisable(true);
    }

    @FXML
    private void handleAccept() {
        RegistrationRequest selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        controller.approveRequest(selected);
        showSuccess("Request from " + selected.getFullName() + " ACCEPTED.");
        loadRequests();
    }

    @FXML
    private void handleReject() {
        RegistrationRequest selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String reason = reasonArea.getText() == null ? "" : reasonArea.getText().trim();
        if (reason.isBlank()) {
            showError("Rejection reason is mandatory.");
            return;
        }
        controller.rejectRequest(selected, reason);
        showSuccess("Request from " + selected.getFullName() + " REJECTED.");
        loadRequests();
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
