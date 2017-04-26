package com.moneymaker.modules.cashflowmanager;

import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created for Money Maker by Jay Damon on 9/20/2016.
 */
public class SQLCashFlow {

    private Connection conn = ConnectionManagerUser.getInstance().getConnection();

    private FormatDate formatDate = new FormatDate();
    private String userSchema = new UsernameData().getUserSchema();

    public ObservableList<CashFlow> viewMonthlyBudgetRemaining(String year, String month, String day) {
        ObservableList<CashFlow> monthlyBudgetRemaining = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.viewMonthlyBudgetRemaining(\"" + userSchema + "\", " + year + ", " + month + ", " + day + ")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String cashFlowAccount = "";
                String cashFlowBudget = rs.getObject("Budget", String.class);
                String cashFlowDate = rs.getObject("Date",String.class);
                String budgetRemaining = rs.getObject("Amount", String.class);
                String budgetAmount = rs.getObject("Budget Amount", String.class);
                String cashFlowProjected = null;

                if (budgetRemaining == null) {
                    cashFlowProjected = budgetAmount;
                } else {
                    cashFlowProjected = budgetRemaining;
                }

                String cashFlowActual = monthlyBudgetActual(cashFlowBudget, year, month);
                String cashOnHandStartingProjection = "";
                String cashOnHandActual = "";
                String cashOnHandCurrentProjection = "";

                String cashFlowType = new Transaction().INCOME;

                if (cashFlowProjected.contains("-")) {
                    cashFlowType = new Transaction().EXPENSE;
                }
                SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
                cashFlowDate = formatter.format(formatDate.parseStringCalendar(cashFlowDate).getTime());

                cashFlowProjected = new FormatDollarAmount().FormatAsDollarWithParenthesis(cashFlowProjected, cashFlowType);
                cashFlowActual = new FormatDollarAmount().FormatAsDollarWithParenthesis(cashFlowActual, cashFlowType);
                if (!cashFlowProjected.equals("$0.00")) {
                    monthlyBudgetRemaining.add(new CashFlow(cashFlowAccount, cashFlowBudget, cashFlowDate, cashFlowBudget, cashFlowProjected,
                            cashFlowActual, cashOnHandStartingProjection, cashOnHandActual, cashOnHandCurrentProjection));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyBudgetRemaining;
    }

    private String monthlyBudgetActual(String budget, String year, String month) {
        String monthlyBudgetActual = null;

        String sql = "CALL moneymakerprocs.viewMonthlyBudgetActualForCashFlow(\"" + userSchema + "\", \"" + budget + "\"," + month + ", " + year + ")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            rs.next();
            monthlyBudgetActual = rs.getObject("Amount", String.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (monthlyBudgetActual == null) {
            monthlyBudgetActual = "0.00";
        }

        return monthlyBudgetActual;
    }

    public ObservableList<CashFlow> recurringTransactions(String year, String month) {
        String day = "1";
        ObservableList<CashFlow> recurringTransactionsCashFlow = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewRecurringTransactionsForCashFlow(\"" + userSchema + "\", " + year + ", " + month + ", " + day + ")";
//        String sql = "CALL moneymakerprocs.viewRecurringTransactionsForCashFlow(?,?,?,?)";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
//                PreparedStatement stmt = conn.prepareStatement(sql);
//                stmt.setString(1, userSchema);
//                stmt.setInt(2, year);
//                stmt.setInt(3, month);
//                stmt.setInt(4, Integer.parseInt(day));

                ) {

            while (rs.next()) {
                String cashFlowActual = "";
                String cashOnHandStartingProjection = "";
                String cashOnHandActual = "";
                String cashOnHandCurrentProjection = "";
                String cashFlowAccount = rs.getObject("Account",String.class);
                String cashFlowBudget = rs.getObject("Budget",String.class);
                String cashFlowCategory = rs.getObject("Category",String.class);
                String cashFlowProjected = rs.getObject("Amount",String.class);

                String startDate = rs.getObject("Start Date", String.class);
                String frequencyDays = rs.getObject("Frequency",String.class);
                String occurrence = rs.getObject("Occurrence",String.class);
                int occurrenceType = rs.getInt("Occurrence Type");
                String transactionType = rs.getObject("Type",String.class);

                cashFlowProjected = new FormatDollarAmount().FormatAsDollarWithParenthesis(cashFlowProjected, transactionType);  // Change formatting of dollar amount

                int selectedYear = Integer.parseInt(year);
                int selectedMonth = Integer.parseInt(month) - 1;
                int selectedDay = Integer.parseInt(day);
                int frequencyDaysInt = Integer.parseInt(frequencyDays);

                Calendar calendarStartDate = formatDate.parseStringCalendar(startDate);  // Convert date string to Calendar Object

                int specificDateDay = calendarStartDate.get(Calendar.DAY_OF_MONTH);  // Day of month of the start date

                Calendar beginningOfMonth = Calendar.getInstance();
                beginningOfMonth.set(selectedYear, selectedMonth, selectedDay);  // First day of selected month

                long numberOfDaysBetweenDates = ChronoUnit.DAYS.between(calendarStartDate.toInstant(), beginningOfMonth.toInstant());  // Calculate the number of days between the start date and the beginning of selected month
                double numberOfOccurrencesForFirstInMonth = Math.floor(numberOfDaysBetweenDates / frequencyDaysInt) + 1; // The number of occurrences necessary to reach the selected month
                long numberOfDaysForFirstOccurrenceInMonth = (long)numberOfOccurrencesForFirstInMonth * frequencyDaysInt;  // The number of days between the first occurrence in the selected month and the start date
                calendarStartDate.add(Calendar.DATE, (int) numberOfDaysForFirstOccurrenceInMonth); // Set start date equal to the first occurrence of the month.

                int monthOfFirstOccurrence = calendarStartDate.get(Calendar.MONTH);  // Month of the first occurrence
                int yearOfFirstOccurrence = calendarStartDate.get(Calendar.YEAR); // Year of the first occurrence
                Calendar currentStartDate = Calendar.getInstance(); // Todays date/Current Start Date

                switch (occurrenceType) {
                    case 1: // Specific Date
                        currentStartDate.set(selectedYear, selectedMonth, specificDateDay);
                        break;
                    case 2: // End of Month
                        Calendar daysInMonth = new GregorianCalendar(selectedYear, selectedMonth, 1);
                        int lastDayOfMonth = daysInMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
                        currentStartDate.set(selectedYear, selectedMonth, lastDayOfMonth);
                        break;
                    case 3: // First of Month
                        currentStartDate.set(selectedYear, selectedMonth, 1);
                        break;
                    case 4: // Specific day of the week
                        currentStartDate = getWeekDayStartDate(calendarStartDate, occurrence);  // Get date of the first occurrence of that day of the week
                        break;
                    case 5: // Rolling date
                        //TODO need formula for this
                }
                SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
                SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
                while(monthOfFirstOccurrence == selectedMonth && yearOfFirstOccurrence == selectedYear) { // Create the necessary occurrences in the selected month

                    String cashFlowDate = formatter.format(currentStartDate.getTime());
                    String actualStartDate = formatterSQL.format(currentStartDate.getTime());

                    Calendar calendarEndDate = currentStartDate;
                    if (frequencyDaysInt == 7 || frequencyDaysInt == 14) {
                        calendarEndDate.add(Calendar.DATE, frequencyDaysInt - 1);
                    } else {
                        calendarEndDate.add(Calendar.DATE, frequencyDaysInt);
                    }

                    String endDate = formatterSQL.format(calendarEndDate.getTime());

                    if (frequencyDays.equals("30") || frequencyDays.equals("60") || frequencyDays.equals("90") || frequencyDays.equals("180")
                            || frequencyDays.equals("270") || frequencyDays.equals("365")) {
                        Calendar newStartDate = new GregorianCalendar(yearOfFirstOccurrence,monthOfFirstOccurrence, 1);
                        actualStartDate = formatterSQL.format(newStartDate.getTime());
                        int lastDayOfMonth = newStartDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                        newStartDate.set(yearOfFirstOccurrence,monthOfFirstOccurrence, lastDayOfMonth);
                        endDate = formatterSQL.format(newStartDate.getTime());
                    }

                    String[] actualAmountAndDate = getActualTransactionAmountAndDate(cashFlowCategory, actualStartDate, endDate, cashFlowDate);
                    if(actualAmountAndDate[0] != null && actualAmountAndDate[1] != null) {
                        cashFlowActual = new FormatDollarAmount().FormatAsDollarWithParenthesis(actualAmountAndDate[0], transactionType);
                        cashFlowDate = actualAmountAndDate[1];
                    }

                    recurringTransactionsCashFlow.add(new CashFlow(cashFlowAccount, cashFlowBudget, cashFlowDate, cashFlowCategory, cashFlowProjected,
                            cashFlowActual, cashOnHandStartingProjection, cashOnHandActual, cashOnHandCurrentProjection));

                    currentStartDate.add(Calendar.DATE, frequencyDaysInt);
                    monthOfFirstOccurrence = currentStartDate.get(Calendar.MONTH);
                    yearOfFirstOccurrence = currentStartDate.get(Calendar.YEAR);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recurringTransactionsCashFlow;
    }

    private Calendar getWeekDayStartDate(Calendar startDate, String occurrence) {
                switch (occurrence) {
                    case "Monday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        break;
                    case "Tuesday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        break;
                    case "Wednesday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        break;
                    case "Thursday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        break;
                    case "Friday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        break;
                    case "Saturday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        break;
                    case "Sunday":
                        startDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        break;
                }
        return startDate;
    }

    private String[] getActualTransactionAmountAndDate(String description, String startDate, String endDate, String projectedDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
        String[] actualAmountAndDate = new String[2];
        String sql = "CALL moneymakerprocs.getActualAmount(\"" + userSchema + "\", \"" + description + "\",\"" + startDate + "\",\"" + endDate + "\")";
        String actualAmount, actualDate;

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                actualAmount = rs.getObject("Amount", String.class);
                actualDate = rs.getObject("Date", String.class);

                if (actualAmount != null && actualDate != null) {
                    Calendar calendarStartDate = formatDate.parseStringCalendar(actualDate);
                    actualDate = formatter.format(calendarStartDate.getTime());
                    actualAmountAndDate[0] = actualAmount;
                    actualAmountAndDate[1] = actualDate;
                } else {
                    actualAmountAndDate[0] = "0.00";
                    actualAmountAndDate[1] = projectedDate;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actualAmountAndDate;
    }
}
