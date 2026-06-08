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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipp.isep.dei.controller.ConsultIntegratedSituationController;
import pt.ipp.isep.dei.domain.AssetEntry;
import pt.ipp.isep.dei.domain.BusinessParticipation;
import pt.ipp.isep.dei.domain.Declaration;
import pt.ipp.isep.dei.domain.Income;
import pt.ipp.isep.dei.domain.PoliticalAgent;
import pt.ipp.isep.dei.domain.PositionEntry;
import pt.ipp.isep.dei.domain.SubsidyEntry;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for US09 - Consult the integrated situation of a political agent
 * on a given reference date. The Ethics Committee member picks an agent and a date;
 * the validated declarations submitted on or before that date are listed and, when a
 * declaration is selected, its entries are shown in the details pane.
 */
public class IntegratedSituationFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgent> agentCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;

    @FXML private TableView<DeclarationRow> declarationsTable;
    @FXML private TableColumn<DeclarationRow, String> colType;
    @FXML private TableColumn<DeclarationRow, String> colDate;
    @FXML private TableColumn<DeclarationRow, String> colStatus;

    @FXML private TextArea detailsArea;

    private MainController mainController;
    private final ConsultIntegratedSituationController controller = new ConsultIntegratedSituationController();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentCombo.getItems().addAll(controller.getPoliticalAgents());

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        declarationsTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<DeclarationRow>() {
                    @Override
                    public void changed(ObservableValue<? extends DeclarationRow> obs,
                                        DeclarationRow old, DeclarationRow selected) {
                        showDetails(selected);
                    }
                });

        clearMessage();
    }

    @FXML
    private void handleSearch() {
        clearMessage();
        declarationsTable.getItems().clear();
        detailsArea.clear();

        PoliticalAgent agent = agentCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (agent == null || date == null) {
            showError("Select an agent and a reference date.");
            return;
        }

        Date referenceDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<Declaration> declarations;
        try {
            declarations = controller.getIntegratedSituation(agent, referenceDate);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        if (declarations.isEmpty()) {
            showError("No validated declarations submitted by " + agent.getName()
                    + " on or before " + date + ".");
            return;
        }

        List<DeclarationRow> rows = new ArrayList<>();
        for (Declaration d : declarations) {
            rows.add(new DeclarationRow(d));
        }
        declarationsTable.setItems(FXCollections.observableArrayList(rows));
        showSuccess(declarations.size() + " validated declaration(s) found. Select one to see the details.");
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showEthicsCommitteeMenu();
        }
    }

    private void showDetails(DeclarationRow row) {
        if (row == null) {
            detailsArea.clear();
            return;
        }
        Declaration d = row.getDeclaration();
        StringBuilder sb = new StringBuilder();
        sb.append(d.getDetails()).append("\n");
        appendPositions(sb, d.getPositionEntries());
        appendIncomes(sb, d.getIncomes());
        appendSubsidies(sb, d.getSubsidyEntries());
        appendAssets(sb, d.getAssetEntries());
        appendBusinessParticipations(sb, d.getBusinessParticipations());
        detailsArea.setText(sb.toString());
    }

    private void appendPositions(StringBuilder sb, List<PositionEntry> entries) {
        if (entries.isEmpty()) return;
        sb.append("Positions:\n");
        for (PositionEntry e : entries) {
            sb.append("  - ").append(e).append("\n");
        }
    }

    private void appendIncomes(StringBuilder sb, List<Income> entries) {
        if (entries.isEmpty()) return;
        sb.append("Incomes:\n");
        for (Income e : entries) {
            sb.append("  - ").append(e).append("\n");
        }
    }

    private void appendSubsidies(StringBuilder sb, List<SubsidyEntry> entries) {
        if (entries.isEmpty()) return;
        sb.append("Subsidies:\n");
        for (SubsidyEntry e : entries) {
            sb.append("  - ").append(e).append("\n");
        }
    }

    private void appendAssets(StringBuilder sb, List<AssetEntry> entries) {
        if (entries.isEmpty()) return;
        sb.append("Assets:\n");
        for (AssetEntry e : entries) {
            sb.append("  - ").append(e).append("\n");
        }
    }

    private void appendBusinessParticipations(StringBuilder sb, List<BusinessParticipation> entries) {
        if (entries.isEmpty()) return;
        sb.append("Business participations:\n");
        for (BusinessParticipation e : entries) {
            sb.append("  - ").append(e).append("\n");
        }
    }

    private void showError(String msg) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.getStyleClass().setAll("message-success");
        messageLabel.setText(msg);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }

    /** Row model for the declarations TableView. */
    public static class DeclarationRow {
        private final Declaration declaration;
        private final String type;
        private final String date;
        private final String status;

        public DeclarationRow(Declaration declaration) {
            this.declaration = declaration;
            this.type = declaration.getType().toString();
            this.date = DATE_FMT.format(declaration.getSubmissionDate());
            this.status = declaration.getStatus().toString();
        }

        public Declaration getDeclaration() { return declaration; }
        public String getType()   { return type; }
        public String getDate()   { return date; }
        public String getStatus() { return status; }
    }
}
