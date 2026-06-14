package pt.ipp.isep.dei.ui.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.ipp.isep.dei.controller.SubmitDeclarationController;
import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.OrganizationDTO;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * JavaFX controller for US06 – Submit a Declaration of Interests.
 * Implements AC1 (household), AC2 (import from previous), AC3 (type rules).
 */
public class SubmitDeclarationFXController implements Initializable {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");

    // ── Header ────────────────────────────────────────────────────────────────
    @FXML private ComboBox<DeclarationType> typeCombo;
    @FXML private ComboBox<DeclarationDTO>  importCombo;      // AC2
    @FXML private Button                    importButton;     // AC2
    @FXML private VBox                      exceptionalBox;   // AC3 — shown only for EXCEPTIONAL
    @FXML private TextField                 amendedIdField;   // AC3
    @FXML private TextField                 amendReasonField; // AC3
    @FXML private Label                     messageLabel;
    @FXML private Button                    submitButton;

    // ── AC1 Household section ─────────────────────────────────────────────────
    @FXML private TextField                     hhNameField;
    @FXML private ComboBox<HouseholdRelation>   hhRelationCombo;
    @FXML private TableView<HouseholdRow>       hhTable;
    @FXML private TableColumn<HouseholdRow, String> hhColName;
    @FXML private TableColumn<HouseholdRow, String> hhColRelation;

    // ── Position entries section ──────────────────────────────────────────────
    @FXML private ComboBox<OrganizationDTO> posOrgCombo;
    @FXML private TextField                posFunctionField;
    @FXML private ComboBox<PositionNature> posNatureCombo;
    @FXML private TextField                posGrossField;
    @FXML private TextField                posConsultingField;
    @FXML private TextField                posBoardField;
    @FXML private DatePicker               posStartField;
    @FXML private DatePicker               posEndField;
    @FXML private TableView<PositionRow>   posTable;
    @FXML private TableColumn<PositionRow, String> posColOrg;
    @FXML private TableColumn<PositionRow, String> posColFunc;
    @FXML private TableColumn<PositionRow, String> posColNature;
    @FXML private TableColumn<PositionRow, String> posColGross;
    @FXML private TableColumn<PositionRow, String> posColStart;

    // ── Subsidy entries section ───────────────────────────────────────────────
    @FXML private ComboBox<OrganizationDTO> subOrgCombo;
    @FXML private TextField              subAmountField;
    @FXML private TextField              subDescField;
    @FXML private DatePicker             subDateField;
    @FXML private TableView<SubsidyRow>  subTable;
    @FXML private TableColumn<SubsidyRow, String> subColOrg;
    @FXML private TableColumn<SubsidyRow, String> subColAmount;
    @FXML private TableColumn<SubsidyRow, String> subColDesc;

    // ── Asset entries section ─────────────────────────────────────────────────
    @FXML private ComboBox<AssetType>  assetTypeCombo;
    @FXML private TextField            assetValueField;
    @FXML private TextField            assetDetailField;
    @FXML private TextField            assetMunicipalityField;
    @FXML private TableView<AssetRow>  assetTable;
    @FXML private TableColumn<AssetRow, String> assetColType;
    @FXML private TableColumn<AssetRow, String> assetColValue;
    @FXML private TableColumn<AssetRow, String> assetColDetail;

    // ── Business participations section ───────────────────────────────────────
    @FXML private ComboBox<OrganizationDTO> bizOrgCombo;
    @FXML private TextField              bizNifField;
    @FXML private TextField              bizValueField;
    @FXML private TextField              bizPctField;
    @FXML private TableView<BizRow>      bizTable;
    @FXML private TableColumn<BizRow, String> bizColOrg;
    @FXML private TableColumn<BizRow, String> bizColNif;
    @FXML private TableColumn<BizRow, String> bizColValue;
    @FXML private TableColumn<BizRow, String> bizColPct;

    // ── Dependencies ──────────────────────────────────────────────────────────
    private final SubmitDeclarationController controller = new SubmitDeclarationController();
    private MainController mainController;

    // ── In-memory lists ───────────────────────────────────────────────────────
    private final ObservableList<HouseholdRow> hhRows       = FXCollections.observableArrayList();
    private final ObservableList<PositionRow>  positionRows = FXCollections.observableArrayList();
    private final ObservableList<SubsidyRow>   subsidyRows  = FXCollections.observableArrayList();
    private final ObservableList<AssetRow>     assetRows    = FXCollections.observableArrayList();
    private final ObservableList<BizRow>       bizRows      = FXCollections.observableArrayList();

    public void setMainController(MainController mc) { this.mainController = mc; }

    // ── Initializable ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Declaration type combo
        typeCombo.setItems(FXCollections.observableArrayList(controller.getDeclarationTypes()));
        typeCombo.setPromptText("Select type…");

        // Show/hide exceptional fields based on type (AC3)
        exceptionalBox.setVisible(false);
        exceptionalBox.setManaged(false);
        typeCombo.valueProperty().addListener(new ChangeListener<DeclarationType>() {
            @Override
            public void changed(ObservableValue<? extends DeclarationType> obs,
                                DeclarationType old, DeclarationType now) {
                boolean isEx = now == DeclarationType.EXCEPTIONAL;
                exceptionalBox.setVisible(isEx);
                exceptionalBox.setManaged(isEx);
            }
        });

        // AC2 – import combo
        List<DeclarationDTO> previous = controller.getPreviousDeclarations();
        importCombo.setItems(FXCollections.observableArrayList(previous));
        importCombo.setPromptText(previous.isEmpty() ? "No previous declarations" : "Select to import…");
        importButton.setDisable(previous.isEmpty());

        // Organisation combos
        ObservableList<OrganizationDTO> orgs =
                FXCollections.observableArrayList(controller.getOrganizations());
        setupOrgCombo(posOrgCombo, orgs);
        setupOrgCombo(subOrgCombo, orgs);
        setupOrgCombo(bizOrgCombo, orgs);

        // AC1 – household relation combo
        hhRelationCombo.setItems(FXCollections.observableArrayList(controller.getHouseholdRelations()));
        hhRelationCombo.setPromptText("Relation…");

        // Position natures
        posNatureCombo.setItems(FXCollections.observableArrayList(controller.getPositionNatures()));
        posNatureCombo.setPromptText("Nature…");

        // Asset types
        assetTypeCombo.setItems(FXCollections.observableArrayList(controller.getAssetTypes()));
        assetTypeCombo.setPromptText("Type…");

        // Table bindings
        hhColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        hhColRelation.setCellValueFactory(new PropertyValueFactory<>("relation"));
        hhTable.setItems(hhRows);

        posColOrg.setCellValueFactory(new PropertyValueFactory<>("org"));
        posColFunc.setCellValueFactory(new PropertyValueFactory<>("function"));
        posColNature.setCellValueFactory(new PropertyValueFactory<>("nature"));
        posColGross.setCellValueFactory(new PropertyValueFactory<>("grossSalary"));
        posColStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        posTable.setItems(positionRows);

        subColOrg.setCellValueFactory(new PropertyValueFactory<>("org"));
        subColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        subColDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        subTable.setItems(subsidyRows);

        assetColType.setCellValueFactory(new PropertyValueFactory<>("type"));
        assetColValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        assetColDetail.setCellValueFactory(new PropertyValueFactory<>("detail"));
        assetTable.setItems(assetRows);

        bizColOrg.setCellValueFactory(new PropertyValueFactory<>("org"));
        bizColNif.setCellValueFactory(new PropertyValueFactory<>("nif"));
        bizColValue.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        bizColPct.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        bizTable.setItems(bizRows);

        messageLabel.setText("");
    }

    // ── AC2 – Import ──────────────────────────────────────────────────────────

    @FXML
    private void handleImport() {
        DeclarationDTO sel = importCombo.getValue();
        if (sel == null) return;

        SubmitDeclarationController.ImportedData imported =
                controller.importFromDeclaration(sel.getId());
        if (imported == null) {
            showError("Could not import the selected declaration.");
            return;
        }

        // Pre-populate household (AC1)
        hhRows.clear();
        for (Object[] m : imported.getHouseholdMembers()) {
            hhRows.add(new HouseholdRow((String) m[0], (HouseholdRelation) m[1]));
        }
        // Pre-populate positions
        positionRows.clear();
        for (Object[] pe : imported.getPositionEntries()) {
            positionRows.add(new PositionRow((String) pe[0], (String) pe[1],
                    (PositionNature) pe[2], (double) pe[3], (double) pe[4],
                    (double) pe[5], (Date) pe[6], (Date) pe[7]));
        }
        // Pre-populate subsidies
        subsidyRows.clear();
        for (Object[] se : imported.getSubsidyEntries()) {
            subsidyRows.add(new SubsidyRow((String) se[0], (double) se[1],
                    (String) se[2], (Date) se[3]));
        }
        // Pre-populate assets
        assetRows.clear();
        for (Object[] ae : imported.getAssetEntries()) {
            String muni = ae.length > 3 ? (String) ae[3] : "";
            assetRows.add(new AssetRow((AssetType) ae[0], (double) ae[1], (String) ae[2], muni));
        }
        // Pre-populate business participations
        bizRows.clear();
        for (Object[] bp : imported.getBusinessParticipations()) {
            bizRows.add(new BizRow((String) bp[0], (long) bp[1],
                    (double) bp[2], (double) bp[3]));
        }

        showSuccess("Data imported from declaration " + sel.getId() + ". Review and adjust before submitting.");
    }

    // ── AC1 – Household ───────────────────────────────────────────────────────

    @FXML
    private void handleAddHousehold() {
        String name = hhNameField.getText() == null ? "" : hhNameField.getText().trim();
        HouseholdRelation rel = hhRelationCombo.getValue();
        if (name.isEmpty() || rel == null) {
            showError("Household: provide a name and select a relation.");
            return;
        }
        hhRows.add(new HouseholdRow(name, rel));
        hhNameField.clear();
        hhRelationCombo.setValue(null);
        clearMessage();
    }

    @FXML
    private void handleRemoveHousehold() {
        HouseholdRow sel = hhTable.getSelectionModel().getSelectedItem();
        if (sel != null) hhRows.remove(sel);
    }

    // ── Position section ──────────────────────────────────────────────────────

    @FXML
    private void handleAddPosition() {
        OrganizationDTO org  = posOrgCombo.getValue();
        String func          = readPosFunction();
        PositionNature nature = posNatureCombo.getValue();
        Double gross         = parseDouble(posGrossField);
        Double consulting    = parseDouble(posConsultingField);
        Double board         = parseDouble(posBoardField);
        Date start           = fromPicker(posStartField);

        if (org == null || func == null || nature == null
                || gross == null || consulting == null || board == null || start == null) {
            showError("Position: fill Organisation, Function, Nature, Gross Salary, " +
                      "Consulting Income, Board Income and Start Date.");
            return;
        }
        positionRows.add(new PositionRow(org.getName(), func, nature, gross, consulting, board,
                start, fromPicker(posEndField)));
        clearPositionForm();
        clearMessage();
    }

    @FXML
    private void handleRemovePosition() {
        PositionRow sel = posTable.getSelectionModel().getSelectedItem();
        if (sel != null) positionRows.remove(sel);
    }

    // ── Subsidy section ───────────────────────────────────────────────────────

    @FXML
    private void handleAddSubsidy() {
        OrganizationDTO org = subOrgCombo.getValue();
        Double amount    = parseDouble(subAmountField);
        String desc      = subDescField.getText() == null ? "" : subDescField.getText().trim();
        Date date        = fromPicker(subDateField);
        if (org == null || amount == null || desc.isEmpty() || date == null) {
            showError("Subsidy: fill Organisation, Amount, Description and Date.");
            return;
        }
        subsidyRows.add(new SubsidyRow(org.getName(), amount, desc, date));
        clearSubsidyForm();
        clearMessage();
    }

    @FXML
    private void handleRemoveSubsidy() {
        SubsidyRow sel = subTable.getSelectionModel().getSelectedItem();
        if (sel != null) subsidyRows.remove(sel);
    }

    // ── Asset section ─────────────────────────────────────────────────────────

    @FXML
    private void handleAddAsset() {
        AssetType type  = assetTypeCombo.getValue();
        Double value    = parseDouble(assetValueField);
        String detail   = assetDetailField.getText() == null ? "" : assetDetailField.getText().trim();
        String municipality = assetMunicipalityField.getText() == null
                ? "" : assetMunicipalityField.getText().trim();
        if (type == null || value == null || detail.isEmpty()) {
            showError("Asset: fill Type, Value and Detail.");
            return;
        }
        // real estate also needs a municipality (the domain requires it)
        if (type == AssetType.REAL_ESTATE && municipality.isEmpty()) {
            showError("Real estate: fill the Municipality field too.");
            return;
        }
        assetRows.add(new AssetRow(type, value, detail, municipality));
        clearAssetForm();
        clearMessage();
    }

    @FXML
    private void handleRemoveAsset() {
        AssetRow sel = assetTable.getSelectionModel().getSelectedItem();
        if (sel != null) assetRows.remove(sel);
    }

    // ── Business section ──────────────────────────────────────────────────────

    @FXML
    private void handleAddBusiness() {
        OrganizationDTO org = bizOrgCombo.getValue();
        Long nif         = parseLong(bizNifField);
        Double val       = parseDouble(bizValueField);
        Double pct       = parseDouble(bizPctField);
        if (org == null || nif == null || val == null || pct == null) {
            showError("Business Participation: fill all fields.");
            return;
        }
        if (pct < 0 || pct > 100) {
            showError("Business Participation: percentage must be between 0 and 100.");
            return;
        }
        bizRows.add(new BizRow(org.getName(), nif, val, pct));
        clearBizForm();
        clearMessage();
    }

    @FXML
    private void handleRemoveBusiness() {
        BizRow sel = bizTable.getSelectionModel().getSelectedItem();
        if (sel != null) bizRows.remove(sel);
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    @FXML
    private void handleSubmit() {
        DeclarationType type = typeCombo.getValue();
        if (type == null) {
            showError("Please select a declaration type.");
            return;
        }
        if (positionRows.isEmpty()) {
            showError("At least one position entry is required.");
            return;
        }

        // AC3 – exceptional fields
        String amendedId = null;
        String reason    = null;
        if (type == DeclarationType.EXCEPTIONAL) {
            amendedId = amendedIdField.getText() == null ? "" : amendedIdField.getText().trim();
            reason    = amendReasonField.getText() == null ? "" : amendReasonField.getText().trim();
            if (amendedId.isEmpty()) {
                showError("EXCEPTIONAL: provide the id of the declaration being amended.");
                amendedIdField.requestFocus();
                return;
            }
            if (reason.isEmpty()) {
                showError("EXCEPTIONAL: provide the reason for this amendment.");
                amendReasonField.requestFocus();
                return;
            }
        }

        // Build data arrays
        List<Object[]> household = new ArrayList<>();
        for (HouseholdRow r : hhRows) {
            household.add(new Object[]{r.nameText, r.relationEnum});
        }

        List<Object[]> positions = new ArrayList<>();
        for (PositionRow r : positionRows) {
            positions.add(new Object[]{r.orgName, r.functionText, r.natureEnum,
                    r.grossVal, r.consultingVal, r.boardVal, r.startDateVal, r.endDate});
        }

        List<Object[]> subsidies = new ArrayList<>();
        for (SubsidyRow r : subsidyRows) {
            subsidies.add(new Object[]{r.orgName, r.amountVal, r.descText, r.dateVal});
        }

        List<Object[]> assets = new ArrayList<>();
        for (AssetRow r : assetRows) {
            Object detail = buildAssetDetail(r.typeEnum, r.detailText, r.municipalityText);
            if (detail == null) {
                showError("Asset detail could not be built for type " + r.typeEnum);
                return;
            }
            assets.add(new Object[]{r.typeEnum, r.valueVal, detail});
        }

        List<Object[]> business = new ArrayList<>();
        for (BizRow r : bizRows) {
            business.add(new Object[]{r.orgName, r.nifVal, r.totalVal, r.pctVal});
        }

        try {
            boolean saved = controller.submitDeclaration(type, amendedId, reason,
                    household, positions, subsidies, assets, business, new ArrayList<>());
            if (saved) {
                showSuccess("Declaration " + controller.getLastSubmittedDeclarationId()
                        + " submitted successfully (status: PENDING).");
                submitButton.setDisable(true);
            } else {
                showError("Submission failed. You may not be registered as a Political Agent.");
            }
        } catch (RuntimeException e) {
            // never let the submit button fail silently — surface the reason
            showError("Submission failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) mainController.showPoliticalAgentMenu();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setupOrgCombo(ComboBox<OrganizationDTO> combo, ObservableList<OrganizationDTO> orgs) {
        combo.setItems(orgs);
        combo.setPromptText("Select organisation…");
    }

    private String readPosFunction() {
        String text = posFunctionField.getText();
        return (text == null || text.isBlank()) ? null : text.trim();
    }

    private Double parseDouble(TextField f) {
        try { return Double.parseDouble(f.getText().trim()); } catch (Exception e) { return null; }
    }

    private Long parseLong(TextField f) {
        try { return Long.parseLong(f.getText().trim()); } catch (Exception e) { return null; }
    }

    private Date fromPicker(DatePicker picker) {
        LocalDate value = picker == null ? null : picker.getValue();
        if (value == null) return null;
        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Object buildAssetDetail(AssetType type, String detail, String municipality) {
        if (type == AssetType.REAL_ESTATE) return new RealEstate(detail, municipality);
        if (type == AssetType.VEHICLES)    return new VehicleAsset(detail);
        if (type == AssetType.STOCKS)      return new StockAsset(detail);
        return null;
    }

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

    private void clearMessage() { messageLabel.setText(""); }

    private void clearPositionForm() {
        posOrgCombo.setValue(null); posFunctionField.clear(); posNatureCombo.setValue(null);
        posGrossField.clear(); posConsultingField.clear(); posBoardField.clear();
        posStartField.setValue(null); posEndField.setValue(null);
    }

    private void clearSubsidyForm() {
        subOrgCombo.setValue(null); subAmountField.clear(); subDescField.clear(); subDateField.setValue(null);
    }

    private void clearAssetForm() {
        assetTypeCombo.setValue(null); assetValueField.clear(); assetDetailField.clear(); assetMunicipalityField.clear();
    }

    private void clearBizForm() {
        bizOrgCombo.setValue(null); bizNifField.clear(); bizValueField.clear(); bizPctField.clear();
    }

    // =========================================================================
    // Row model classes
    // =========================================================================

    public static class HouseholdRow {
        private final String name, relation;
        final String nameText;
        final HouseholdRelation relationEnum;

        HouseholdRow(String name, HouseholdRelation relation) {
            this.nameText    = name;
            this.relationEnum = relation;
            this.name        = name;
            this.relation    = relation.toString();
        }

        public String getName()     { return name; }
        public String getRelation() { return relation; }
    }

    public static class PositionRow {
        private final String org, function, nature, grossSalary, startDate;
        final String orgName;
        final String functionText;
        final PositionNature natureEnum;
        final double grossVal, consultingVal, boardVal;
        final Date startDateVal, endDate;

        PositionRow(String orgName, String func, PositionNature nature,
                    double gross, double consulting, double board, Date start, Date end) {
            this.orgName = orgName; this.functionText = func; this.natureEnum = nature;
            this.grossVal = gross; this.consultingVal = consulting; this.boardVal = board;
            this.startDateVal = start; this.endDate = end;
            this.org = orgName; this.function = func; this.nature = nature.toString();
            this.grossSalary = String.format("%.2f", gross);
            this.startDate = start != null ? new SimpleDateFormat("dd-MM-yyyy").format(start) : "";
        }

        public String getOrg()         { return org; }
        public String getFunction()    { return function; }
        public String getNature()      { return nature; }
        public String getGrossSalary() { return grossSalary; }
        public String getStartDate()   { return startDate; }
    }

    public static class SubsidyRow {
        private final String org, amount, description;
        final String orgName; final double amountVal; final String descText; final Date dateVal;

        SubsidyRow(String orgName, double amount, String desc, Date date) {
            this.orgName = orgName; this.amountVal = amount; this.descText = desc; this.dateVal = date;
            this.org = orgName; this.amount = String.format("%.2f", amount); this.description = desc;
        }

        public String getOrg()         { return org; }
        public String getAmount()      { return amount; }
        public String getDescription() { return description; }
    }

    public static class AssetRow {
        private final String type, value, detail;
        final AssetType typeEnum; final double valueVal; final String detailText; final String municipalityText;

        AssetRow(AssetType type, double value, String detail, String municipality) {
            this.typeEnum = type; this.valueVal = value; this.detailText = detail;
            this.municipalityText = municipality;
            this.type = type.toString(); this.value = String.format("%.2f", value);
            this.detail = (type == AssetType.REAL_ESTATE && municipality != null && !municipality.isEmpty())
                    ? detail + " (" + municipality + ")" : detail;
        }

        public String getType()   { return type; }
        public String getValue()  { return value; }
        public String getDetail() { return detail; }
    }

    public static class BizRow {
        private final String org, nif, totalValue, percentage;
        final String orgName; final long nifVal; final double totalVal, pctVal;

        BizRow(String orgName, long nif, double total, double pct) {
            this.orgName = orgName; this.nifVal = nif; this.totalVal = total; this.pctVal = pct;
            this.org = orgName; this.nif = String.valueOf(nif);
            this.totalValue = String.format("%.2f", total); this.percentage = String.format("%.2f%%", pct);
        }

        public String getOrg()        { return org; }
        public String getNif()        { return nif; }
        public String getTotalValue() { return totalValue; }
        public String getPercentage() { return percentage; }
    }
}
