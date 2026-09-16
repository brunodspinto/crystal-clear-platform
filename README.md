# Crystal Clear Platform

A transparency portal where political agents submit declarations of interests, an Ethics Committee reviews them, and citizens and journalists consult and analyse the data.

The platform combines a Java/JavaFX desktop application with graph algorithms, to detect nepotism and conflicts of interest in a network of relations between entities, and statistical analysis in Python.

## Features

**Declarations of interests**
- Political agents submit initial, regular or exceptional declarations covering income, positions, assets, business participations and household members
- Data from a previous declaration can be imported into a new one
- The Ethics Committee validates declarations or returns them with comments

**Relations network analysis**
- Relations graph between entities built from CSV data, with adjacency matrices and interactive SVG export
- Nepotism detection: pairs of entities connected by both an appointment and a personal tie
- Conflict of interest detection between political agents and organisations
- Shortest pathway and distance between any two entities
- Influence subnetwork of an entity
- Network dynamics over time through dated snapshots
- Automatic inference of family relationships (symmetric, inverse and transitive)

**Statistical analysis (Python)**
- Declaration statistics by type, role and institution
- Top companies by total share value held by political actors
- Largest stock value increases over time
- Correlation between remuneration and assets per political role, validated through residual analysis
- Non-linear patterns between participation percentages and total stock value

**Access and roles**
- Five user profiles: Ordinary Citizen, Journalist, Political Agent, Ethics Committee Member and Administrator
- Registration requests approved by the administrator, with email notification
- Role-based access: sensitive values are partially masked for citizens and fully visible to journalists

## Tech Stack

- **Java 21** and **JavaFX** (graphical interface)
- **Maven** (build and dependency management)
- **JUnit** and **JaCoCo** (testing and coverage)
- **Python** with pandas, NumPy, SciPy, Matplotlib and seaborn (statistics)
- **Graphviz** (graph rendering)

## Getting Started

### Requirements

- JDK 21 or newer
- Maven 3.6 or newer
- Python 3.10 or newer (only for statistical features)
- Graphviz 2.40 or newer, with the `dot` binary on the system PATH

### Installation

```bash
git clone https://github.com/brunodspinto/crystal-clear-platform.git
cd crystal-clear-platform
mvn clean package
pip install -r requirements.txt
```

Install Graphviz:
- **macOS:** `brew install graphviz`
- **Linux (Debian/Ubuntu):** `sudo apt install graphviz`
- **Windows:** download the installer from [graphviz.org](https://graphviz.org/download/) and add `dot` to the PATH

### Running

```bash
mvn javafx:run
```

### Tests

```bash
mvn clean test              # run unit tests
mvn test jacoco:report      # generate coverage report
```

## Documentation

- [User Manual](User-Manual/UserManual.md): installation and features available to each role
- [`docs/`](docs): requirements engineering, analysis and design artefacts

## Team

Developed by Bruno Pinto, Tomás Fonseca, Marcelo Oliveira and André Oliveira.

## Academic Context

Developed as the Integrative Project (LAPR2) of the 1st year, 2nd semester of the Degree in Informatics Engineering at ISEP – Polytechnic of Porto, 2025/2026, following an iterative process across three sprints.
