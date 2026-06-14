package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in political agent. Each button navigates to the
 * screen of a political agent functionality; those screens are built by
 * their owners.
 */
public class PoliticalAgentMenuController {

    private MainController mainController;
    private final AuthenticationController authController = new AuthenticationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleListOrganizations() {
        open("/fxml/ListOrganizations.fxml");
    }

    @FXML
    private void handleSubmitDeclaration() {
        open("/fxml/SubmitDeclaration.fxml");
    }

    @FXML
    private void handleNetworkDynamics() {
        open("/fxml/NetworkDynamics.fxml");
    }

    @FXML
    private void handleLogout() {
        authController.doLogout();
        if (mainController != null) {
            mainController.showLogin();
        }
    }

    private void open(String fxml) {
        if (mainController != null) {
            mainController.loadCenter(fxml);
        }
    }
}
