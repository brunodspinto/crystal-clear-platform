package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in administrator. Each button navigates to the
 * screen of an admin functionality; those screens are built by their owners.
 */
public class AdminMenuController {

    private MainController mainController;
    private final AuthenticationController authController = new AuthenticationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleReviewRegistration() {
        open("/fxml/ReviewRegistration.fxml");
    }

    @FXML
    private void handleRegisterOrganization() {
        open("/fxml/RegisterOrganization.fxml");
    }

    @FXML
    private void handleLoadEntities() {
        open("/fxml/LoadEntities.fxml");
    }

    @FXML
    private void handleExportDeclarationCsv() {
        open("/fxml/ExportDeclarationCsv.fxml");
    }

    @FXML
    private void handleExportHoldings() {
        open("/fxml/ExportHoldings.fxml");
    }

    @FXML
    private void handleExportGraph() {
        open("/fxml/ExportGraphSvg.fxml");
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
