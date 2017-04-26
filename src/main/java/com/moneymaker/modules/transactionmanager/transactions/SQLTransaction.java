package com.moneymaker.modules.transactionmanager.transactions;

import com.moneymaker.modules.transactionmanager.recurringtransactions.RecurringTransaction;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created for MoneyMaker by Jay Damon on 8/27/2016.
 */
public class SQLTransaction {
    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();
    private String userSchema = new UsernameData().getUserSchema();

    private FormatDollarAmount formatDollarAmount = new FormatDollarAmount();
    private FormatDate formatDate = new FormatDate();
    private SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
    private SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);

    public ObservableList<Transaction> viewTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewTransactions(\"" + userSchema + "\")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                String transactionID = rs.getObject("ID", String.class);
                String transactionAccount = rs.getObject("Account Name", String.class);
                String transactionBudget = rs.getObject("Budget Name", String.class);
                String transactionCategory = rs.getObject("Category", String.class);
                String transactionRecurring = rs.getObject("Recurring Transaction", String.class);
                String transactionDate = rs.getObject("Date", String.class);
                String transactionDescription = rs.getObject("Description", String.class);
                BigDecimal transactionAmount = rs.getBigDecimal("Amount").setScale(2, RoundingMode.CEILING);
                String transactionTimeStamp = "";

                NumberFormat fmt = NumberFormat.getCurrencyInstance();

                if (transactionDate != null) {
                    Calendar calendarTransactionDate = formatDate.parseStringCalendar(transactionDate);
                    transactionDate = formatter.format(calendarTransactionDate.getTime());
                }

//                if (transactionDescription.contains("  ")) {
                    transactionDescription = transactionDescription.replaceAll("\\s+","");
//                }

                transactions.add(new Transaction(transactionID, transactionAccount, transactionBudget, transactionCategory,
                        transactionRecurring, transactionDate, transactionDescription, fmt.format(transactionAmount), transactionTimeStamp));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void newTransaction(String tranAcc, String tranBudget, String tranCat, String tranDate, String tranDesc,
                               String tranAmount) {

        String sql = "CALL moneymakerprocs.addTransaction(\"" + userSchema + "\", \"" + tranAcc + "\",\"" + tranBudget + "\", \"" + tranCat + "\" , \"" + tranDate + "\", \"" +
                tranDesc + "\", " + tranAmount + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(int tranID) {
        String sql = "CALL moneymakerprocs.deleteTransaction(\"" + userSchema + "\", " + tranID + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Transaction> findIfDuplicateTransaction(String tranDate, String tranDesc, String tranAmount) {
        ObservableList<Transaction> transaction = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.findDuplicateTransactions(\"" + userSchema + "\", \"" + tranDate + "\", \"" + tranDesc + "\", " + tranAmount + ")";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            transaction.add(new Transaction("ID","Account","Budget","Category", "Recurring", tranDate, tranDesc, tranAmount, "Right Now"));
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {
                    String transactionDate = rs.getObject("tran_date", String.class);
                    String transactionDesc = rs.getObject("tran_description", String.class);
                    BigDecimal transactionAmount = rs.getBigDecimal("tran_amount").setScale(2,RoundingMode.CEILING);
                    String transactionTimeAdded = rs.getObject("tran_time_added", String.class);

                    Format format = NumberFormat.getCurrencyInstance();

                    transaction.add(new Transaction("ID", "Account", "Budget", "Category", "Recurring",  transactionDate, transactionDesc, format.format(transactionAmount), transactionTimeAdded));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (transaction.size() == 1) {
                transaction = null;
            }
        return transaction;
    }

    public ObservableList<String> listTransactionCategory(String budgetCategory) {
        ObservableList<String> transactionCategories = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.listTransactionCategory(\"" + userSchema + "\", \"" + budgetCategory + "\")";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs =  stmt.executeQuery(sql);
            while (rs.next()) {
                String transactionCategory = rs.getObject("trancat_name", String.class);
                transactionCategories.add(transactionCategory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionCategories;
    }

    public ObservableList<Transaction> viewUncategorizedTransactions(String budgetID) {
        ObservableList<Transaction> uncategorizedTransactions = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.viewUncategorizedTransactions(\"" + userSchema + "\", " + budgetID + ")";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String transactionID = rs.getObject("tranID", String.class);
                String transactionAcc = rs.getObject("acc_name", String.class);
                String transactionBudget = rs.getObject("bud_name", String.class);
                String transactionCategory = rs.getObject("trancat_name", String.class);
                String transactionRecurring = rs.getObject("recName", String.class);
                String transactionDate = rs.getObject("tran_date", String.class);
                String transactionDesc = rs.getObject("tran_description", String.class);
                BigDecimal transactionAmount = rs.getBigDecimal("tran_amount").setScale(2, RoundingMode.CEILING);
                String transactionTimeStamp = rs.getObject("tran_time_added", String.class);

                Format format = NumberFormat.getCurrencyInstance();

                if (!transactionDate.equals("")) {
                    Calendar calendarTransactionDate = formatDate.parseStringCalendar(transactionDate);
                    transactionDate = formatter.format(calendarTransactionDate.getTime());
                }

                uncategorizedTransactions.add(new Transaction(transactionID, transactionAcc, transactionBudget,
                        transactionCategory, transactionRecurring, transactionDate, transactionDesc, format.format(transactionAmount), transactionTimeStamp));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uncategorizedTransactions;
    }

    public void updateTransactionAddCategory(String transactionCategory, String transactionID, String budgetID) {
        String sql = "CALL moneymakerprocs.updateTransactionAddCategory(\"" + userSchema + "\", \"" + transactionCategory + "\"," + transactionID + ", " + budgetID + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTransaction(String transactionID, String transactionDescription, String accountName, String budgetName,
                                  String categoryName, String recurringTransaction, String transactionDate, String amount) {

        if(!transactionDate.equals("")) {
            Calendar calendarTransactionDate = formatDate.parseStringCalendar(transactionDate);
            transactionDate = formatterSQL.format(calendarTransactionDate.getTime());
        }


        if(!recurringTransaction.equals("NULL")) {
            recurringTransaction = "\"" + recurringTransaction + "\"";
        }

        amount = formatDollarAmount.CleanDollarAmountsForSQL(amount);

        String sql;
        String budgetNameInput = budgetName;

        if (!budgetName.equals("NULL")) {
            budgetNameInput = "\"" + budgetName + "\"";
        }
        if (budgetName.equals("Goal")) {
            sql = "CALL moneymakerprocs.updateTransactionWithGoal(\"" + userSchema + "\", " + transactionID + ",\"" +
                    transactionDescription + "\", \"" + accountName + "\", " + budgetNameInput + ", \"" +
                    categoryName + "\", " + recurringTransaction + ", \"" + transactionDate + "\", " + amount + ")";
        } else {
            sql = "CALL moneymakerprocs.updateTransaction(\"" + userSchema + "\", " + transactionID + ",\"" +
                    transactionDescription + "\", \"" + accountName + "\", " + budgetNameInput + ", \"" +
                    categoryName + "\", " + recurringTransaction + ", \"" + transactionDate + "\", " + amount + ")";
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(recurringTransaction.equals("Not Recurring") || recurringTransaction.equals("NULL")) {
            setTransactionFieldRecurringToNull(transactionID);
        }
    }

    private void setTransactionFieldRecurringToNull(String transactionID) {
        String sql = "CALL moneymakerprocs.setTransactionFieldRecurringToNull(\"" + userSchema + "\", " + transactionID + ")";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTransactionCategory(String transactionID, String budgetName, String transactionCategory) {
        String sql = "CALL moneymakerprocs.updateTransactionCategory(\"" + userSchema + "\", " + transactionID + ",\"" + budgetName + "\",\"" + transactionCategory + "\")";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<RecurringTransaction> viewRecurringTransactions() {
        ObservableList<RecurringTransaction> recurringTransactions = FXCollections.observableArrayList();


        String sql = "CALL moneymakerprocs.viewRecurringTransactions(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while(rs.next()) {
                String recurringTransactionID = rs.getObject("recID", String.class);
                String recurringTransactionName = rs.getObject("recName", String.class);
                String recurringTransactionAccount = rs.getObject("acc_name", String.class);
                String recurringTransactionBudget = rs.getObject("budgetName", String.class);
                String recurringTransactionFrequency = rs.getObject("freqtype_name", String.class);
                String recurringTransactionOccurrence = rs.getObject("occtype_name", String.class);
                String recurringTransactionType = rs.getObject("tran_type", String.class);
                String recurringTransactionStartDate = rs.getObject("start_date", String.class);
                String recurringTransactionEndDate = rs.getObject("end_date", String.class);
                String recurringTransactionAmount = rs.getObject("amount", String.class);

                FormatDollarAmount formatDollarAmount = new FormatDollarAmount();
                recurringTransactionAmount = formatDollarAmount.FormatAsDollarWithParenthesis(recurringTransactionAmount, recurringTransactionType);

                if (recurringTransactionStartDate != null) {
                    Calendar calendarRecurringTransactionStartDate = formatDate.parseStringCalendar(recurringTransactionStartDate);
                    recurringTransactionStartDate = formatter.format(calendarRecurringTransactionStartDate.getTime());
                }

                if (recurringTransactionEndDate != null) {
                    Calendar calendarRecurringTransactionEndDate = formatDate.parseStringCalendar(recurringTransactionEndDate);
                    recurringTransactionEndDate = formatter.format(calendarRecurringTransactionEndDate.getTime());
                }

                recurringTransactions.add(new RecurringTransaction(recurringTransactionID, recurringTransactionName,
                        recurringTransactionAccount, recurringTransactionBudget, recurringTransactionFrequency,
                        recurringTransactionOccurrence, recurringTransactionType, recurringTransactionStartDate,
                        recurringTransactionEndDate, recurringTransactionAmount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recurringTransactions;
    }

    public ObservableList<String> listRecurringTransactions() {
        ObservableList<String> recurringTransactions = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.listRecurringTransactions(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String recurringTransactionName = rs.getObject("recName", String.class);

                recurringTransactions.add(recurringTransactionName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recurringTransactions;
    }

    public void addRecurringTransaction(String transactionName, String accountName, String budgetName, String frequencyType,
                                        String occurrence, String type, String startDate, String endDate, String amount) {
        String endDateFinal = endDate;
        if (!endDate.equals("NULL")) {
            endDateFinal = "\"" + endDateFinal + "\"";
        }

        if (type.equals("Expense")) {
            amount = "-" + amount;
        }

//        if (!budgetName.equals("NULL")) {
//            budgetName = "\"" + budgetName + "\"";
//        }

        String sql = "CALL moneymakerprocs.addRecurringTransaction(\"" + userSchema + "\", \"" + transactionName + "\", \"" + accountName + "\", \"" +
                budgetName + "\", \"" + frequencyType + "\", \"" + occurrence + "\", \"" + type + "\"," +
                " \"" + startDate + "\", " + endDateFinal + ", " + amount + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRecurringTransaction(String transactionID, String transactionName, String accountName,
                                           String budgetName, String frequencyType, String occurrence, String type,
                                           String startDate, String endDate, String amount) {
        String endDateFinal = endDate;
        if (!endDate.equals("NULL")) {
            endDateFinal = "\"" + endDateFinal + "\"";
        }

        if (!budgetName.equals("NULL")) {
            budgetName = "\"" + budgetName + "\"";
        }

        if(type.equals("Expense")) {
            amount = "-" + amount;
        }

        amount = formatDollarAmount.CleanDollarAmountsForSQL(amount);

        String sql = "CALL moneymakerprocs.updateRecurringTransaction(\"" + userSchema + "\", " + transactionID + ",\"" + transactionName + "\", \"" + accountName + "\", " +
                budgetName + ", \"" + frequencyType + "\", \"" + occurrence + "\", \"" + type + "\"," +
                " \"" + startDate + "\", " + endDateFinal + ", " + amount + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecurringTransaction(String recurringTransactionID) {
        String sql = "CALL moneymakerprocs.deleteRecurringTransaction(\"" + userSchema + "\", " + recurringTransactionID + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
