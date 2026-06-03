package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in citizen. Gives access to the citizen
 * functionalities (consult assets and submit a complaint) and to logout.
 */
public class CitizenMenuController {

    private MainController mainController;
    private final AuthenticationController authController = new AuthenticationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleConsultAssets() {
        if (mainController != null) {
            mainController.showConsultAssets();
        }
    }

    @FXML
    private void handleSubmitComplaint() {
        if (mainController != null) {
            mainController.showSubmitComplaint();
        }
    }

    @FXML
    private void handleLogout() {
        authController.doLogout();
        if (mainController != null) {
            mainController.showLogin();
        }
    }
}
