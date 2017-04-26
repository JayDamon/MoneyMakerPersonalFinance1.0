package com.moneymaker.modules.transfermanager;


import com.moneymaker.modules.budgetmanager.Budget;
import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.main.UsernameData;
import com.moneymaker.modules.transactionmanager.transactions.SQLTransaction;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.utilities.FormatDate;
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
 * Created for MoneyMaker by Jay Damon on 10/20/20.
 */
public class SQLTransfer {
    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();
    private Format format = NumberFormat.getCurrencyInstance();
    private FormatDate formatDate = new FormatDate();
    private SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
    private String userSchema = new UsernameData().getUserSchema();

    public ObservableList<Transfer> viewTransfers() {
        ObservableList<Transfer> transfers = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewTransfers(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            while (rs.next()) {
                String transferID = rs.getObject("transferID", String.class);
                String transferDate = rs.getObject("transferDate", String.class);
                String transactionCategory = rs.getObject("transactionCategory", String.class);
                String fromAccountName = rs.getObject("fromAccountName", String.class);
                String toAccountName = rs.getObject("toAccountName", String.class);
                BigDecimal transferAmount = rs.getBigDecimal("transferAmount").setScale(2,RoundingMode.CEILING);
                String fromTransactionID = rs.getObject("fromTransactionID", String.class);
                String toTransactionID = rs.getObject("toTransactionID", String.class);

                if (transferID != null) {
                    Calendar calendarTransferDate = formatDate.parseStringCalendar(transferDate);
                    transferDate = formatter.format(calendarTransferDate.getTime());
                }

                transfers.add(new Transfer(transferID, transferDate, transactionCategory, fromAccountName,
                        toAccountName, format.format(transferAmount), fromTransactionID, toTransactionID));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transfers;
    }

    public ObservableList<Transaction> viewTransferTransactions(String accountName) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewTransferTransactions(\"" + userSchema + "\", \"" + new Budget().TRANSFER + "\",\"" + accountName + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            rs.beforeFirst();

            while (rs.next()) {
                String transactionID = rs.getObject("tranID", String.class);
                String transactionAcc = "";
                String transactionBudget = "";
                String transactionCategory = "";
                String transactionRecurring = "";
                String transactionDate = rs.getObject("tran_date", String.class);
                String transactionDesc = rs.getObject("tran_description", String.class);
                BigDecimal transactionAmount = rs.getBigDecimal("tran_amount").setScale(2, RoundingMode.CEILING);
                String transactionTimeStamp = "";

                if (!transactionDate.equals("")) {
                    Calendar calendarTransactionDate = formatDate.parseStringCalendar(transactionDate);
                    transactionDate = formatter.format(calendarTransactionDate.getTime());
                }

                transactions.add(new Transaction(transactionID, transactionAcc, transactionBudget,
                        transactionCategory, transactionRecurring, transactionDate, transactionDesc, format.format(transactionAmount), transactionTimeStamp));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public void deleteTransfer(int transferID) {
        String sql = "CALL moneymakerprocs.getTransferData(\"" + userSchema + "\", " + transferID + ")";
        String deleteSQL = "CALL moneymakerprocs.deleteTransfer(\"" + userSchema + "\", " + transferID + ")";
        SQLTransaction sqlTransaction = new SQLTransaction();

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            rs.beforeFirst();

            if (rs.next()) {
                int fromTransactionID = rs.getInt("fromTransactionID");
                int toTransactionID = rs.getInt("toTransactionID");

                sqlTransaction.deleteTransaction(fromTransactionID);
                sqlTransaction.deleteTransaction(toTransactionID);

            }
            stmt.executeQuery(deleteSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTransferTransactionID(String account, String date, String description, String amount) {
        String sql = "CALL moneymakerprocs.getTransferTransactionID(\"" + userSchema + "\", \"" + account + "\",\"" + date + "\",\"" + description + "\"," + amount + ")";
        int transactionID = -1;
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            if (rs.next()) {
                transactionID = rs.getInt("transactionID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionID;
    }

    public void newTransfer(String date, String fromTransaction, String toTransaction, String amount) {
        String sql = "CALL moneymakerprocs.addTransfer(\"" + userSchema + "\", \"" + date + "\"," + fromTransaction + "," + toTransaction + "," + amount + ")";
        try {
                Statement stmt = conn.createStatement();
                stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public void updateTransfer(String transferID, String date, String fromTransactionID, String toTransactionID, String amount) {
        String sql = "CALL moneymakerprocs.updateTransfer(\"" + userSchema + "\", " + transferID + ",\"" + date + "\"," + fromTransactionID + "," + toTransactionID + "," + amount + ")";

        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String[] getTransferTransaction(String transactionID) {
        String sql = "CALL moneymakerprocs.getTransferTransaction(\"" + userSchema + "\", " + transactionID + ")";
        String[] transferTransaction = new String[4];

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
                rs.beforeFirst();
                if (rs.next()) {

                    String transferTransactionDate = rs.getObject("transactionDate", String.class);
                    Calendar calendarTransactionDate = formatDate.parseStringCalendar(transferTransactionDate);
                    transferTransactionDate = formatter.format(calendarTransactionDate.getTime());
                    BigDecimal transferTransactionAmount = rs.getBigDecimal("transactionAmount").setScale(2, RoundingMode.CEILING);
                    NumberFormat fmt = NumberFormat.getCurrencyInstance();

                    transferTransaction[0] = rs.getObject("transactionID", String.class);
                    transferTransaction[1] = transferTransactionDate;
                    transferTransaction[2] = rs.getObject("transactionDescription", String.class);
                    transferTransaction[3] = fmt.format(transferTransactionAmount);
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transferTransaction;
    }

    public ObservableList<String> listTransferCategories() {
        ObservableList<String> transactionCategories = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.listTransferCategories(\"" + userSchema + "\", \"" + new Budget().TRANSFER + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            rs.beforeFirst();

            while (rs.next()) {
                transactionCategories.add(rs.getObject("trancat_name", String.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionCategories;
    }
}
