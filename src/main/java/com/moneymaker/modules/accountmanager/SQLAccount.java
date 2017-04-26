package com.moneymaker.modules.accountmanager;

import com.moneymaker.modules.budgetmanager.Budget;
import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created for MoneyMaker by Jay Damon on 8/27/2016.
 */
public class SQLAccount {
    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();
    private String userSchema = new UsernameData().getUserSchema();
    public void addAccount(String accountTypeID, String accountName, String startingBalance,
                           int primary, int inCashFlow) {
        String sql = "CALL moneymakerprocs.addAccount(\"" + userSchema + "\", \"" + accountTypeID + "\",\"" +
                accountName + "\"," + startingBalance + "," + primary + "," + inCashFlow + ")";
        try {
            Statement stmt = conn.createStatement   ();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(String accountType, String accountName, String startingBalance, int primary,
                              int inCashFlow, int accountID) {
        String sql = "CALL moneymakerprocs.updateAccount(\"" + userSchema + "\", \"" + accountType + "\",\"" +
                accountName + "\"," + startingBalance + "," + primary + "," + inCashFlow + "," + accountID + ")";
        try {
            Statement stmt = conn.createStatement   ();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(int accID) {
        String sql = "CALL moneymakerprocs.deleteAccount(\"" + userSchema + "\", " + accID + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> listAccounts() {
        ObservableList<String> accountList = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.listAccounts(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {

                String accType = rs.getObject("acc_name", String.class);

                accountList.add(accType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountList;
    }

    public ArrayList<String> listAccountTypes() {
        ArrayList<String> accountTypeList = new ArrayList<>();
        String sql = "CALL moneymakerprocs.listAccountTypes(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {

                String account = rs.getObject("acctype_name", String.class);

                accountTypeList.add(account);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountTypeList;
    }

    public ObservableList<Account> viewAccounts() {

        ObservableList<Account> data = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewAccounts(\"" + userSchema + "\")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            while (rs.next()) {
                String accID = rs.getObject("accID", String.class);
                String name = rs.getObject("acc_name", String.class);
                String type = rs.getObject("acctype_name", String.class);
                String balance = rs.getObject("acc_start_balance", String.class);
                String isPrimaryAccount = rs.getObject("isPrimary", String.class);
                String inCashFlow = rs.getObject("isInCashFlow", String.class);
                String dollarType;

                if (Float.parseFloat(balance) < 0 ) {
                    dollarType = new Transaction().EXPENSE;
                } else {
                    dollarType = new Transaction().INCOME;
                }

                balance = new FormatDollarAmount().FormatAsDollarWithParenthesis(balance, dollarType);

                Calendar todaysDate = Calendar.getInstance();
                SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
                String endDate = formatterSQL.format(todaysDate.getTime());
                BigDecimal bigDecimalCurrentBalance = viewAccountBalance(endDate, name);
                int bigDecimalComparison = bigDecimalCurrentBalance.compareTo(BigDecimal.ZERO);
                String currentBalanceType = null;
                switch (bigDecimalComparison) {
                    case 0:
                        currentBalanceType = new Transaction().INCOME;
                        break;
                    case 1:
                        currentBalanceType = new Transaction().INCOME;
                        break;
                    case -1:
                        currentBalanceType = new Transaction().EXPENSE;
                        break;
                }
                String currentBalance = bigDecimalCurrentBalance.toString();
                currentBalance = new FormatDollarAmount().FormatAsDollarWithParenthesis(currentBalance, currentBalanceType);

                if (Integer.parseInt(isPrimaryAccount) == 1) {
                    name = "* " + name;
                }

                data.add(new Account(accID, name, type, balance, currentBalance, isPrimaryAccount, inCashFlow));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public BigDecimal viewPrimaryAccountBalance(String endDate) {

        BigDecimal accountBalance = BigDecimal.ZERO;

        String sql = "CALL moneymakerprocs.viewPrimaryAccountBalance(\"" + userSchema + "\", \"" + endDate + "\",\"" +
                new Budget().TRANSFER + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            rs.beforeFirst();
            while (rs.next()) {

                String accountClassification = rs.getObject("Account Classification", String.class);
                BigDecimal transactionTotal = rs.getBigDecimal("Transaction Total");
                BigDecimal startingBalance = rs.getBigDecimal("Starting Balance");

                accountBalance = accountBalanceCalculation(accountClassification, transactionTotal, startingBalance, sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountBalance;

    }

    private BigDecimal viewAccountBalance(String endDate, String accountName) {
        BigDecimal accountBalance = BigDecimal.ZERO;

        String sql = "CALL moneymakerprocs.viewAccountBalance(\"" +
                userSchema + "\", \"" + endDate + "\",\"" + accountName + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            rs.beforeFirst();
            while (rs.next()) {
                String accountClassification = rs.getObject("Account Classification", String.class);
                BigDecimal transactionTotal = rs.getBigDecimal("Transaction Total");
                BigDecimal startingBalance = rs.getBigDecimal("Starting Balance");
                if (accountClassification != null && transactionTotal != null && startingBalance != null) {
                    accountBalance = accountBalanceCalculation(accountClassification, transactionTotal, startingBalance, sql);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountBalance;
    }

    private BigDecimal accountBalanceCalculation(
            String accountClassification, BigDecimal transactionTotal, BigDecimal startingBalance, String sql) {

        BigDecimal accountBalance = BigDecimal.ZERO;

        transactionTotal = transactionTotal == null ? BigDecimal.ZERO : transactionTotal;
        startingBalance = startingBalance == null ? BigDecimal.ZERO : startingBalance;

        switch (accountClassification) {
            case "Credit Card":
                accountBalance = startingBalance.subtract(transactionTotal);
                break;
            case "Asset":
                try {
                    accountBalance = startingBalance.add(transactionTotal);
                } catch (NullPointerException e) {
                    System.out.println("Transaction Total: " + transactionTotal);
                    System.out.println("Account Classification: " + accountClassification);
                    System.out.println("Starting Balance: " + startingBalance);
                    System.out.println(sql);
                }
                break;
            case "Liability":
                accountBalance = startingBalance.subtract(transactionTotal);
                break;
            case "Equity":
                accountBalance = startingBalance.add(transactionTotal);
                break;
        }

        return accountBalance;
    }

}
