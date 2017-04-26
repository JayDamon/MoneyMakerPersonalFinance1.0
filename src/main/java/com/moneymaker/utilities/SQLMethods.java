package com.moneymaker.utilities;

import com.moneymaker.modules.budgetmanager.BudgetGraph;
import com.moneymaker.main.UsernameData;
import com.moneymaker.utilities.ConnectionManager.ConnectionManagerUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created for MoneyMaker by Jay Damon on 8/27/2016.
 */
public class SQLMethods {

    private static Connection conn = ConnectionManagerUser.getInstance().getConnection();
    private String userSchema = new UsernameData().getUserSchema();

    public ArrayList<String> listFrequency() {
        ArrayList<String> frequencyList = new ArrayList<>();
        String sql = "CALL moneymakerprocs.ListFrequency(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {

                String freqtype = rs.getObject("freqtype_name", String.class);

                frequencyList.add(freqtype);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frequencyList;
    }

    public ArrayList listOccurrence() {
        ArrayList<String> occurrenceList = new ArrayList<>();
        String sql = "CALL moneymakerprocs.ListOccurrence(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {

                String occType = rs.getObject("occtype_name", String.class);

                occurrenceList.add(occType);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return occurrenceList;
    }



    public ArrayList<String> listTranType() {

        ArrayList<String> tranTypeList = new ArrayList<>();

        String sql = "CALL moneymakerprocs.ListTranType(\"" + userSchema + "\")";

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            rs.beforeFirst();

            while (rs.next()) {

                String occType = rs.getObject("tran_type", String.class);

                tranTypeList.add(occType);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tranTypeList;
    }

    public ObservableList<BudgetGraph> graphBudgets(String startDate, String endDate) {
        ObservableList<BudgetGraph> budgetGraph = FXCollections.observableArrayList();
        String sql = "CALL moneymakerprocs.graphBudgets(\"" + userSchema + "\", \"" + startDate + "\",\"" + endDate + "\")";
        //ToDo fix dates.  Income planned is only showing 2 weeks rather than full month
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                String budget = rs.getObject("Budget", String.class);
                String plannedAmount = rs.getObject("Planned", String.class);
                String actualAmount = rs.getObject("Actual", String.class);

                if (plannedAmount == null) {
                    plannedAmount = "0.0000";
                }

                if (actualAmount == null) {
                    actualAmount = "0.0000";
                }

                budgetGraph.add(new BudgetGraph(budget, plannedAmount, actualAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return budgetGraph;
    }
}
