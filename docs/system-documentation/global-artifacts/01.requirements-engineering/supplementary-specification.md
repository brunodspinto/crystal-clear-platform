# Supplementary Specification (FURPS+)

## Functionality

_Specifies functionalities that:  
&nbsp; &nbsp; (i) are common across several US/UC;  
&nbsp; &nbsp; (ii) are not related to US/UC, namely: Audit, Reporting and Security._

* **Security/Authentication:** All users who wish to use the application must be authenticated. The system enforces strict password rules: passwords must contain exactly seven alphanumeric characters, including exactly three capital letters and two digits.
* **Access Control:** The information provided and the operations available within the system vary depending on the specific type of user (e.g., Administrator, Political Agent, Citizen).

## Usability

_Evaluates the user interface. It has several subcategories,
among them: error prevention; interface aesthetics and design; help and
documentation; consistency and standards._

* **Documentation Language:** The application documentation (including requirements, analysis, and design artifacts) must be written entirely in English.
* **User Interface:** Interaction with the system should be straightforward, providing clear prompts and feedback during Input/Output operations to prevent user errors.

## Reliability

_Refers to the integrity, compliance and interoperability of the software. The requirements to be considered are: frequency and severity of failure, possibility of recovery, possibility of prediction, accuracy, average time between failures._

* **Data Integrity:** The validation of business rules must be rigorously respected when recording and updating data within the system to prevent corrupt or invalid states.
* **Data Persistence:** The application must employ object serialization to guarantee the persistence of the data between successive runs (i.e., data must not be lost when the application restarts).

## Performance

_Evaluates the performance requirements of the software, namely: response time, start-up time, recovery time, memory consumption, CPU usage, load capacity and application availability._

* **Serialization Efficiency:** The system should be able to serialize and deserialize objects during start-up and shutdown without causing excessive delays to the user experience.
* **Responsiveness:** As a pedagogical Java application, it should handle in-memory data operations swiftly, maintaining a low response time for daily operations.

## Supportability

_The supportability requirements gathers several characteristics, such as:
testability, adaptability, maintainability, compatibility,
configurability, installability, scalability and more._

* **Maintainability:** The class structure must be designed to allow easy maintenance and the addition of new features following the best Object-Oriented (OO) practices.
* **Testability:** The development team must implement unit tests for all methods, except for the methods that specifically implement Input/Output operations.
* **Testing Framework:** Unit tests must be implemented using the JUnit 5 framework.
* **Coverage:** The JaCoCo plugin must be used to generate the test coverage report.

### Design Constraints

_Specifies or constraints the system design process. Examples may include: programming languages, software process, mandatory standards/patterns, use of development tools, class library, etc._

* **Design Methodology:** The team must adopt best practices for identifying requirements and for Object-Oriented software analysis and design.
* **Diagram Format:** All images and figures produced during the software development process (e.g., UCD, SSD, Domain Models) must be recorded and exported in SVG format.

### Implementation Constraints

_Specifies or constraints the code or construction of a system such
such as: mandatory standards/patterns, implementation languages,
database integrity, resource limits, operating system._

* **Programming Language:** The application must be exclusively developed in the Java programming language.
* **Coding Standards:** The team must adopt recognized coding standards, such as CamelCase for class and method naming.
* **Code Documentation:** The team must use Javadoc to generate useful, standard documentation for the Java code.

### Interface Constraints

_Specifies or constraints the features inherent to the interaction of the
system being developed with other external systems._

* **User Interaction:** The primary interface for this sprint relies on a console-based Input/Output system to interact with the actors.
* **External Systems:** No external database management systems are required at this stage, as data persistence is handled via local object serialization.

### Physical Constraints

_Specifies a limitation or physical requirement regarding the hardware used to house the system, as for example: material, shape, size or weight._

* **Hardware Restrictions:** There are no specific physical constraints. The application is designed to run on any standard personal computer capable of executing a Java Virtual Machine (JVM).