# US09 - Consult the Integrated Situation of a Political Agent on a Given Date

## 2. Analysis

### 2.1. Relevant Domain Model Excerpt

![Domain Model](svg/US09-DM.svg)

### 2.2. Other Remarks

The "integrated situation" of a political agent on a given date is composed of all information declared across their validated declarations submitted up to that date. This includes:

- Active **positions** (public or private roles held by the agent)
- **Income sources** and respective amounts
- **Assets** (real estate, vehicles, financial instruments, etc.)
- **Business participations** (stakes in companies)

Only declarations with status `VALIDATED` are taken into account (per AC2). If multiple declarations exist up to the given date, the most recent validated data per category should be presented. If no validated declarations exist, an appropriate message is shown to the user (AC3).

This US depends on US06 (declaration submission) and US08 (declaration validation) being implemented, as there must be validated declarations for this feature to return meaningful results.
