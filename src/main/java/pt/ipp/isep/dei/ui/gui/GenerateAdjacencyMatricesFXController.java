package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import pt.ipp.isep.dei.controller.GenerateAdjacencyMatricesController;
import pt.ipp.isep.dei.controller.LabeledAdjacencyMatrix;
import pt.ipp.isep.dei.domain.graph.AdjacencyMatrix;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the per-relation adjacency matrices of the relations
 * graph (US21). The administrator generates the matrices and picks which one
 * to display (each relation label plus the global one) from a combo box.
 * Reuses the same {@link GenerateAdjacencyMatricesController} as the console UI.
 */
public class GenerateAdjacencyMatricesFXController implements Initializable {

    private static final int MATRIX_CELL_CHAR_WIDTH = 10;
    private static final String GLOBAL_OPTION = "GLOBAL (sum of all relations)";

    @FXML private ComboBox<String> matrixCombo;
    @FXML private TextArea matrixArea;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final GenerateAdjacencyMatricesController controller = new GenerateAdjacencyMatricesController();

    private GenerateAdjacencyMatricesController.GenerationResult result;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clearMessage();
        matrixCombo.setDisable(true);
        matrixArea.setText("");
    }

    @FXML
    private void handleGenerate() {
        clearMessage();
        matrixCombo.getItems().clear();
        matrixCombo.setDisable(true);
        matrixArea.setText("");
        result = null;

        GenerateAdjacencyMatricesController.GenerationResult generated;
        try {
            generated = controller.generate();
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            return;
        }

        if (generated.getMatrices().isEmpty()) {
            showError("The relations graph has no edges. No matrices were generated.");
            return;
        }

        result = generated;
        for (LabeledAdjacencyMatrix entry : result.getMatrices()) {
            matrixCombo.getItems().add(entry.getLabel());
        }
        if (result.getGlobalMatrix() != null) {
            matrixCombo.getItems().add(GLOBAL_OPTION);
        }
        matrixCombo.setDisable(false);
        matrixCombo.getSelectionModel().selectFirst();
        handleMatrixSelected();

        int n = result.getNodeIds().size();
        showSuccess("Generated " + result.getMatrices().size()
                + " adjacency matrices (" + n + " x " + n + " each).");
    }

    @FXML
    private void handleMatrixSelected() {
        if (result == null) {
            return;
        }
        String selected = matrixCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        AdjacencyMatrix matrix = null;
        if (GLOBAL_OPTION.equals(selected)) {
            matrix = result.getGlobalMatrix();
        } else {
            for (LabeledAdjacencyMatrix entry : result.getMatrices()) {
                if (entry.getLabel().equals(selected)) {
                    matrix = entry.getMatrix();
                    break;
                }
            }
        }
        if (matrix != null) {
            matrixArea.setText(matrixToText(selected, matrix, result.getNodeIds()));
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showAdminMenu();
        }
    }

    private String matrixToText(String label, AdjacencyMatrix matrix, List<String> nodeIds) {
        int n = matrix.getSize();
        int nonZero = controller.countNonZeroEntries(matrix);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Matrix: ").append(label)
                .append(" (").append(n).append("x").append(n)
                .append(", ").append(nonZero).append(" non-zero) ===\n\n");

        sb.append(pad(""));
        for (int j = 0; j < n; j++) {
            sb.append(pad(nodeIds.get(j)));
        }
        sb.append("\n");

        for (int i = 0; i < n; i++) {
            sb.append(pad(nodeIds.get(i)));
            for (int j = 0; j < n; j++) {
                sb.append(pad(formatWeight(matrix.getWeight(i, j))));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String pad(String s) {
        if (s.length() >= MATRIX_CELL_CHAR_WIDTH) {
            return s.substring(0, MATRIX_CELL_CHAR_WIDTH - 1) + " ";
        }
        StringBuilder out = new StringBuilder(s);
        while (out.length() < MATRIX_CELL_CHAR_WIDTH) {
            out.append(" ");
        }
        return out.toString();
    }

    private String formatWeight(double w) {
        if (w == 0) {
            return "0";
        }
        if (w == (long) w) {
            return Long.toString((long) w);
        }
        return String.format("%.2f", w);
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
