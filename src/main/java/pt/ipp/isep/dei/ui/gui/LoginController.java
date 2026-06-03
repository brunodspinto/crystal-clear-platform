package pt.ipp.isep.dei.ui.gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.repository.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.isep.lei.esoft.auth.UserSession;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

/**
 * Login controller for the citizen / journalist GUI. Validates the password
 * against the non-functional requirement (seven alphanumeric characters,
 * including three capital letters and two digits) and uses the existing
 * AuthenticationController to log the user in.
 */
public class LoginController implements Initializable {

    private static final int PASSWORD_LENGTH = 7;
    private static final int MIN_UPPER = 3;
    private static final int MIN_DIGITS = 2;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private MainController mainController;
    private AuthenticationController authController;
    private AuthenticationRepository authRepository;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authController = new AuthenticationController();
        authRepository = Repositories.getInstance().getAuthenticationRepository();
        messageLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email and password are required.");
            return;
        }
        if (!isPasswordValid(password)) {
            messageLabel.setText("Password must have " + PASSWORD_LENGTH
                    + " alphanumeric characters, " + MIN_UPPER + " uppercase and "
                    + MIN_DIGITS + " digits.");
            return;
        }

        boolean ok = authController.doLogin(email, password);
        if (!ok) {
            messageLabel.setText("Invalid credentials.");
            return;
        }

        dispatchByRole();
    }

    @FXML
    private void handleClear() {
        emailField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }

    @FXML
    private void handleRegister() {
        if (mainController != null) {
            mainController.showRegister();
        }
    }

    private boolean isPasswordValid(String password) {
        if (password.length() != PASSWORD_LENGTH) {
            return false;
        }
        int upper = 0;
        int digits = 0;
        for (int i = 0; i < password.length(); i = i + 1) {
            char c = password.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
            if (Character.isUpperCase(c)) {
                upper = upper + 1;
            }
            if (Character.isDigit(c)) {
                digits = digits + 1;
            }
        }
        return upper >= MIN_UPPER && digits >= MIN_DIGITS;
    }

    private void dispatchByRole() {
        if (mainController == null) {
            messageLabel.setText("GUI not initialised properly.");
            return;
        }
        if (sessionHasRole(AuthenticationController.ROLE_JOURNALIST)) {
            mainController.showJournalistMenu();
            return;
        }
        if (sessionHasRole(AuthenticationController.ROLE_CITIZEN)) {
            mainController.showCitizenMenu();
            return;
        }
        messageLabel.setText("This GUI is for citizens and journalists.");
    }

    private boolean sessionHasRole(String roleDescription) {
        if (authRepository == null) {
            return false;
        }
        UserSession session = authRepository.getCurrentUserSession();
        if (session == null || !session.isLoggedIn()) {
            return false;
        }
        List<UserRoleDTO> roles = session.getUserRoles();
        if (roles == null) {
            return false;
        }
        for (UserRoleDTO role : roles) {
            if (roleDescription.equals(role.getDescription())) {
                return true;
            }
        }
        return false;
    }
}
