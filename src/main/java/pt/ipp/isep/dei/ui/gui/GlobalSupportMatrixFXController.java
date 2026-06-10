package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pt.ipp.isep.dei.controller.GlobalSupportMatrixController;
import pt.ipp.isep.dei.domain.graph.SupportGraph;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * GUI controller for the global support adjacency matrix (US33). The
 * administrator picks a snapshot date and the matrix of the support graph
 * (undirected, unweighted) at that date is displayed. Reuses the same
 * {@link GlobalSupportMatrixController} as the console UI.
 */
public class GlobalSupportMatrixFXController implements Initializable {

    private static final int CELL_WIDTH = 8;

    @FXML private DatePicker datePicker;
    @FXML private TextArea matrixArea;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final GlobalSupportMatrixController controller = new GlobalSupportMatrixController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
        matrixArea.setText("");
    }

    @FXML
    private void handleShow() {
        clearMessage();
        matrixArea.setText("");

        if (!controller.hasData()) {
            showError("No entities loaded. Please load entities from CSV first.");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            showError("Choose a snapshot date first.");
            return;
        }

        SupportGraph supportGraph;
        try {
            supportGraph = controller.buildFor(date.toString());
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        matrixArea.setText(matrixToText(supportGraph, date.toString()));
        showSuccess("Support graph built for " + date + " ("
                + supportGraph.size() + " active entities).");
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private String matrixToText(SupportGraph sg, String date) {
        int n = sg.size();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Support graph at ").append(date)
                .append(" (").append(n).append("x").append(n).append(") ===\n\n");

        if (n == 0) {
            sb.append("(no active entities at this date)\n");
            return sb.toString();
        }

        boolean[][] matrix = sg.getAdjacencyMatrix();

        sb.append(pad(""));
        for (int j = 0; j < n; j++) {
            sb.append(pad(sg.getRegistry().idAt(j)));
        }
        sb.append("\n");

        for (int i = 0; i < n; i++) {
            sb.append(pad(sg.getRegistry().idAt(i)));
            for (int j = 0; j < n; j++) {
                sb.append(pad(matrix[i][j] ? "1" : "0"));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String pad(String s) {
        if (s.length() >= CELL_WIDTH) {
            return s.substring(0, CELL_WIDTH - 1) + " ";
        }
        StringBuilder out = new StringBuilder(s);
        while (out.length() < CELL_WIDTH) {
            out.append(" ");
        }
        return out.toString();
    }

    private void showError(String message) {
        messageLabel.getStyleClass().setAll("message-error");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.getStyleClass().setAll("message-success");
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.getStyleClass().clear();
        messageLabel.setText("");
    }
}
