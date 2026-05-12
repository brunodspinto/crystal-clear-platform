# Crystal Clear Platform — User Manual

**Sprint 2 — 1st Version (May 2026)**

ISEP / LEI / LAPR2 2025-2026 — Team g023

| Student Number | Name              |
|----------------|-------------------|
| [removed]        | Tomás Fonseca     |
| [removed]        | Marcelo Oliveira  |
| [removed]        | André Oliveira    |
| [removed]        | Bruno Pinto       |

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
- **Python packages:** `pandas`, `matplotlib`, `scipy`
- **Git:** 2.30 or newer
- **RAM:** 4 GB (8 GB recommended)
- **Disk:** 500 MB free

### Installation

1. Clone the repository: `git clone <repository-url>`
2. Enter the project folder: `cd HEREISLAPR`
3. Build the application: `mvn clean package`
4. Install Python dependencies: `pip install -r requirements.txt`
5. Run the platform: `java -jar target/project-template-1.0-SNAPSHOT-jar-with-dependencies.jar`

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
- Compare total income across political roles using boxplots.
- Identify the top companies by total share value held by political actors.
- Identify the top stock value increases over time.
- Consult the integrated situation of a political agent on a chosen date.

### 3.3 Political Agent

A person performing a political role (deputy, minister, mayor, councillor, etc.).

- Submit a Declaration of Interests (initial, regular, or exceptional).
- List the institutions registered on the platform.

### 3.4 Ethics Committee Member

A member of the body responsible for supervising declarations.

- Review pending registration requests and accept or reject them.
- Validate a Declaration of Interests, or return it with comments.
- Consult the integrated situation of a political agent on a given date.

### 3.5 Product Owner / System Administrator

A technical user who configures and feeds the platform.

- Create tasks in the platform.
- Register a new organisation.
- Load the list of entities from a CSV file.
- Build the relations graph between entities from a CSV file.
- Generate the adjacency matrices of the relations graph.
- Export the declarations dataset to CSV.
- Export the holdings dataset to CSV.
- Export the heterogeneous graph as an interactive SVG file.

### 3.6 Common Actions (any user)

These actions are available to every user, regardless of role:

- Register on the platform by filling out the registration form.
- Log in with the credentials assigned after a registration request is approved.
- Log out at any moment by returning to the main menu and choosing "Exit".

---

## 4. Glossary

| Term | Description |
| :--- | :--- |
| Declaration of Interests | The document submitted by a Political Agent detailing income, assets, positions, and business participations. |
| Ethics Committee | The body responsible for validating declarations and supervising compliance. |
| Integrated Situation | A consolidated view of a Political Agent's financial and professional status on a given date. |
| Journalist | A registered member of the press with extended access to the platform. |
| Ordinary Citizen | A member of the public who can consult declarations and submit complaints. |
| Political Agent | A person performing a political function (deputy, minister, mayor, etc.). |
| Product Owner | The technical user who configures the platform and exports data. |
| Validated Declaration | A Declaration of Interests reviewed and approved by the Ethics Committee. |
