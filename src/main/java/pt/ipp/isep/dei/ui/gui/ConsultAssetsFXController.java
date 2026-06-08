package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipp.isep.dei.controller.ConsultAssetsController;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.AssetType;
import pt.ipp.isep.dei.domain.PoliticalAgent;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for US11 - Consult the assets of a political agent on a specific date.
 * Citizens see masked sensitive values; journalists see full details (AC4).
 */
public class ConsultAssetsFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgent> agentCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;
    @FXML private Label maskNotice;

    @FXML private TableView<AssetRow> assetsTable;
    @FXML private TableColumn<AssetRow, String> colType;
    @FXML private TableColumn<AssetRow, String> colValue;
    @FXML private TableColumn<AssetRow, String> colDetail;

    private MainController mainController;
    private final ConsultAssetsController controller = new ConsultAssetsController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentCombo.getItems().addAll(controller.getPoliticalAgents());

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));

        maskNotice.setVisible(false);
        clearMessage();
    }

    @FXML
    private void handleSearch() {
        clearMessage();
        assetsTable.getItems().clear();
        maskNotice.setVisible(false);

        PoliticalAgent agent = agentCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (agent == null || date == null) {
            showError("Select an agent and a reference date.");
            return;
        }

        Date referenceDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<AssetEntry> assets;
        try {
            assets = controller.getAssetsAt(agent, referenceDate);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        if (assets.isEmpty()) {
            showError("No validated declarations found for " + agent.getName() + " on or before " + date + ".");
            return;
        }

        boolean journalist = controller.isCurrentUserJournalist();
        if (!journalist) {
            maskNotice.setVisible(true);
        }

        List<AssetRow> rows = new ArrayList<>();
        for (AssetEntry a : assets) {
            rows.add(toRow(a, journalist));
        }
        assetsTable.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    private void handleBack() {
        if (mainController == null) return;
        if (controller.isCurrentUserJournalist()) {
            mainController.showJournalistMenu();
        } else {
            mainController.showCitizenMenu();
        }
    }

    private AssetRow toRow(AssetEntry a, boolean fullDetails) {
        String type = a.getAssetType().toString();
        String value = fullDetails ? String.format("%.2f €", a.getAssetValue()) : "***";
        String detail = "";
        if (a.getAssetType() == AssetType.REAL_ESTATE && a.getRealEstate() != null) {
            detail = a.getRealEstate().getDescription() + " (" + a.getRealEstate().getMunicipality() + ")";
        } else if (a.getAssetType() == AssetType.VEHICLES && a.getVehicleAsset() != null) {
            detail = fullDetails ? a.getVehicleAsset().toString() : "***";
        } else if (a.getAssetType() == AssetType.STOCKS && a.getStockAsset() != null) {
            detail = fullDetails ? a.getStockAsset().toString() : "***";
        }
        return new AssetRow(type, value, detail);
    }

    private void showError(String msg) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(msg);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }

    /** Row model for the assets TableView. */
    public static class AssetRow {
        private final String type;
        private final String value;
        private final String detail;

        public AssetRow(String type, String value, String detail) {
            this.type = type;
            this.value = value;
            this.detail = detail;
        }

        public String getType()   { return type; }
        public String getValue()  { return value; }
        public String getDetail() { return detail; }
    }
}
