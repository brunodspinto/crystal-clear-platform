"As a member of the Ethics Committee, I want to consult the Integrated Situation of a Political Agent on a given date."

The system should allow a member of the Ethics Committee to select a political agent and a specific date, and view their integrated situation on that date, including all active positions, income sources, assets, and business participations declared up to that date.

AC1: The Ethics Committee member must select a valid registered political agent

AC2: Only validated declarations (status = VALIDATED) up to the specified date are considered

AC3: If no validated declarations exist up to the specified date, an appropriate message is shown

Dependencies:

US06 - declarations must exist to be consulted

US08 - only validated declarations are included

US01/US02 - the Ethics Committee member must be registered and approved to access this feature

Input:

Selectable: Political Agent name

Typed/Selectable: date

Output:

If successful, display the integrated situation of the political agent on that date (positions, income, assets, business participations)

If no results, show a message saying that there are no validated declarations up to that date

If invalid date or any other error, show error message
