package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import pt.ipp.isep.dei.controller.AuthenticationController;
import pt.ipp.isep.dei.controller.ReviewRegistrationController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Menu shown to a logged in administrator. Each button navigates to the
 * screen of an admin functionality; those screens are built by their owners.
 * Register Organization (US04) and Export Declaration CSV (US24) are provided
 * here.
 */
public class AdminMenuController implements Initializable {

    @FXML private Button reviewButton;

    private MainController mainController;
    private final AuthenticationController authController = new AuthenticationController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // badge with the number of registration requests waiting for review; a
        // badge failing must never stop the menu from opening
        try {
            int pending = new ReviewRegistrationController().getPendingRequestsAsDTO().size();
            if (pending > 0) {
                reviewButton.setText("Review Registration Requests (" + pending + " pending)");
            }
        } catch (RuntimeException e) {
            // keep the plain button label
        }
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
    private void handleBuildRelationsGraph() {
        open("/fxml/BuildRelationsGraph.fxml");
    }

    @FXML
    private void handleAdjacencyMatrices() {
        open("/fxml/GenerateAdjacencyMatrices.fxml");
    }

    @FXML
    private void handleGlobalSupportMatrix() {
        open("/fxml/GlobalSupportMatrix.fxml");
    }

    @FXML
    private void handleExportDeclarationGraph() {
        open("/fxml/ExportDeclarationGraph.fxml");
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
