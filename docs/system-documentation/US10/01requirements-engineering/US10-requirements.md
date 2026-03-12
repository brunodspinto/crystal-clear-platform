"As a journalist, I want to analyse the evolution of a Political
Agent’s income over a given period of time (between two dates)."

The system should allow a journalist to select a political agent and a time period (start date and end date) and view how their total income changed across declarations submitted in that period. Only validated declarations should be considered.



AC1: The journalist must select a valid registered political agent

AC2: The start date must be before or equal to the end date

AC3: Only validated declarations (status = VALIDATED) within the period are shown

AC4: Results are displayed chronologically ordered by declaration date

AC5: If no validated declarations exist in the period, an appropriate message is shown


Dependencies:

US06 - declarations must exist to be analysed

US08 - only validated declarations are included in the analysis

US01/US02 - the journalist must be registered and approved to access this feature

Input:
Selectable: Political Agent name

Typed/Selectable: start date

Typed/Selectable: end date

Output:
If successful, list of the dates with the corresponding income

If no results, show a message saying that there are no validated results

If invalid dates or any other error that may be futurely averiguated, show error message