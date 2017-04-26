package com.moneymaker.modules.budgetmanager;

import com.moneymaker.modules.goalmanager.Goal;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.modules.goalmanager.SQLGoal;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import com.moneymaker.utilities.ParseAndSplitDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created for MoneyMaker by Jay Damon on 8/27/2016.
 */
public class SQLBudget {
    private Connection conn = ConnectionManagerUser.getInstance().getConnection();

    private FormatDate formatDate = new FormatDate();
    private SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
    private SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
    private String userSchema = new UsernameData().getUserSchema();

    public void updateBudget(String budID, String budStart, String budEnd, String budFreq, String budAmount) {

        String budStartFinal = budEnd;
        String budEndFinal = budEnd;

        if (!budStart.equals("NULL")) {
            Calendar calStart = formatDate.parseStringCalendar(budStart);
            budStart = formatterSQL.format(calStart.getTime());
            budStartFinal = "\"" + budStart + "\"";
        }

        if (!budEnd.equals("NULL")) {
            Calendar calEnd = formatDate.parseStringCalendar(budEnd);
            budEnd = formatterSQL.format(calEnd.getTime());
            budEndFinal = "\"" + budEnd + "\"";
        }
        System.out.println(budAmount);
        if (budAmount.equals("")) {
            budAmount = "0.00";
        }

        String sql = "CALL moneymakerprocs.updateBudget(\"" + userSchema + "\", " + budID + ", " + budStartFinal + ", " + budEndFinal + ", \"" + budFreq +"\" , " + budAmount +")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBudget(int budGeneric, String budName) {

        String sql = null;
        try {
            Statement stmt = conn.createStatement();
            switch (budGeneric) {
                case 0:
                    JOptionPane.showMessageDialog(null, "This Budget already exists");
                    return;
                case 1:
                    sql = "CALL moneymakerprocs.activateBudget(\"" + userSchema + "\", \"" + budName + "\")";
                    break;
                case 2:
                    sql = "CALL moneymakerprocs.addBudgetName(\"" + userSchema + "\", \"" + budName + "\")";
                    break;
            }
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBudget(int budGeneric, String budName) {
        String sql = null;
        switch (budGeneric) {
            case 0:
                sql = "CALL moneymakerprocs.deleteBudget(\"" + userSchema + "\", \"" + budName + "\")";
                break;
            case 1:
                sql = "CALL moneymakerprocs.resetGenericBudget(\"" + userSchema + "\", \"" + budName + "\")";

        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBudgetGeneric(String budgetName) {
        String sql = "CALL moneymakerprocs.getBudgetGeneric(\"" + userSchema + "\", \"" + budgetName + "\")";
        int budGen = 0;
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            budGen = 2;

            if (rs.next()) {

                budGen = rs.getInt(1);
            } else if (!rs.next()) {
                budGen = 2;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budGen;

    }

    public ObservableList<String> listActiveBudget() {
        ObservableList<String> budgetList = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.listActiveBudget(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {
                String budgetName = rs.getObject("bud_name", String.class);
                    budgetList.add(budgetName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgetList;
    }

    public ObservableList<String> inactiveBudgets() {
        ObservableList<String> inactiveBudgets = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.listInactiveBudget(\"" + userSchema + "\");";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {
                String budgetName = rs.getObject("bud_name", String.class);
                inactiveBudgets.add(budgetName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inactiveBudgets;
    }

    public ObservableList<Budget> viewBudget() {

        ObservableList<Budget> viewBudget = FXCollections.observableArrayList();

        String sql = "CALL moneymakerprocs.viewBudget(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            while (rs.next()) {
                String budID = rs.getObject("budID", String.class);
                String budName = rs.getObject("bud_name", String.class);
                String startDate = rs.getObject("bud_start_date", String.class);
                String endDate = rs.getObject("bud_end_date", String.class);
                String frequency = rs.getObject("freqtype_name", String.class);
                String stringAmount = rs.getObject("bud_amount", String.class);
                String uncategorizedTransaction = rs.getObject("uncat_transaction", String.class);

                String amount = "";
//                if (!budName.equals("Transfer")) {
                    if (budName == null) {
                        budName = "";
                    } else if (budName.equals("Goal")) {
                        stringAmount = sumGoalAmount();
                    }
                    if (startDate == null) {
                        startDate = "";
                    }
                    if (endDate == null) {
                        endDate = "";
                    }
                    if (frequency == null) {
                        frequency = "";
                    }
                    if (stringAmount != null) {
                        NumberFormat fmt = NumberFormat.getCurrencyInstance();
                        Float amountConversion = Float.parseFloat(stringAmount);
                        BigDecimal bigDecimalAmount = BigDecimal.valueOf(amountConversion);
                        amount = fmt.format(bigDecimalAmount);
                    }

                    if (!startDate.equals("")) {
                        Calendar calStartDate = formatDate.parseStringCalendar(startDate);
                        startDate = formatter.format(calStartDate.getTime());
                    }

                    if (!endDate.equals("")) {
                        Calendar calEndDate = formatDate.parseStringCalendar(endDate);
                        endDate = formatter.format(calEndDate.getTime());
                    }

                    viewBudget.add(new Budget(budID, budName, startDate, endDate, frequency, amount, uncategorizedTransaction));
//                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return viewBudget;
    }

    private String sumGoalAmount() {

        FormatDate formatDate = new FormatDate();

        Float floatGoals = 0f;

        SQLGoal sqlGoal = new SQLGoal();
        ObservableList<Goal> goals = sqlGoal.viewGoals();

        for (Goal g: goals) {

            String goalStartDate = g.getGoalStartDate();
            String goalEndDate = g.getGoalEndDate();

            String startDate = formatDate.formatFromTableDate(goalStartDate);
            String endDate = formatDate.formatFromTableDate(goalEndDate);
            Float goalAmount = Float.parseFloat(new FormatDollarAmount().CleanDollarAmountsForSQL(g.getGoalAmount()));

            ParseAndSplitDate splitStartDate = new ParseAndSplitDate(startDate);
            ParseAndSplitDate splitEndDate = new ParseAndSplitDate(endDate);

            int startMonth = splitStartDate.getFormattedMonth();
            int startYear = splitStartDate.getFormattedYear();
            int endMonth = splitEndDate.getFormattedMonth();
            int endYear = splitEndDate.getFormattedYear();
            int monthsBetween;

            if (startYear != endYear) {
                monthsBetween = (endYear - startYear) * 12;
                monthsBetween += ((12-startMonth) + endMonth);
            } else {
                monthsBetween = endMonth - startMonth;
            }
            if (monthsBetween != 0) {
                floatGoals += goalAmount / monthsBetween;
            }
        }

        return floatGoals.toString();
    }

}
