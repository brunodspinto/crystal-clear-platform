package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;

import pt.ipp.isep.dei.controller.AuthenticationController;

/**
 * Menu shown to a logged in member of the Ethics Committee. The first group of
 * buttons opens the network and validation functionalities (those screens are
 * built by their owners); the second group runs the statistical analyses
 * (US14, US17, US18, US28, US30, US31), each of which runs its Python script
 * and opens the resulting graph(s).
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
    private void handleUs14() {
        showStatistics(StatsScreen.US14);
    }

    @FXML
    private void handleUs17() {
        showStatistics(StatsScreen.US17);
    }

    @FXML
    private void handleUs18() {
        showStatistics(StatsScreen.US18);
    }

    @FXML
    private void handleUs28() {
        showStatistics(StatsScreen.US28);
    }

    @FXML
    private void handleUs30() {
        showStatistics(StatsScreen.US30);
    }

    @FXML
    private void handleUs31() {
        showStatistics(StatsScreen.US31);
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

    private void showStatistics(StatsScreen screen) {
        if (mainController != null) {
            mainController.showStatistics(screen);
        }
    }
}
