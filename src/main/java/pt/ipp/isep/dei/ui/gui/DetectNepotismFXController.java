package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipp.isep.dei.controller.DetectNepotismController;
import pt.ipp.isep.dei.domain.graph.NepotismDetector.NepotismPair;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the direct nepotism detection (US22). Runs the same
 * {@link DetectNepotismController} as the console UI and shows the detected
 * appointed/appointer pairs in a table.
 */
public class DetectNepotismFXController implements Initializable {

    @FXML private TableView<PairRow> pairsTable;
    @FXML private TableColumn<PairRow, String> colAppointed;
    @FXML private TableColumn<PairRow, String> colAppointer;
    @FXML private TableColumn<PairRow, String> colTie;
    @FXML private Label resultLabel;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final DetectNepotismController controller = new DetectNepotismController();
    private final ObservableList<PairRow> rows = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colAppointed.setCellValueFactory(new PropertyValueFactory<>("appointed"));
        colAppointer.setCellValueFactory(new PropertyValueFactory<>("appointer"));
        colTie.setCellValueFactory(new PropertyValueFactory<>("tie"));
        pairsTable.setItems(rows);
        pairsTable.setPlaceholder(new Label("No detection has been run yet."));
        clearMessage();
        resultLabel.setText("");
    }

    @FXML
    private void handleDetect() {
        clearMessage();
        resultLabel.setText("");
        rows.clear();

        List<NepotismPair> pairs;
        try {
            pairs = controller.detect();
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            return;
        }

        if (pairs.isEmpty()) {
            resultLabel.setText("No direct nepotism cases detected.");
            return;
        }

        for (NepotismPair pair : pairs) {
            rows.add(new PairRow(pair));
        }
        resultLabel.setText(pairs.size() + " case(s) of direct nepotism detected.");
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showEthicsCommitteeMenu();
        }
    }

    private void showError(String message) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }

    /** Row model for the nepotism pairs table. */
    public static class PairRow {
        private final String appointed, appointer, tie;

        PairRow(NepotismPair pair) {
            this.appointed = pair.getAppointed();
            this.appointer = pair.getAppointer();
            this.tie = pair.getRelationshipLabel();
        }

        public String getAppointed() { return appointed; }
        public String getAppointer() { return appointer; }
        public String getTie()       { return tie; }
    }
}
