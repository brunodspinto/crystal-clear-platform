package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pt.ipp.isep.dei.controller.DetectConflictsController;
import pt.ipp.isep.dei.domain.graph.ConflictDetector.Chain;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * GUI controller for the indirect nepotism / conflict of interest queries
 * (US23). The user picks one of the predefined questions and the detected
 * chains are listed, first and last entity included. Reuses the same
 * {@link DetectConflictsController} as the console UI.
 */
public class DetectConflictsFXController implements Initializable {

    @FXML private ComboBox<String> questionCombo;
    @FXML private TextField orgFilterField;
    @FXML private ListView<String> chainsList;
    @FXML private Label resultLabel;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final DetectConflictsController controller = new DetectConflictsController();
    private final ObservableList<String> chainItems = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionCombo.getItems().addAll(controller.getAvailableQuestions());
        questionCombo.setPromptText("Select a question…");
        chainsList.setItems(chainItems);
        chainsList.setPlaceholder(new Label("No query has been run yet."));
        clearMessage();
        resultLabel.setText("");

        // The organisation filter only applies to question 2
        orgFilterField.setDisable(true);
        questionCombo.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> obs,
                                        Number old, Number now) {
                        boolean isOrgQuery = now != null && now.intValue()
                                == DetectConflictsController.QUERY_RELATIVES_IN_ORGANISATION;
                        orgFilterField.setDisable(!isOrgQuery);
                        if (!isOrgQuery) {
                            orgFilterField.clear();
                        }
                    }
                });
    }

    @FXML
    private void handleRunQuery() {
        clearMessage();
        resultLabel.setText("");
        chainItems.clear();

        int queryIndex = questionCombo.getSelectionModel().getSelectedIndex();
        if (queryIndex < 0) {
            showError("Select a question first.");
            return;
        }

        String organisationId = orgFilterField.getText() == null
                ? null : orgFilterField.getText().trim();

        List<Chain> chains;
        try {
            chains = controller.runQuery(queryIndex, organisationId);
        } catch (IllegalStateException e) {
            showError(e.getMessage());
            return;
        }

        if (chains.isEmpty()) {
            resultLabel.setText("No situations detected for this query.");
            return;
        }

        for (Chain chain : chains) {
            String line = chain.toString();
            if (chain.hasContext()) {
                line = line + "   (" + chain.getContext() + ")";
            }
            chainItems.add(line);
        }
        resultLabel.setText(chains.size() + " chain(s) detected.");
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
}
