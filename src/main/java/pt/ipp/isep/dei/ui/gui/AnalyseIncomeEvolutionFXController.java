package pt.ipp.isep.dei.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipp.isep.dei.controller.AnalyseIncomeEvolutionController;
import pt.ipp.isep.dei.dto.DeclarationDTO;
import pt.ipp.isep.dei.dto.PoliticalAgentDTO;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AnalyseIncomeEvolutionFXController implements Initializable {

    @FXML private ComboBox<PoliticalAgentDTO> agentCombo;
    @FXML private DatePicker startPicker;
    @FXML private DatePicker endPicker;
    @FXML private Label messageLabel;

    @FXML private TableView<DeclarationRow> declarationTable;
    @FXML private TableColumn<DeclarationRow, String> colId;
    @FXML private TableColumn<DeclarationRow, String> colDate;
    @FXML private TableColumn<DeclarationRow, String> colType;
    @FXML private TableColumn<DeclarationRow, String> colTotal;
    @FXML private TableColumn<DeclarationRow, Integer> colCount;

    @FXML private LineChart<String, Number> incomeChart;

    private MainController mainController;
    private final AnalyseIncomeEvolutionController controller = new AnalyseIncomeEvolutionController();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentCombo.getItems().addAll(controller.getPoliticalAgents());

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCount.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    @FXML
    private void handleSearch() {
        messageLabel.setText("");
        declarationTable.getItems().clear();
        incomeChart.getData().clear();

        PoliticalAgentDTO agent = agentCombo.getValue();
        LocalDate start = startPicker.getValue();
        LocalDate end = endPicker.getValue();

        if (agent == null || start == null || end == null) {
            messageLabel.setText("Select an agent and both dates.");
            return;
        }

        Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<DeclarationDTO> declarations;
        try {
            declarations = controller.getIncomeEvolution(agent, startDate, endDate);
        } catch (IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());
            return;
        }

        if (declarations.isEmpty()) {
            messageLabel.setText("No validated declarations found for the selected period.");
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (DeclarationDTO d : declarations) {
            double total = d.getTotalIncome();
            String dateLabel = DATE_FMT.format(d.getSubmissionDate());
            declarationTable.getItems().add(new DeclarationRow(
                    d.getId(), dateLabel, d.getType(),
                    String.format("%.2f €", total), d.getIncomes().size()
            ));
            series.getData().add(new XYChart.Data<>(dateLabel, total));
        }
        incomeChart.getData().add(series);
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showJournalistMenu();
        }
    }

    /** Row model for the TableView. */
    public static class DeclarationRow {
        private final String id;
        private final String date;
        private final String type;
        private final String total;
        private final int count;

        public DeclarationRow(String id, String date, String type, String total, int count) {
            this.id = id;
            this.date = date;
            this.type = type;
            this.total = total;
            this.count = count;
        }

        public String getId()    { return id; }
        public String getDate()  { return date; }
        public String getType()  { return type; }
        public String getTotal() { return total; }
        public int    getCount() { return count; }
    }
}
