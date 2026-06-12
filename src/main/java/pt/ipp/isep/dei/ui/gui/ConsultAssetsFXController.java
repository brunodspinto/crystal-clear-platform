package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import pt.ipp.isep.dei.dto.AssetEntryDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;

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

    @FXML private ComboBox<PoliticalAgentDTO> agentCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;
    @FXML private Label maskNotice;

    @FXML private TableView<AssetRow> assetsTable;
    @FXML private TableColumn<AssetRow, String> colAgent;
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

        colAgent.setCellValueFactory(new PropertyValueFactory<>("agent"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));

        // Picking a date filters the agent dropdown to the ones that actually
        // have validated declarations on or before it (same rule as the search)
        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> obs,
                                LocalDate old, LocalDate now) {
                refreshAgentOptions(now);
                refreshTable();
            }
        });

        // Selecting an agent filters the table on the spot too
        agentCombo.valueProperty().addListener(new ChangeListener<PoliticalAgentDTO>() {
            @Override
            public void changed(ObservableValue<? extends PoliticalAgentDTO> obs,
                                PoliticalAgentDTO old, PoliticalAgentDTO now) {
                refreshTable();
            }
        });

        maskNotice.setVisible(false);
        clearMessage();

        // The table starts with every validated asset; the filters above
        // make rows disappear as they are applied
        refreshTable();
    }

    /**
     * Rebuilds the table using the current filters. With no agent selected,
     * every agent's validated assets are shown (up to the chosen date, or
     * today when no date is set); selecting an agent or a date narrows the
     * rows down.
     */
    private void refreshTable() {
        assetsTable.getItems().clear();
        maskNotice.setVisible(false);

        LocalDate date = datePicker.getValue();
        Date referenceDate = date != null ? toDate(date) : new Date();

        PoliticalAgentDTO selected = agentCombo.getValue();
        List<PoliticalAgentDTO> agents;
        if (selected != null) {
            agents = new ArrayList<>();
            agents.add(selected);
        } else {
            agents = controller.getPoliticalAgents();
        }

        boolean journalist = controller.isCurrentUserJournalist();
        List<AssetRow> rows = new ArrayList<>();
        for (PoliticalAgentDTO agent : agents) {
            List<AssetEntryDTO> assets;
            try {
                assets = controller.getAssetsAt(agent, referenceDate);
            } catch (IllegalArgumentException ex) {
                continue;
            }
            for (AssetEntryDTO a : assets) {
                rows.add(toRow(agent.getName(), a, journalist));
            }
        }

        if (!rows.isEmpty() && !journalist) {
            maskNotice.setVisible(true);
        }
        assetsTable.setItems(FXCollections.observableArrayList(rows));
    }

    private void refreshAgentOptions(LocalDate date) {
        clearMessage();
        PoliticalAgentDTO selected = agentCombo.getValue();

        List<PoliticalAgentDTO> options;
        if (date == null) {
            options = controller.getPoliticalAgents();
        } else {
            options = controller.getPoliticalAgentsWithDataAt(toDate(date));
        }
        agentCombo.setItems(FXCollections.observableArrayList(options));

        // keep the previous selection if that agent is still available
        if (selected != null) {
            for (PoliticalAgentDTO a : options) {
                if (a.getEmail().equals(selected.getEmail())) {
                    agentCombo.setValue(a);
                    return;
                }
            }
        }
        agentCombo.setValue(null);
        if (date != null && options.isEmpty()) {
            showError("No agents have validated declarations on or before " + date + ".");
        }
    }

    @FXML
    private void handleSearch() {
        clearMessage();
        refreshTable();
        if (assetsTable.getItems().isEmpty()) {
            showError("No validated declarations match the current filters.");
        }
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

    private Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private AssetRow toRow(String agentName, AssetEntryDTO a, boolean fullDetails) {
        String value = fullDetails ? String.format("%.2f €", a.getValue()) : "***";
        String detail = a.getDetail();
        if (a.isSensitiveDetail() && !fullDetails) {
            detail = "***";
        }
        return new AssetRow(agentName, a.getType(), value, detail);
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
        private final String agent;
        private final String type;
        private final String value;
        private final String detail;

        public AssetRow(String agent, String type, String value, String detail) {
            this.agent = agent;
            this.type = type;
            this.value = value;
            this.detail = detail;
        }

        public String getAgent()  { return agent; }
        public String getType()   { return type; }
        public String getValue()  { return value; }
        public String getDetail() { return detail; }
    }
}
