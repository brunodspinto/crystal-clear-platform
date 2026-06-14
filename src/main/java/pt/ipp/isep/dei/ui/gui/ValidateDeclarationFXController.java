package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import pt.ipp.isep.dei.controller.ValidateDeclarationController;
import pt.ipp.isep.dei.domain.ValidationOutcome;
import pt.ipp.isep.dei.dto.DeclarationDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * JavaFX controller for US08 – Validate a submitted Declaration of Interests.
 *
 * <p>The scene has two states:</p>
 * <ul>
 *   <li><b>List state</b> – shows the table of pending declarations and a
 *       "Review" button. The detail/outcome panel is hidden.</li>
 *   <li><b>Review state</b> – shows the full declaration details, the outcome
 *       radio buttons, and the comments section (visible only when
 *       RETURNED_FOR_CORRECTION is selected).</li>
 * </ul>
 */
public class ValidateDeclarationFXController implements Initializable {

    // ── Pending declarations table ────────────────────────────────────────────
    @FXML private TableView<DeclarationRow>              pendingTable;
    @FXML private TableColumn<DeclarationRow, String>   colId;
    @FXML private TableColumn<DeclarationRow, String>   colAgent;
    @FXML private TableColumn<DeclarationRow, String>   colType;
    @FXML private TableColumn<DeclarationRow, String>   colDate;
    @FXML private Button                                 reviewButton;

    // ── Review panel (hidden until a declaration is selected) ─────────────────
    @FXML private VBox                  reviewPanel;
    @FXML private Label                 lblAgent;
    @FXML private Label                 lblStatus;
    @FXML private Label                 lblType;
    @FXML private Label                 lblDate;
    @FXML private Label                 lblPositions;
    @FXML private Label                 lblIncomes;
    @FXML private Label                 lblSubsidies;
    @FXML private Label                 lblAssets;
    @FXML private Label                 lblBusiness;
    @FXML private Label                 lblAttachments;
    @FXML private ToggleGroup           outcomeGroup;
    @FXML private RadioButton           rbValidated;
    @FXML private RadioButton           rbReturned;

    // ── Comments section (shown only when RETURNED_FOR_CORRECTION) ────────────
    @FXML private VBox                  commentsSection;
    @FXML private TableView<CommentRow> commentsTable;
    @FXML private TableColumn<CommentRow, String> colSection;
    @FXML private TableColumn<CommentRow, String> colComment;
    @FXML private TextField             sectionField;
    @FXML private TextField             commentField;

    // ── Footer ────────────────────────────────────────────────────────────────
    @FXML private Label                 messageLabel;
    @FXML private Button                confirmButton;

    // ── Dependencies ──────────────────────────────────────────────────────────
    private final ValidateDeclarationController controller = new ValidateDeclarationController();
    private MainController mainController;

    private DeclarationDTO selectedDeclaration;
    private final ObservableList<DeclarationRow> pendingRows  = FXCollections.observableArrayList();
    private final ObservableList<CommentRow>     commentRows  = FXCollections.observableArrayList();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // ── Initializable ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pending table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAgent.setCellValueFactory(new PropertyValueFactory<>("agent"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        pendingTable.setItems(pendingRows);
        pendingTable.setPlaceholder(new Label("No pending declarations found."));

        // Enable Review button only when a row is selected
        reviewButton.setDisable(true);
        pendingTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<DeclarationRow>() {
                    @Override
                    public void changed(ObservableValue<? extends DeclarationRow> obs,
                                        DeclarationRow old, DeclarationRow now) {
                        reviewButton.setDisable(now == null);
                    }
                });

        // Comments table columns
        colSection.setCellValueFactory(new PropertyValueFactory<>("section"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        commentsTable.setItems(commentRows);
        commentsTable.setPlaceholder(new Label("No comments added yet."));

        // Show/hide comments section based on outcome selection
        rbReturned.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs,
                                Boolean wasSelected, Boolean isSelected) {
                commentsSection.setVisible(isSelected);
                commentsSection.setManaged(isSelected);
            }
        });

        // Start in list state
        reviewPanel.setVisible(false);
        reviewPanel.setManaged(false);
        messageLabel.setText("");

        loadPendingDeclarations();
    }

    // ── List state ────────────────────────────────────────────────────────────

    private void loadPendingDeclarations() {
        pendingRows.clear();
        for (DeclarationDTO d : controller.getPendingDeclarations()) {
            pendingRows.add(new DeclarationRow(d));
        }
    }

    @FXML
    private void handleReview() {
        DeclarationRow row = pendingTable.getSelectionModel().getSelectedItem();
        if (row == null) return;

        selectedDeclaration = row.declaration;
        showDetails(selectedDeclaration);

        // Reset outcome and comments
        rbValidated.setSelected(true);
        commentRows.clear();
        sectionField.clear();
        commentField.clear();
        commentsSection.setVisible(false);
        commentsSection.setManaged(false);
        messageLabel.setText("");

        // Switch to review state
        reviewPanel.setVisible(true);
        reviewPanel.setManaged(true);
        pendingTable.setDisable(true);
        reviewButton.setDisable(true);
    }

    private void showDetails(DeclarationDTO d) {
        lblAgent.setText(d.getAgentName());
        lblStatus.setText(d.getStatus());
        lblType.setText(d.getType());
        lblDate.setText(new java.text.SimpleDateFormat("dd-MM-yyyy").format(d.getSubmissionDate()));
        lblPositions.setText(String.valueOf(d.getPositions().size()));
        lblIncomes.setText(String.valueOf(d.getIncomes().size()));
        lblSubsidies.setText(String.valueOf(d.getSubsidies().size()));
        lblAssets.setText(String.valueOf(d.getAssets().size()));
        lblBusiness.setText(String.valueOf(d.getBusinessParticipations().size()));
        lblAttachments.setText(String.valueOf(d.getAttachmentsCount()));
    }

    // ── Review state ──────────────────────────────────────────────────────────

    @FXML
    private void handleAddComment() {
        String section = sectionField.getText() == null ? "" : sectionField.getText().trim();
        String comment = commentField.getText() == null ? "" : commentField.getText().trim();

        if (section.isEmpty()) {
            showError("Section name is required.");
            sectionField.requestFocus();
            return;
        }
        if (comment.isEmpty()) {
            showError("Comment text is required.");
            commentField.requestFocus();
            return;
        }

        commentRows.add(new CommentRow(section, comment));
        sectionField.clear();
        commentField.clear();
        clearMessage();
    }

    @FXML
    private void handleRemoveComment() {
        CommentRow sel = commentsTable.getSelectionModel().getSelectedItem();
        if (sel != null) commentRows.remove(sel);
    }

    @FXML
    private void handleConfirm() {
        if (selectedDeclaration == null) return;

        ValidationOutcome outcome = rbValidated.isSelected()
                ? ValidationOutcome.VALIDATED
                : ValidationOutcome.RETURNED_FOR_CORRECTION;

        if (outcome == ValidationOutcome.RETURNED_FOR_CORRECTION && commentRows.isEmpty()) {
            showError("At least one comment is required when returning for correction (AC2).");
            return;
        }

        // the decision is irreversible, so ask for confirmation (same as the console UI)
        String question = outcome == ValidationOutcome.VALIDATED
                ? "Confirm the validation of declaration " + selectedDeclaration.getId() + "?"
                : "Confirm returning declaration " + selectedDeclaration.getId() + " for correction?";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        ButtonType choice = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (choice != ButtonType.OK) {
            return;
        }

        List<Object[]> comments = new ArrayList<>();
        for (CommentRow r : commentRows) {
            comments.add(new Object[]{r.sectionText, r.commentText});
        }

        boolean saved = controller.processValidation(selectedDeclaration.getId(), outcome, comments);

        if (saved) {
            String msg = outcome == ValidationOutcome.VALIDATED
                    ? "Declaration validated successfully."
                    : "Declaration returned for correction with " + commentRows.size() + " comment(s).";
            showSuccess(msg);
            confirmButton.setDisable(true);

            // Refresh pending list after short delay and return to list state
            loadPendingDeclarations();
            reviewPanel.setVisible(false);
            reviewPanel.setManaged(false);
            pendingTable.setDisable(false);
            selectedDeclaration = null;
        } else {
            showError("Validation failed. The declaration may no longer be PENDING, " +
                      "or you are not registered as an Ethics Committee Member.");
        }
    }

    @FXML
    private void handleCancelReview() {
        reviewPanel.setVisible(false);
        reviewPanel.setManaged(false);
        pendingTable.setDisable(false);
        pendingTable.getSelectionModel().clearSelection();
        selectedDeclaration = null;
        clearMessage();
    }

    @FXML
    private void handleBack() {
        if (mainController != null) mainController.showEthicsCommitteeMenu();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void showError(String msg) {
        messageLabel.getStyleClass().removeAll("message-success");
        messageLabel.getStyleClass().add("message-error");
        messageLabel.setText(msg);
    }

    private void showSuccess(String msg) {
        messageLabel.getStyleClass().removeAll("message-error");
        messageLabel.getStyleClass().add("message-success");
        messageLabel.setText(msg);
    }

    private void clearMessage() {
        messageLabel.setText("");
        messageLabel.getStyleClass().removeAll("message-error", "message-success");
    }

    // =========================================================================
    // Row model classes
    // =========================================================================

    /** Row model for the pending declarations table. */
    public static class DeclarationRow {
        private final String id, agent, type, date;
        final DeclarationDTO declaration;

        DeclarationRow(DeclarationDTO d) {
            this.declaration = d;
            this.id    = d.getId();
            this.agent = d.getAgentName();
            this.type  = d.getType();
            this.date  = new java.text.SimpleDateFormat("dd-MM-yyyy").format(d.getSubmissionDate());
        }

        public String getId()    { return id; }
        public String getAgent() { return agent; }
        public String getType()  { return type; }
        public String getDate()  { return date; }
    }

    /** Row model for the comments table. */
    public static class CommentRow {
        private final String section, comment;
        final String sectionText, commentText;

        CommentRow(String section, String comment) {
            this.sectionText = section;
            this.commentText = comment;
            this.section = section;
            this.comment = comment;
        }

        public String getSection() { return section; }
        public String getComment() { return comment; }
    }
}
