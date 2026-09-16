# Crystal Clear Platform: User Manual

Developed by Bruno Pinto, Tomás Fonseca, Marcelo Oliveira and André Oliveira. ISEP, 2025/2026.

---

## 1. Introduction

The **Crystal Clear Platform** is a transparency portal where political agents submit declarations of interests, the Ethics Committee reviews them, and citizens and journalists consult and analyse the data. This manual describes how to install the platform and what each type of user can do with it.

---

## 2. Requirements

To install and run the platform, your machine must have:

- **OS:** Windows 10+ / macOS 12+ / any modern Linux distribution
- **Java:** JDK 21 or newer
- **Maven:** 3.6 or newer
- **Python:** 3.10 or newer (only for statistical features)
- **Python packages:** `pandas`, `numpy`, `matplotlib`, `scipy`, `seaborn`
- **Graphviz:** 2.40 or newer (required by the relations graph rendering; the `dot` binary must be on the system PATH)
- **Git:** 2.30 or newer
- **RAM:** 4 GB (8 GB recommended)
- **Disk:** 500 MB free

### Installation

1. Clone the repository: `git clone <repository-url>`
2. Enter the project folder: `cd crystal-clear-platform`
3. Build the application: `mvn clean package`
4. Install Python dependencies: `pip install -r requirements.txt`
5. Install Graphviz:
   - macOS: `brew install graphviz`
   - Linux (Debian/Ubuntu): `sudo apt install graphviz`
   - Windows: download the installer from https://graphviz.org/download/ and make sure the `dot` binary is added to the system PATH.
6. Run the platform (graphical interface): `mvn javafx:run`
   - A text-only console version can also be launched from the generated `target/crystal-clear-platform-1.0-SNAPSHOT-jar-with-dependencies.jar`; this manual describes the graphical interface.

The platform opens a graphical interface (JavaFX) where you log in and reach the menu of your role. The sections below describe the functionalities available in each menu.

If the "Build Relations Graph" option in the Administrator menu fails with a message about Graphviz, it means the `dot` binary is missing from the PATH: install Graphviz as shown above and run the option again.

---

## 3. Roles

The platform supports five user profiles. Sections 3.1 to 3.5 describe each profile and the functionalities available to them. Section 3.6 lists the actions that any user can perform regardless of role.

### 3.1 Ordinary Citizen

A member of the public who wants to scrutinise political agents.

- Request a registration on the platform.
- Consult the assets of a political agent on a specific date (sensitive values are partially masked).
- Submit a complaint about a political agent's behaviour.

### 3.2 Journalist

A registered member of the Journalists' Union with extended access for investigative work.

- Consult the assets of a political agent on a specific date (full details).
- Analyse the evolution of a political agent's income over a chosen period.

### 3.3 Political Agent

A person performing a political role (deputy, minister, mayor, councillor, etc.).

- Submit a Declaration of Interests (initial, regular, or exceptional). The declaration includes income, positions, assets, business participations, and the household members of the agent. When filling a new declaration, data from a previous declaration can be imported to avoid retyping. An exceptional declaration must reference the declaration being amended and the reason for the amendment.
- List the institutions registered on the platform.

### 3.4 Ethics Committee Member

A member of the body responsible for supervising declarations and investigating irregularities.

**Network and validation:**

- Validate a Declaration of Interests, or return it with comments.
- Consult the integrated situation of a political agent on a given date.
- Assess a complaint submitted by a citizen, marking it as valid or invalid (with the reason when invalid).
- Detect nepotism: find pairs of entities connected by both an appointment and a personal tie.
- Detect conflicts of interest between political agents and organisations.
- Find a pathway between two entities in the relations network and see the distance between them.
- Visualise the influence subnetwork of an entity: the part of the network where that entity can integrate chains of influence.
- Examine the asset and net worth evolution of political agents to spot anomalous wealth accumulation.

**Statistical analyses:**

- View declaration statistics (distribution of declarations by type, role, and institution).
- Identify the top companies by total share value held by political actors.
- Identify the largest stock value increases over time.
- Analyse the correlation between remuneration and assets for each political role.
- Validate the statistical results through residual analysis (distribution of residuals before and after normalisation).
- Investigate non-linear patterns between company participation percentages and total stock value.

### 3.5 Product Owner / System Administrator

A technical user who configures and feeds the platform.

- Review pending registration requests and accept or reject them. The requester is notified of the decision by email.
- Register a new organisation.
- Load the list of entities from a CSV file.
- Build the relations graph between entities from a CSV file.
- Generate the adjacency matrices of the relations graph.
- Generate the global support adjacency matrix of the support graph (no directions or weights).
- Export the declarations dataset to CSV.
- Export the holdings dataset to CSV.
- Export the heterogeneous graph as an interactive SVG file.
- Export the Declaration of Interests data to a graph CSV file; family relationships (symmetric, inverse, and transitive) are inferred automatically during the export.

### 3.6 Common Actions (any user)

These actions are available to every user, regardless of role:

- Register on the platform by filling out the registration form.
- Log in with the credentials assigned after a registration request is approved.
- View the dynamics of the relations network over time (Network Dynamics): a list of dates produces discrete snapshots of the network. Available from every authenticated role menu.
- Log out at any moment using the **Logout** button in your role menu.

---

## 4. Glossary

| Term | Description |
| :--- | :--- |
| Complaint | A report submitted by a citizen about the behaviour of a political agent, later assessed by the Ethics Committee. |
| Conflict of Interest | A situation where a political agent's private interests may improperly influence their public duties. |
| Declaration of Interests | The document submitted by a Political Agent detailing income, assets, positions, business participations, and household members. |
| Ethics Committee | The body responsible for validating declarations and supervising compliance. |
| Influence Subnetwork | The part of the relations network where a given entity can integrate chains of influence. |
| Integrated Situation | A consolidated view of a Political Agent's financial and professional status on a given date. |
| Journalist | A registered member of the press with extended access to the platform. |
| Nepotism | The appointment of a person by someone with whom they have a personal tie. |
| Ordinary Citizen | A member of the public who can consult declarations and submit complaints. |
| Political Agent | A person performing a political function (deputy, minister, mayor, etc.). |
| Product Owner | The technical user who configures the platform and exports data. |
| Snapshot | The state of the relations network at a specific date, used to analyse the network dynamics over time. |
| Support Graph | An undirected, unweighted graph connecting entities that support each other in the network. |
| Validated Declaration | A Declaration of Interests reviewed and approved by the Ethics Committee. |
