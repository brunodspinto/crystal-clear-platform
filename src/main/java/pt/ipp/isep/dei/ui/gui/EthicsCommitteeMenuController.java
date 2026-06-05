package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in member of the Ethics Committee. Each button
 * navigates to the screen of an Ethics Committee functionality; those screens
 * are built by their owners.
 */
public class EthicsCommitteeMenuController {

    private MainController mainController;
    private final AuthenticationController authController = new AuthenticationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleValidateDeclaration() {
        open("/fxml/ValidateDeclaration.fxml");
    }

    @FXML
    private void handleIntegratedSituation() {
        open("/fxml/IntegratedSituation.fxml");
    }

    @FXML
    private void handleAssessComplaint() {
        open("/fxml/AssessComplaint.fxml");
    }

    @FXML
    private void handleDetectNepotism() {
        open("/fxml/DetectNepotism.fxml");
    }

    @FXML
    private void handleDetectConflicts() {
        open("/fxml/DetectConflicts.fxml");
    }

    @FXML
    private void handleFindPath() {
        open("/fxml/FindPath.fxml");
    }

    @FXML
    private void handleSubnetwork() {
        open("/fxml/Subnetwork.fxml");
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
