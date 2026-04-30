# Glossary

**Terms, Expressions and Acronyms (TEA) must be organized alphabetically.**

| **_TEA_** (EN) | **_TEA_** (PT) | **_Description_** (EN) |
| :--- | :--- | :--- |
| **Adjacency Matrix** | **Matriz de Adjacência** | A matrix representation of a graph where rows and columns correspond to entities and entries represent the existence or weight of a relationship between them. |
| **AD** | **DP** | Acronym for _Asset Declaration_. |
| **Acquisition Value** | **Valor de Aquisição** | The price at which an asset was originally purchased or acquired by a political agent. |
| **Asset** | **Ativo / Bem** | An item of property owned by a political agent, including real estate, which has an acquisition and market value. |
| **Asset Declaration** | **Declaração de Património** | A formal document submitted by a political agent detailing their income, assets, positions, and business participations. |
| **Business Participation** | **Participação Social** | Quotas, shares, and holdings a political agent has in companies, along with their respective market values. |
| **Complaint** | **Queixa** | A report submitted by a citizen regarding a political agent's behaviour or lack of transparency. |
| **Conflict of Interest** | **Conflito de Interesses** | A situation where a political agent's personal financial interests may influence or compromise their public duties. |
| **CSV Dataset** | **Conjunto de Dados CSV** | A structured comma-separated values file used to exchange declaration or holdings data between the platform and external statistical tools. |
| **Declaration Dataset** | **Dataset de Declarações** | A CSV file containing one row per declaration, with columns for declaration id, agent id, role, institution, declaration type, declaration date, gross salary, side income, and asset values by category (Table 2 of the specifications). |
| **Declaration of Interests** | **Declaração de Interesses** | The core document submitted by Political Agents, which may be initial, regular, or exceptional, detailing their professional, financial, and patrimonial situation. |
| **DI** | **DI** | Acronym for _Declaration of Interests_. |
| **EC** | **CE** | Acronym for _Ethics Committee_. |
| **Entity** | **Entidade** | A node in the heterogeneous network representing a real-world object of one of the following types: person, organization, position, or asset. Each entity has an id, type, start/end dates, and type-specific attributes. |
| **Ethics Committee** | **Comissão de Ética** | The body responsible for managing and supervising declarations of interests and verifying incompatibilities. |
| **Exceptional Declaration** | **Declaração Excecional** | A Declaration of Interests submitted outside the regular schedule, triggered by a significant change in a political agent's situation. |
| **Function** | **Função** | A political or administrative role (e.g., Mayor, Minister, Deputy) performed by a Political Agent. |
| **Graph** | **Grafo** | A mathematical structure composed of entities (vertices/nodes) and relationships (edges) used to model the heterogeneous and multi-relational network of political agents, organizations, positions, and assets. |
| **Heterogeneous Network** | **Rede Heterogénea** | A graph that combines different types of vertices (e.g., person, organization, position, asset), as opposed to a homogeneous network where all nodes are of the same type. |
| **Holdings Dataset** | **Dataset de Participações** | A CSV file containing one row per political-agent/company pair, with columns for agent id, company NIF, total value in stocks, company percentage, and declaration date (Table 3 of the specifications). |
| **Illicit Enrichment** | **Enriquecimento Ilícito** | Wealth accumulated by a political agent that cannot be justified by their official salary or declared income. |
| **Income** | **Rendimento** | The financial gains or remuneration received by a political agent from various sources, subject to temporal analysis. |
| **Incompatibility** | **Incompatibilidade** | A legal or regulatory conflict that prevents a political agent from simultaneously holding certain positions or financial interests. |
| **Initial Declaration** | **Declaração Inicial** | The first Declaration of Interests submitted by a political agent upon taking up a public function. |
| **Integrated Situation** | **Situação Integrada** | A consolidated view of a Political Agent's financial and professional status on a given date, used for temporal analysis. |
| **Journalist** | **Jornalista** | A registered member of the Journalists' Union with specific access rights to the transparency portal for investigative purposes. |
| **JRN** | **JRN** | Acronym for _Journalist_. |
| **Market Value** | **Valor de Mercado** | The estimated current value of an asset at a given point in time, as declared by a political agent. |
| **Multi-relational Network** | **Rede Multi-Relacional** | A graph in which pairs of vertices can be connected by multiple distinct types of edges (e.g., relativeOf, friendOf, memberOf), each representing a different type of relationship. |
| **National Identity Card** | **Cartão de Cidadão** | An official identification document required from Ordinary Citizens during platform registration. |
| **Nepotism** | **Nepotismo** | The practice of appointing or favouring relatives or close associates to positions of power or authority, regardless of their merit. |
| **OC** | **CC** | Acronym for _Ordinary Citizen_. |
| **ORG** | **ORG** | Acronym for _Organization_. |
| **Organization** | **Organização** | An entity (company, political party, foundation, institute, or association) associated with a political agent's positions, business participations, or subsidy declarations. |
| **OrganizationType** | **Tipo de Organização** | A predefined classification of an organization's legal form: Company, Political Party, Foundation, Institute, or Association. |
| **Ordinary Citizen** | **Cidadão Comum** | A member of the public who has an interest in scrutinising political agents and can use the portal to report suspicious behaviour. |
| **PA** | **AP** | Acronym for _Political Agent_. |
| **PO** | **PO** | Acronym for _Product Owner_. |
| **Product Owner** | **Product Owner** | The stakeholder responsible for the platform's overall requirements. In Sprint 2, the Product Owner triggers data export operations (US24, US25) and graph interaction features (US19–US21, US26). |
| **Political Agent** | **Agente Político** | A person performing a political function such as deputy, minister, councillor, mayor, or parish council president. |
| **Position** | **Cargo** | A professional or public role held by a political agent, either in a public institution or a private organisation. |
| **Press Card** | **Cartão de Jornalista** | An official credential issued by the Journalists' Union, required for journalist registration on the platform. |
| **Real Estate** | **Imóvel** | A specific type of asset comprising land or buildings, categorized as either urban or rural. |
| **Relationship** | **Relação / Aresta** | An edge in the heterogeneous network connecting two entities. All relationships share a common structure: id, type, startDate, endDate, entity1, entity2, weight. Types include relativeOf, friendOf, holdsPosition, inOrganization, memberOf, influences, among others. |
| **Registration Request** | **Pedido de Registo** | A formal application submitted by a prospective user to gain access to the platform with a specific role. |
| **Regular Declaration** | **Declaração Regular** | A Declaration of Interests submitted periodically by a political agent during the exercise of their public function. |
| **Rejection Reason** | **Motivo de Rejeição** | The mandatory explanation provided by the System Administrator when refusing a registration request, displayed to the user upon their next login attempt. |
| **Role** | **Papel / Perfil** | The set of permissions assigned to a user (e.g., Administrator, Journalist, Citizen) that dictates their access level on the platform. |
| **SA** | **AS** | Acronym for _System Administrator_. |
| **Subsidy** | **Subsídio / Apoio** | Financial support or grants received by a political agent from specific institutions. |
| **System Administrator** | **Administrador do Sistema** | The person or team responsible for managing the platform and its access privileges. |
| **TP** | **PT** | Acronym for _Transparency Portal_. |
| **Transparency Portal** | **Portal da Transparência** | The IT platform that supports the registration, evaluation, and analysis of declarations of interests by political agents. |
| **Validated Declaration** | **Declaração Validada** | A declaration of interests that has been reviewed and approved by the Ethics Committee. |
| **Validation Comment** | **Comentário de Validação** | Specific feedback provided by an Ethics Committee member on a section of a declaration that contains inconsistencies and is returned for correction. |
| **VD** | **DV** | Acronym for _Validated Declaration_. |