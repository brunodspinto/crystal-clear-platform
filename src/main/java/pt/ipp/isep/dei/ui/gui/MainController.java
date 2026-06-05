package pt.ipp.isep.dei.ui.gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Mediator that owns the root BorderPane and is responsible for swapping
 * the center region whenever the user navigates between scenes. Each child
 * controller communicates with the rest of the GUI through this class so
 * scenes do not need to know about each other directly.
 */
public class MainController {

    @FXML
    private BorderPane root;

    private Stage stage;
    private HostServices hostServices;
    private StatsScreen pendingStatsScreen;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void showLogin() {
        loadCenter("/fxml/Login.fxml");
    }

    public void showRegister() {
        loadCenter("/fxml/Register.fxml");
    }

    public void showCitizenMenu() {
        loadCenter("/fxml/CitizenMenu.fxml");
    }

    public void showJournalistMenu() {
        loadCenter("/fxml/JournalistMenu.fxml");
    }

    public void showAdminMenu() {
        loadCenter("/fxml/AdminMenu.fxml");
    }

    public void showRegisterOrganization() {
        loadCenter("/fxml/RegisterOrganization.fxml");
    }

    public void showExportDeclarationCsv() {
        loadCenter("/fxml/ExportDeclarationCsv.fxml");
    }

    public void showIncomeEvolution() {
        loadCenter("/fxml/AnalyseIncomeEvolution.fxml");
    }

    public void showConsultAssets() {
        loadCenter("/fxml/ConsultAssets.fxml");
    }

    public void showSubmitComplaint() {
        loadCenter("/fxml/SubmitComplaint.fxml");
    }

    public void showPoliticalAgentMenu() {
        loadCenter("/fxml/PoliticalAgentMenu.fxml");
    }

    public void showEthicsCommitteeMenu() {
        loadCenter("/fxml/EthicsCommitteeMenu.fxml");
    }

    public void showStatistics(StatsScreen screen) {
        this.pendingStatsScreen = screen;
        loadCenter("/fxml/StatisticsView.fxml");
    }

    void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object child = loader.getController();
            if (child instanceof LoginController) {
                ((LoginController) child).setMainController(this);
            } else if (child instanceof RegisterSceneController) {
                ((RegisterSceneController) child).setMainController(this);
            } else if (child instanceof AnalyseIncomeEvolutionFXController) {
                ((AnalyseIncomeEvolutionFXController) child).setMainController(this);
            } else if (child instanceof CitizenMenuController) {
                ((CitizenMenuController) child).setMainController(this);
            } else if (child instanceof JournalistMenuController) {
                ((JournalistMenuController) child).setMainController(this);
            } else if (child instanceof SubmitComplaintFXController) {
                ((SubmitComplaintFXController) child).setMainController(this);
            } else if (child instanceof AdminMenuController) {
                ((AdminMenuController) child).setMainController(this);
            } else if (child instanceof PoliticalAgentMenuController) {
                ((PoliticalAgentMenuController) child).setMainController(this);
            } else if (child instanceof EthicsCommitteeMenuController) {
                ((EthicsCommitteeMenuController) child).setMainController(this);
            } else if (child instanceof RegisterOrganizationFXController) {
                ((RegisterOrganizationFXController) child).setMainController(this);
            } else if (child instanceof ExportDeclarationCsvFXController) {
                ((ExportDeclarationCsvFXController) child).setMainController(this);
            } else if (child instanceof StatisticsAnalysisFXController) {
                StatisticsAnalysisFXController stats = (StatisticsAnalysisFXController) child;
                stats.setMainController(this);
                if (pendingStatsScreen != null) {
                    stats.setScreen(pendingStatsScreen);
                    pendingStatsScreen = null;
                }
            }

            root.setCenter(view);
        } catch (Exception e) {
            // se a fxml ainda nao existir, regista no log pa nao crashar a app toda
            e.printStackTrace();
        }
    }
}
