package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import pt.ipp.isep.dei.controller.ReviewRegistrationController;
import pt.ipp.isep.dei.dto.RegistrationRequestDTO;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for US02 - Accept/Reject Registration Requests.
 */
public class ReviewRegistrationFXController implements Initializable {

    @FXML private ListView<RegistrationRequestDTO> requestList;
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

    /**
     * Sets main controller.
     *
     * @param mainController the main controller
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRequests();

        requestList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<RegistrationRequestDTO>() {
                    @Override
                    public void changed(ObservableValue<? extends RegistrationRequestDTO> obs,
                                        RegistrationRequestDTO old, RegistrationRequestDTO selected) {
                        showDetails(selected);
                    }
                });

        requestList.setCellFactory(new javafx.util.Callback<ListView<RegistrationRequestDTO>, javafx.scene.control.ListCell<RegistrationRequestDTO>>() {
            @Override
            public javafx.scene.control.ListCell<RegistrationRequestDTO> call(ListView<RegistrationRequestDTO> lv) {
                return new javafx.scene.control.ListCell<RegistrationRequestDTO>() {
                    @Override
                    protected void updateItem(RegistrationRequestDTO r, boolean empty) {
                        super.updateItem(r, empty);
                        setText(empty || r == null ? null : r.getFullName() + " (" + r.getRole() + ")");
                    }
                };
            }
        });
    }

    private void loadRequests() {
        List<RegistrationRequestDTO> pending = controller.getPendingRequestsAsDTO();
        requestList.setItems(FXCollections.observableArrayList(pending));
        clearDetails();
        clearMessage();
    }

    private void showDetails(RegistrationRequestDTO r) {
        if (r == null) {
            clearDetails();
            return;
        }
        detailName.setText(r.getFullName());
        detailEmail.setText(r.getEmail());
        detailRole.setText(r.getRole().toString());
        detailDate.setText(DATE_FMT.format(r.getSubmissionDate()));
        String docLabel = r.getRole().getDocumentLabel();
        if (docLabel != null && r.getIdentificationDocument() != null) {
            detailDoc.setText(docLabel.replace(": ", ": ") + r.getIdentificationDocument());
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
        RegistrationRequestDTO selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        controller.approveRequestByEmail(selected.getEmail());
        showSuccess("Request from " + selected.getFullName() + " ACCEPTED.");
        loadRequests();
    }

    @FXML
    private void handleReject() {
        RegistrationRequestDTO selected = requestList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String reason = reasonArea.getText() == null ? "" : reasonArea.getText().trim();
        if (reason.isBlank()) {
            showError("Rejection reason is mandatory.");
            return;
        }
        controller.rejectRequestByEmail(selected.getEmail(), reason);
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
