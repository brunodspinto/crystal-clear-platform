package pt.ipp.isep.dei.ui.gui;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reusable GUI controller for the Ethics Committee statistics screens
 * (US14, US17, US18, US28, US30, US31). The concrete screen is provided through
 * {@link #setScreen(StatsScreen)}. Running the analysis launches the
 * corresponding Python script as an external process (its textual output is
 * streamed into a read-only area) and, on success, opens the generated SVG
 * graph(s) in the system browser through {@link HostServices}.
 *
 * <p>The project does not depend on {@code javafx-web}, so the SVG is opened in
 * the default browser instead of an embedded WebView.</p>
 */
public class StatisticsAnalysisFXController {

    private static final String PYTHON = "python3";

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;
    @FXML private Button runButton;
    @FXML private Button openButton;

    private MainController mainController;
    private StatsScreen screen;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Configures this controller for a specific statistics screen.
     *
     * @param screen the statistics screen to display and run.
     */
    public void setScreen(StatsScreen screen) {
        this.screen = screen;
        titleLabel.setText(screen.getTitle());
        descriptionLabel.setText(screen.getDescription());
        clearStatus();
        logArea.clear();
    }

    @FXML
    private void handleRun() {
        if (screen == null) {
            return;
        }
        runButton.setDisable(true);
        clearStatus();
        logArea.clear();
        logArea.appendText("Running " + screen.getScriptPath() + " ...\n\n");

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                runScript();
            }
        }, "stats-" + screen.name());
        worker.setDaemon(true);
        worker.start();
    }

    private void runScript() {
        int exitCode = -1;
        try {
            ProcessBuilder builder = new ProcessBuilder(PYTHON, screen.getScriptPath());
            builder.directory(new File(System.getProperty("user.dir")));
            builder.environment().put("MPLBACKEND", "Agg");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String text = line;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            logArea.appendText(text + "\n");
                        }
                    });
                }
            }
            exitCode = process.waitFor();
        } catch (IOException ex) {
            final String message = ex.getMessage();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    logArea.appendText(
                            "\nCould not start Python. Make sure 'python3' is on the PATH.\n"
                                    + message + "\n");
                }
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        final int code = exitCode;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                onScriptFinished(code);
            }
        });
    }

    private void onScriptFinished(int exitCode) {
        runButton.setDisable(false);
        if (exitCode == 0) {
            showSuccess("Analysis finished. Opening the graph(s)...");
            openGraphs();
        } else {
            showError("Analysis failed (exit code " + exitCode
                    + "). You can still open a previously generated graph.");
        }
    }

    @FXML
    private void handleOpenGraphs() {
        clearStatus();
        openGraphs();
    }

    private void openGraphs() {
        HostServices hostServices = mainController == null ? null : mainController.getHostServices();
        if (hostServices == null) {
            showError("Cannot open the graph: host services unavailable.");
            return;
        }

        int opened = 0;
        StringBuilder missing = new StringBuilder();
        for (String svgPath : screen.getSvgPaths()) {
            File file = new File(System.getProperty("user.dir"), svgPath);
            if (file.exists()) {
                hostServices.showDocument(file.toURI().toString());
                opened = opened + 1;
            } else {
                if (missing.length() > 0) {
                    missing.append(", ");
                }
                missing.append(svgPath);
            }
        }

        if (opened == 0) {
            showError("No graph found yet. Run the analysis first. (missing: " + missing + ")");
        } else if (missing.length() > 0) {
            showSuccess("Opened " + opened + " graph(s). Not found yet: " + missing);
        } else {
            showSuccess("Opened " + opened + " graph(s) in the browser.");
        }
    }

    @FXML
    private void handleBack() {
        if (mainController != null) {
            mainController.showEthicsCommitteeMenu();
        }
    }

    private void showError(String message) {
        statusLabel.getStyleClass().setAll("message-error");
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        statusLabel.getStyleClass().setAll("message-success");
        statusLabel.setText(message);
    }

    private void clearStatus() {
        statusLabel.getStyleClass().clear();
        statusLabel.setText("");
    }
}
