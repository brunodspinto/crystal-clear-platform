"As a citizen/journalist, I want to consult the assets of a political
agent on a specific date."

The system should display the assets of a chosen political agent on a specific date, accessible for both citizens and journalists. Only validated declarations should be considered.

AC1: The journalist must select a valid registered political agent

AC2: Only validated declarations (status = VALIDATED) on that specific date are shown

AC3: If no validated declarations exist in the period, an appropriate message is shown

AC4: Sensitive data must be partially omitted based on the user's role

Dependencies:

US06 - declarations must exist to be analysed

US08 - only validated declarations are included in the analysis

US01/US02 - if somebody choses the journalist option, the journalist must be registered and approved to access this feature. *This dependency will be reviewed in the future to see if it's really needed.

Input:

Selectable: Political Agent name

Typed/Selectable: date of the assets

Output:

If successful, list of the dates with the corresponding income

If no results, show a message saying that there are no validated results

If invalid dates or any other error that may be futurely averiguated, show error message



