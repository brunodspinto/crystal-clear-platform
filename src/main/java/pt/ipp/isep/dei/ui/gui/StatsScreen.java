package pt.ipp.isep.dei.ui.gui;

/**
 * Configuration for each statistics screen handled by the Ethics Committee GUI.
 * Every value bundles the human-readable title, a short description, the Python
 * script that produces the analysis and the SVG graph(s) it generates. A single
 * {@link StatisticsAnalysisFXController} is reused for all of them, configured
 * with one of these values.
 *
 * <p>Paths are relative to the project root (the working directory used by
 * {@code mvn javafx:run}), matching the relative paths hard-coded in the
 * Python scripts.</p>
 */
public enum StatsScreen {

    US14("Declaration Statistics",
            "Descriptive statistics (histograms and box plots) of the declared income components.",
            "src/main/python/us14/declaration_stats.py",
            new String[]{
                    "docs/system-documentation/US14/US14_histograms.svg",
                    "docs/system-documentation/US14/US14_boxplots.svg"
            }),

    US16("Income Boxplots by Role",
            "Distribution of the total declared income, grouped by political role.",
            "src/main/python/us16/income_boxplot.py",
            new String[]{
                    "docs/system-documentation/US16/us16_boxplot.svg"
            }),

    US17("Top Companies by Stock Value",
            "Companies with the highest total value held by political agents.",
            "src/main/python/us17/top_companies.py",
            new String[]{
                    "docs/system-documentation/US17/US17_top_companies.svg"
            }),

    US18("Largest Stock Increases",
            "Agents with the largest increase in stock value between declarations.",
            "src/main/python/us18/stock_increases.py",
            new String[]{
                    "docs/system-documentation/US18/US18_top_increases.svg",
                    "docs/system-documentation/US18/US18_evolution.svg"
            }),

    US28("Role vs Asset Correlation",
            "Pearson correlation between total remuneration and each asset type, by role.",
            "src/main/python/us28/role_asset_correlation.py",
            new String[]{
                    "docs/system-documentation/US28/US28_correlation.svg"
            }),

    US30("Residual Analysis",
            "Residuals of the wealth-vs-income regression, in original and normalized scale.",
            "src/main/python/us30/residual_analysis.py",
            new String[]{
                    "docs/system-documentation/US30/US30_residuals_original.svg",
                    "docs/system-documentation/US30/US30_residuals_normalized.svg"
            }),

    US31("Shareholding Non-linearity",
            "Relationship between ownership percentage and total stock value.",
            "src/main/python/us31/shareholding_nonlinearity.py",
            new String[]{
                    "docs/system-documentation/US31/US31_scatter_fit.svg",
                    "docs/system-documentation/US31/US31_residuals.svg",
                    "docs/system-documentation/US31/US31_mean_by_ownership.svg"
            });

    private final String title;
    private final String description;
    private final String scriptPath;
    private final String[] svgPaths;

    StatsScreen(String title, String description, String scriptPath, String[] svgPaths) {
        this.title = title;
        this.description = description;
        this.scriptPath = scriptPath;
        this.svgPaths = svgPaths;
    }

    /**
     * @return the title shown at the top of the screen.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return a short description of what the analysis computes.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the project-relative path of the Python script to run.
     */
    public String getScriptPath() {
        return scriptPath;
    }

    /**
     * @return the project-relative paths of the SVG graphs produced by the script.
     */
    public String[] getSvgPaths() {
        return svgPaths.clone();
    }
}
