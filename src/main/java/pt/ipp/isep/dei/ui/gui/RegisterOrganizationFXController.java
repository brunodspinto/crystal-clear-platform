package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pt.ipp.isep.dei.controller.RegisterOrganizationController;
import pt.ipp.isep.dei.domain.OrganizationNature;
import pt.ipp.isep.dei.domain.OrganizationType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * GUI controller for registering an organization (US04). The administrator
 * types the organization name and picks its type and legal nature from
 * predefined lists (no free text for the nature, as required by US04). The
 * screen reuses the same {@link RegisterOrganizationController} as the console UI.
 */
public class RegisterOrganizationFXController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<OrganizationType> typeCombo;
    @FXML private ComboBox<OrganizationNature> natureCombo;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final RegisterOrganizationController controller = new RegisterOrganizationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeCombo.setItems(FXCollections.observableArrayList(controller.getOrganizationTypes()));
        natureCombo.setItems(FXCollections.observableArrayList(controller.getOrganizationNatures()));
        clearMessage();
    }

    @FXML
    private void handleRegister() {
        clearMessage();

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        OrganizationType type = typeCombo.getValue();
        OrganizationNature nature = natureCombo.getValue();

        if (name.isBlank() || type == null || nature == null) {
            showError("Enter a name and select a type and a nature.");
            return;
        }

        boolean registered;
        try {
            registered = controller.registerOrganization(name, nature.toString(), type);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        if (registered) {
            showSuccess("Organization \"" + name + "\" registered successfully.");
            clearForm();
        } else {
            showError("An organization with that name and type already exists.");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        clearMessage();
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private void clearForm() {
        nameField.clear();
        typeCombo.getSelectionModel().clearSelection();
        natureCombo.getSelectionModel().clearSelection();
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
