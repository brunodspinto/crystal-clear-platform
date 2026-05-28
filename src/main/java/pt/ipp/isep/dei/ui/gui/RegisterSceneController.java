package pt.ipp.isep.dei.ui.gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import pt.ipp.isep.dei.controller.RegisterController;
import pt.ipp.isep.dei.domain.UserRole;

/**
 * JavaFX scene controller for US01 – Request registration on the platform.
 *
 * <p>Follows the same mediator pattern as {@link LoginController}: receives a
 * reference to {@link MainController} (injected by
 * {@link MainController#loadCenter(String)}) and calls
 * {@code mainController.showLogin()} to navigate back.</p>
 *
 * <p>Business logic is delegated entirely to the existing
 * {@link RegisterController}.</p>
 */
public class RegisterSceneController implements Initializable {

    // ── FXML bindings ──────────────────────────────────────────────────────────
    @FXML private TextField          fullNameField;
    @FXML private TextField          emailField;
    @FXML private PasswordField      passwordField;
    @FXML private PasswordField      confirmPasswordField;
    @FXML private ComboBox<UserRole> roleComboBox;
    @FXML private HBox               documentRow;
    @FXML private Label              documentLabel;
    @FXML private TextField          documentField;
    @FXML private Label              passwordHintLabel;
    @FXML private Label              messageLabel;
    @FXML private Button             submitButton;

    // ── Dependencies ───────────────────────────────────────────────────────────
    private final RegisterController controller = new RegisterController();
    private MainController mainController;

    /**
     * Injected by {@link MainController#loadCenter(String)} after the FXML loads.
     *
     * @param mainController the application mediator
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // ── Initializable ──────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<UserRole> roles = controller.getAvailableRoles();
        roleComboBox.setItems(FXCollections.observableArrayList(roles));
        roleComboBox.setPromptText("Select a role…");

        documentRow.setVisible(false);
        documentRow.setManaged(false);

        messageLabel.setText("");

        passwordField.textProperty().addListener((obs, oldVal, newVal) ->
                updatePasswordHint(newVal));

        roleComboBox.valueProperty().addListener((obs, oldRole, newRole) ->
                updateDocumentRow(newRole));
    }

    // ── Event handlers ─────────────────────────────────────────────────────────

    @FXML
    private void handleSubmit() {
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("message-error", "message-success");

        String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
        String email    = emailField.getText()    == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String confirm  = confirmPasswordField.getText() == null ? "" : confirmPasswordField.getText();
        UserRole role   = roleComboBox.getValue();
        String document = documentField.getText() == null ? "" : documentField.getText().trim();

        if (fullName.isEmpty()) {
            showError("Full name is required.");
            fullNameField.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            showError("Email address is required.");
            emailField.requestFocus();
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return;
        }
        if (!controller.isValidPassword(password)) {
            showError("Password must have exactly 7 alphanumeric characters, "
                    + "at least 3 uppercase letters and at least 2 digits.");
            passwordField.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match.");
            confirmPasswordField.requestFocus();
            return;
        }
        if (role == null) {
            showError("Please select a role.");
            roleComboBox.requestFocus();
            return;
        }
        if (controller.requiresDocument(role) && document.isEmpty()) {
            String docLabel = controller.getDocumentLabel(role).replace(": ", "");
            showError(docLabel + " is required for this role.");
            documentField.requestFocus();
            return;
        }

        try {
            boolean saved = controller.submitRequest(
                    fullName, email, password, role,
                    controller.requiresDocument(role) ? document : null);

            if (saved) {
                showSuccess("Registration request submitted. Awaiting administrator approval.");
                disableForm();
            } else {
                showError("A registration request for this email already exists.");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        if (mainController != null) {
            mainController.showLogin();
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private void updatePasswordHint(String password) {
        if (password.isEmpty()) {
            passwordHintLabel.setText("7 chars · ≥3 uppercase · ≥2 digits · alphanumeric only");
            passwordHintLabel.getStyleClass().removeAll("hint-ok", "hint-error");
            passwordHintLabel.getStyleClass().add("hint-neutral");
            return;
        }
        if (controller.isValidPassword(password)) {
            passwordHintLabel.setText("✓ Password is valid");
            passwordHintLabel.getStyleClass().removeAll("hint-neutral", "hint-error");
            passwordHintLabel.getStyleClass().add("hint-ok");
        } else {
            passwordHintLabel.setText("7 chars · ≥3 uppercase · ≥2 digits · alphanumeric only");
            passwordHintLabel.getStyleClass().removeAll("hint-neutral", "hint-ok");
            passwordHintLabel.getStyleClass().add("hint-error");
        }
    }

    private void updateDocumentRow(UserRole role) {
        if (role != null && controller.requiresDocument(role)) {
            documentLabel.setText(controller.getDocumentLabel(role));
            documentRow.setVisible(true);
            documentRow.setManaged(true);
        } else {
            documentRow.setVisible(false);
            documentRow.setManaged(false);
            documentField.clear();
        }
    }

    private void showError(String message) {
        messageLabel.getStyleClass().removeAll("message-success");
        messageLabel.getStyleClass().add("message-error");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.getStyleClass().removeAll("message-error");
        messageLabel.getStyleClass().add("message-success");
        messageLabel.setText(message);
    }

    private void disableForm() {
        submitButton.setDisable(true);
        fullNameField.setDisable(true);
        emailField.setDisable(true);
        passwordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        roleComboBox.setDisable(true);
        documentField.setDisable(true);
    }
}
