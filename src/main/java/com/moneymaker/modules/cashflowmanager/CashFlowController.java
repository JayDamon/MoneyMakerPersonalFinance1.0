package com.moneymaker.modules.cashflowmanager;

import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.modules.accountmanager.SQLAccount;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.net.URL;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Cash Flow created by Jay Damon on 9/19/2016.
 */
public class CashFlowController implements Initializable {

    @FXML
    TableView<CashFlow> tblCashFlow;

    @FXML
    ListView<String> listViewMonths;
    @FXML
    ListView<Integer> listViewYears;

    private SQLCashFlow sqlCashFlow = new SQLCashFlow();

    public void initialize(URL url, ResourceBundle rs) {
        Calendar today = Calendar.getInstance();
        int thisYearInt = today.get(Calendar.YEAR);
        String thisYear = String.valueOf(thisYearInt);
        String thisMonth = new DateFormatSymbols().getMonths()[today.get(Calendar.MONTH)];
        String thisMonthInt = String.valueOf(today.get(Calendar.MONTH)+1);
        String day = "1";
        showCashFlow(thisYear, thisMonthInt, day);
        addMonths(thisMonth);
        addYears(thisYear);

        listViewMonths.setOnMouseClicked(event -> updateVisibleCashFlow());
        listViewYears.setOnMouseClicked(event -> updateVisibleCashFlow());
    }

    private void updateVisibleCashFlow() {
        int selectedInt = listViewMonths.getSelectionModel().getSelectedIndex();
        int selectedYearInt = listViewYears.getSelectionModel().getSelectedIndex();
        if (selectedInt != -1 && selectedYearInt != -1) {
            String selectedMonth = listViewMonths.getSelectionModel().getSelectedItem();
            String selectedYear = String.valueOf(listViewYears.getSelectionModel().getSelectedItem());
            String selectedDay = "1";
            Date selectedDate = new Date();
            try {
                selectedDate = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(selectedMonth);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            String selectedMonthInt = String.valueOf(cal.get(Calendar.MONTH) + 1);

            showCashFlow(selectedYear, selectedMonthInt, selectedDay);
        }
    }

    private void showCashFlow(String displayYear, String displayMonthNumber, String displayDay) {

        DateFormat f = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);

        int intDisplayYear = Integer.parseInt(displayYear);
        int intDisplayMonth = Integer.parseInt(displayMonthNumber);

        Float floatCashOnHandStarting = 0f;
        Float floatCashOnHandActual;
        Float floatCashOnHandCurrent = 0f;
        String cashOnHandStarting = null;
        String cashOnHandActual;
        String cashOnHandCurrent = null;
        int currentTracker = 0;

        ObservableList<CashFlow> cashFlow = tblCashFlow.getItems();
        cashFlow.clear();

    //Pull full list budget items that are active in the time.  The amounts viewed subtract the amount of any recurring transactions
        cashFlow.addAll(sqlCashFlow.viewMonthlyBudgetRemaining(displayYear, displayMonthNumber, displayDay));

    //Pull full list of Recurring transactions and budgeted amounts that are active in the given month and year
        cashFlow.addAll(sqlCashFlow.recurringTransactions(displayYear, displayMonthNumber));

    //Sort cash Flow list which is the combined list of budget items and recurring transactions
        Collections.sort(cashFlow, (o1, o2) -> {
            try {
                return f.parse(o1.getCashFlowDate()).compareTo(f.parse(o2.getCashFlowDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        });
        int cIndex = 0;

    //Get balance on last day of previous time period (ptp)
        if (intDisplayMonth == 0) {
            intDisplayMonth = 11;
            intDisplayYear -= 1;
        } else {
            intDisplayMonth -= 1;
        }
        Calendar ptpDaysInMonth = new GregorianCalendar(intDisplayYear, intDisplayMonth, 1);
        int ptpLastDayOfMonth = ptpDaysInMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar ptpFinalDate = Calendar.getInstance();
        ptpFinalDate.set(intDisplayYear, intDisplayMonth, ptpLastDayOfMonth);
        SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
        String ptpFinalDateString = formatterSQL.format(ptpFinalDate.getTime());
        SQLAccount sqlAccount = new SQLAccount();
        floatCashOnHandActual = Float.parseFloat(sqlAccount.viewPrimaryAccountBalance(ptpFinalDateString).toString());

    //Loop through Cash Flow list
        for (CashFlow c: cashFlow) { //ToDo not splitting out each income entry, instead it is summing the two
            FormatDollarAmount formatDollarAmount = new FormatDollarAmount();

            Calendar currentDate = Calendar.getInstance();  //Today's Date
            Calendar cashFlowDate = Calendar.getInstance();  //Used to store date of this Cash Flow item
            try {
                cashFlowDate.setTime(f.parse(c.getCashFlowDate()));
            } catch (ParseException e) {
                //ToDo Add error logger
                e.printStackTrace();
            }

        //Compare the Cash Flow date to find out if the Cash Flow Date is before or after today's date
            int dateComparison = currentDate.getTime().compareTo(cashFlowDate.getTime()); // 0 is equal to, 1 already happened, -1 hasn't yet happened

        //The end date will be the last end date for this Cash Flow Item based on its frequency
            Calendar calendarEndDate = Calendar.getInstance();
            int selectedYear = cashFlowDate.get(Calendar.YEAR);
            int selectedMonth = cashFlowDate.get(Calendar.MONTH)-1;
            Calendar daysInMonth = new GregorianCalendar(selectedYear, selectedMonth, 1);  //Get number of days in a month
            int lastDayOfMonth = daysInMonth.getActualMaximum(Calendar.DAY_OF_MONTH);  //Get last day of month
            calendarEndDate.set(selectedYear, selectedMonth, lastDayOfMonth);  //Set end date as last day of month

            cashOnHandActual = floatCashOnHandActual.toString(); //Create string version of cash on hand total
            if (dateComparison == 0 || dateComparison == -1) {
                switch (currentTracker) {
                    case 0:
                        if (cIndex == 0) {
                            floatCashOnHandStarting = floatCashOnHandStarting + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                            floatCashOnHandActual = floatCashOnHandActual + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowActual()));
                            cashOnHandStarting = floatCashOnHandStarting.toString();
                            cashOnHandCurrent = floatCashOnHandActual.toString();
                            cashOnHandActual = null;
                        } else {
                            floatCashOnHandActual = floatCashOnHandActual + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowActual()));
                            floatCashOnHandStarting = floatCashOnHandStarting + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                            cashOnHandActual = floatCashOnHandActual.toString();
                            cashOnHandStarting = floatCashOnHandStarting.toString();
                            cashOnHandCurrent = null;
                        }
                        currentTracker++;
                        break;
                    case 1:
                        floatCashOnHandCurrent = floatCashOnHandActual + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                        floatCashOnHandStarting = floatCashOnHandStarting + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                        cashOnHandCurrent = floatCashOnHandCurrent.toString();
                        cashOnHandStarting = floatCashOnHandStarting.toString();
                        cashOnHandActual = null;
                        currentTracker++;
                        break;

                    case 2:
                        floatCashOnHandCurrent = floatCashOnHandCurrent + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                        floatCashOnHandStarting = floatCashOnHandStarting + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                        cashOnHandCurrent = floatCashOnHandCurrent.toString();
                        cashOnHandStarting = floatCashOnHandStarting.toString();
                        cashOnHandActual = null;
                        break;
                }

            } else if (dateComparison == 1) {
                floatCashOnHandStarting = floatCashOnHandStarting + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowProjected()));
                floatCashOnHandActual = floatCashOnHandActual + Float.parseFloat(formatDollarAmount.CleanDollarAmountsForSQL(c.getCashFlowActual()));
                cashOnHandStarting = floatCashOnHandStarting.toString();
                cashOnHandActual = floatCashOnHandActual.toString();
                cashOnHandCurrent = null;

            }

            if (cashOnHandCurrent != null) {
                if (floatCashOnHandCurrent < 0) {
                    c.setCashOnHandCurrentProjection(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandCurrent, new Transaction().EXPENSE));
                } else {
                    c.setCashOnHandCurrentProjection(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandCurrent, new Transaction().INCOME));
                }
            }

            if (cashOnHandStarting!= null) {
                if (floatCashOnHandStarting < 0) {
                    c.setCashOnHandStartingProjection(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandStarting, new Transaction().EXPENSE));
                } else {
                    c.setCashOnHandStartingProjection(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandStarting, new Transaction().INCOME));
                }
            }

            if (cashOnHandActual != null) {
                if (floatCashOnHandActual < 0) {
                    c.setCashOnHandActual(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandActual, new Transaction().EXPENSE));
                } else {
                    c.setCashOnHandActual(formatDollarAmount.FormatAsDollarWithParenthesis(
                            cashOnHandActual, new Transaction().INCOME));
                }
            }

            // ToDo how do i handle accounts.  Currently it pulls Capital One Balance, but i probably want to pull by checking with capital one accounted for

            cIndex++;
        }

    }

    private void addMonths(String thisMonth) {
        ObservableList<String> monthsList = FXCollections.observableArrayList();

        String[] months = new DateFormatSymbols().getMonths();
        Collections.addAll(monthsList, months);

        listViewMonths.getItems().clear();
        listViewMonths.setItems(monthsList);
        int count = 0;
        for (Object o : listViewMonths.getItems()) {
            if (o.equals(thisMonth)) {
                listViewMonths.getSelectionModel().select(count);
                listViewMonths.scrollTo(count);
                break;
            }
            count++;
        }
    }

    private void addYears(String thisYear) {
        ObservableList<Integer> yearList = FXCollections.observableArrayList();

        for (int i = 1980; i < 2050; i++) {
            yearList.add(i);
        }

        listViewYears.setItems(yearList);
        int count = 0;
        for(Integer o : listViewYears.getItems()) {
            if (o.toString().equals(thisYear)) {
                listViewYears.getSelectionModel().select(count);
                listViewYears.scrollTo(count);
                break;
            }
            count++;
        }
    }
}
