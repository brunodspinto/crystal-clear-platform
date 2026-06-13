package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import pt.ipp.isep.dei.controller.NetworkDynamicsController;
import pt.ipp.isep.dei.domain.graph.Edge;
import pt.ipp.isep.dei.domain.graph.Entity;
import pt.ipp.isep.dei.domain.graph.NetworkSnapshot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for US32 - Network dynamics over time. The administrator
 * adds a list of snapshot dates, typed one by one or loaded from a CSV file;
 * for each date a snapshot of the active entities and relations is shown in
 * the results area.
 * Reuses the same {@link NetworkDynamicsController} as the console UI.
 */
public class NetworkDynamicsFXController implements Initializable {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    @FXML private TextField dateField;
    @FXML private ListView<String> datesList;
    @FXML private TextArea resultArea;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final NetworkDynamicsController controller = new NetworkDynamicsController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
        resultArea.setText("");
        if (!controller.hasData()) {
            showError("No entities loaded. Please load entities from CSV first (US19).");
        }
    }

    @FXML
    private void handleAddDate() {
        clearMessage();
        String date = dateField.getText() == null ? "" : dateField.getText().trim();
        if (date.isBlank()) {
            showError("Date cannot be blank.");
            return;
        }
        if (!date.matches(DATE_PATTERN)) {
            showError("Invalid format. Use yyyy-MM-dd.");
            return;
        }
        if (datesList.getItems().contains(date)) {
            showError("That date was already added.");
            return;
        }
        datesList.getItems().add(date);
        dateField.setText("");
    }

    @FXML
    private void handleRemoveDate() {
        clearMessage();
        String selected = datesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a date to remove.");
            return;
        }
        datesList.getItems().remove(selected);
    }

    @FXML
    private void handleLoadCsv() {
        clearMessage();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Snapshot Dates CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        File file = chooser.showOpenDialog(
                mainController != null ? mainController.getStage() : null);
        if (file == null) {
            return;
        }
        try {
            List<String> dates = controller.loadDatesFromCsv(file.getAbsolutePath());
            if (dates.isEmpty()) {
                showError("No valid dates were found in the file.");
                return;
            }
            int added = 0;
            for (String date : dates) {
                if (!datesList.getItems().contains(date)) {
                    datesList.getItems().add(date);
                    added++;
                }
            }
            showSuccess(added + " date(s) loaded from " + file.getName() + ".");
        } catch (IOException e) {
            showError("Failed to read the file: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerate() {
        clearMessage();
        resultArea.setText("");

        if (!controller.hasData()) {
            showError("No entities loaded. Please load entities from CSV first (US19).");
            return;
        }
        if (datesList.getItems().isEmpty()) {
            showError("Add at least one snapshot date.");
            return;
        }

        List<String> dates = new ArrayList<>(datesList.getItems());
        Collections.sort(dates);

        List<NetworkSnapshot> snapshots = controller.buildSnapshots(dates);
        resultArea.setText(snapshotsToText(snapshots));
        showSuccess("Generated " + snapshots.size() + " snapshot(s).");
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private String snapshotsToText(List<NetworkSnapshot> snapshots) {
        StringBuilder sb = new StringBuilder();
        sb.append("NETWORK DYNAMICS: ").append(snapshots.size()).append(" SNAPSHOT(S)\n");

        for (NetworkSnapshot snap : snapshots) {
            sb.append("\n--- Snapshot: ").append(snap.getDate()).append(" ---\n");
            sb.append("  Active entities : ").append(snap.getEntityCount()).append("\n");
            sb.append("  Active relations: ").append(snap.getEdgeCount()).append("\n");

            if (snap.getEntityCount() > 0) {
                sb.append("\n  Entities by type:\n");
                appendCountByType(sb, snap.getActiveEntities());
            }

            if (snap.getEdgeCount() > 0) {
                sb.append("\n  Relations by label:\n");
                appendCountByLabel(sb, snap.getActiveEdges());
            }

            if (snap.getEntityCount() == 0 && snap.getEdgeCount() == 0) {
                sb.append("  (no active entities or relations at this date)\n");
            }
        }
        return sb.toString();
    }

    private void appendCountByType(StringBuilder sb, List<Entity> entities) {
        List<String> seen = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Entity e : entities) {
            String type = e.getClass().getSimpleName();
            int idx = seen.indexOf(type);
            if (idx < 0) {
                seen.add(type);
                counts.add(1);
            } else {
                counts.set(idx, counts.get(idx) + 1);
            }
        }
        for (int i = 0; i < seen.size(); i++) {
            sb.append(String.format("    %-20s %d%n", seen.get(i), counts.get(i)));
        }
    }

    private void appendCountByLabel(StringBuilder sb, List<Edge> edges) {
        List<String> seen = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Edge e : edges) {
            int idx = seen.indexOf(e.getLabel());
            if (idx < 0) {
                seen.add(e.getLabel());
                counts.add(1);
            } else {
                counts.set(idx, counts.get(idx) + 1);
            }
        }
        for (int i = 0; i < seen.size(); i++) {
            sb.append(String.format("    %-25s %d%n", seen.get(i), counts.get(i)));
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
}
