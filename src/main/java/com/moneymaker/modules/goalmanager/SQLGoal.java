package com.moneymaker.modules.goalmanager;

import com.moneymaker.modules.transactionmanager.transactions.Transaction;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import com.moneymaker.utilities.FormatDate;
import com.moneymaker.utilities.FormatDollarAmount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created for MoneyMaker by Jay Damon on 9/19/2016.
 */
public class SQLGoal {

    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();
    private FormatDate formatDate = new FormatDate();
    private SimpleDateFormat formatter = new SimpleDateFormat(FormatDate.CALENDAR_DISPLAY_DATE);
    private SimpleDateFormat formatterSQL = new SimpleDateFormat(FormatDate.SQL_INPUT_DATE);
    private String userSchema = new UsernameData().getUserSchema();

    public ArrayList<String> listOfGoalTypes() {
        ArrayList<String> listOfGoalTypes = new ArrayList<>();
        String sql = "CALL moneymakerprocs.listGoalType(\"" + userSchema + "\")";
        try (
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)
        ) {
            rs.beforeFirst();
            while(rs.next()) {
                String goalType = rs.getObject("goalTypeName", String.class);
                listOfGoalTypes.add(goalType);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listOfGoalTypes;
    }

    public void addGoal(String goalName, String goalPriority, String goalType, String goalAccount,
                        String goalStartDate, String goalEndDate, String goalAmount) {
        if (!goalEndDate.equals("NULL")) {
            goalEndDate = "\"" + goalEndDate + "\"";
        }

        String sql = "CALL moneymakerprocs.addGoal(\"" + userSchema + "\", \"" + goalName +"\", " + goalPriority + ", \"" + goalType + "\", \"" + goalAccount + "\", \"" +
                                        goalStartDate + "\", " + goalEndDate + ", " + goalAmount + ")";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Goal> viewGoals() {
        ObservableList<Goal> goals = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.viewGoals(\"" + userSchema + "\")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            while(rs.next()) {
                String goalID = rs.getObject("goalID", String.class);
                String goalName = rs.getObject("goalName", String.class);
                String goalPriority = rs.getObject("goalPriority", String.class);
                String goalTypeName = rs.getObject("goalTypeName", String.class);
                String goalAccountName = rs.getObject("acc_name", String.class);
                String goalStartDate = rs.getObject("startDate", String.class);
                String goalEndDate = rs.getObject("endDate", String.class);
                String goalAmount = rs.getObject("goalAmount", String.class);
                String goalActualAmount = rs.getObject("goalActualAmount", String.class);

                if (goalAmount != null) {
                    Float floatGoalAmount = Float.parseFloat(goalAmount);
                    if (floatGoalAmount < 0) {
                        goalAmount = new FormatDollarAmount().FormatAsDollarWithParenthesis(goalAmount, new Transaction().EXPENSE);
                    } else {
                        goalAmount = new FormatDollarAmount().FormatAsDollarWithParenthesis(goalAmount, "None");
                    }
                }
                if (goalActualAmount == null) {
                    goalActualAmount = "0";
                }

                Float floatGoalAmount = Float.parseFloat(goalActualAmount);
                if (floatGoalAmount < 0) {
                    goalActualAmount = new FormatDollarAmount().FormatAsDollarWithParenthesis(goalActualAmount, new Transaction().EXPENSE);
                } else {
                    goalActualAmount = new FormatDollarAmount().FormatAsDollarWithParenthesis(goalActualAmount, "None");
                }

                if (goalStartDate != null) {
                    Calendar calStartDate = formatDate.parseStringCalendar(goalStartDate);
                    goalStartDate = formatter.format(calStartDate.getTime());
                }

                if (goalEndDate != null) {
                    Calendar calEndDate = formatDate.parseStringCalendar(goalEndDate);
                    goalEndDate = formatter.format(calEndDate.getTime());
                }

                goals.add(new Goal(goalID, goalName, goalPriority, goalTypeName, goalAccountName, goalStartDate, goalEndDate, goalAmount, goalActualAmount));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

    public ArrayList<String> goalCount() {
        ArrayList<String> goalCount = new ArrayList<>();

        String sql = "CALL moneymakerprocs.countGoals(\"" + userSchema + "\")";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            rs.beforeFirst();
            while (rs.next()) {
                Integer goalCountInt = rs.getInt("goalCount");
                for (int i=1; i<=goalCountInt; i++) {
                    String value = String.valueOf(i);
                    goalCount.add(value);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return goalCount;
    }

    public void updateGoals(String goalID, String goalName, String goalPriority, String goalType,
                            String goalAccount, String goalStartDate, String goalEndDate, String goalAmount) {

        if (goalStartDate != null) {
            Calendar calStartDate = formatDate.parseStringCalendar(goalStartDate);
            goalStartDate = formatterSQL.format(calStartDate.getTime());
            goalStartDate = "\"" + goalStartDate + "\"";
        }

        if (goalEndDate != null) {
            Calendar calEndDate = formatDate.parseStringCalendar(goalEndDate);
            goalEndDate = formatterSQL.format(calEndDate.getTime());
            goalEndDate = "\"" + goalEndDate + "\"";
        }

        goalAmount = new FormatDollarAmount().CleanDollarAmountsForSQL(goalAmount);

        String sql = "CALL moneymakerprocs.updateGoals(\"" + userSchema + "\", " + goalID + ", \"" + goalName + "\", " + goalPriority + ", \"" + goalType + "\", \"" +
                        goalAccount + "\", " + goalStartDate + ", " + goalEndDate + ", " + goalAmount + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGoal(String goalID) {
        String sql = "CALL moneymakerprocs.deleteGoal(\"" + userSchema + "\", " + goalID + ")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> listGoals() {
        ArrayList<String> goals = new ArrayList<>();

        String sql = "CALL moneymakerprocs.listGoals(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            while (rs.next()) {
                String goalName = rs.getObject("Goal Name", String.class);
                goals.add(goalName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return goals;
    }

}
