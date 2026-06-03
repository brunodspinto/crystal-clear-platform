package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in journalist. Gives access to the journalist
 * functionalities (consult assets and analyse income evolution) and to logout.
 */
public class JournalistMenuController {

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
    private void handleIncomeEvolution() {
        if (mainController != null) {
            mainController.showIncomeEvolution();
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
