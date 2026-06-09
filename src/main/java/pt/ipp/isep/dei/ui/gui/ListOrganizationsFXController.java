package pt.ipp.isep.dei.ui.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipp.isep.dei.controller.ListOrganizationsController;
import pt.ipp.isep.dei.domain.OrganizationType;
import pt.ipp.isep.dei.dto.OrganizationDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * GUI controller for US03 - List institutions grouped by type.
 */
public class ListOrganizationsFXController implements Initializable {

    @FXML private TableView<OrgRow> orgTable;
    @FXML private TableColumn<OrgRow, String> colType;
    @FXML private TableColumn<OrgRow, String> colName;
    @FXML private TableColumn<OrgRow, String> colNature;
    @FXML private Label messageLabel;

    private MainController mainController;
    private final ListOrganizationsController controller = new ListOrganizationsController();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colNature.setCellValueFactory(new PropertyValueFactory<>("nature"));

        loadOrganizations();
    }

    private void loadOrganizations() {
        Map<OrganizationType, List<OrganizationDTO>> grouped = controller.getOrganizationsGroupedByType();
        List<OrgRow> rows = new ArrayList<>();
        for (Map.Entry<OrganizationType, List<OrganizationDTO>> entry : grouped.entrySet()) {
            for (OrganizationDTO org : entry.getValue()) {
                rows.add(new OrgRow(
                        entry.getKey().toString(),
                        org.getName(),
                        org.getNatureDesignation()
                ));
            }
        }
        orgTable.setItems(FXCollections.observableArrayList(rows));
        if (rows.isEmpty()) {
            messageLabel.setText("No institutions registered yet.");
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showPoliticalAgentMenu();
        }
    }

    /** Row model for the TableView. */
    public static class OrgRow {
        private final String type;
        private final String name;
        private final String nature;

        public OrgRow(String type, String name, String nature) {
            this.type = type;
            this.name = name;
            this.nature = nature;
        }

        public String getType()   { return type; }
        public String getName()   { return name; }
        public String getNature() { return nature; }
    }
}
