package com.moneymaker.utilities;

import com.moneymaker.utilities.ConnectionManager.ConnectionManagerAdmin;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created for MoneyMaker by Jay Damon on 10/25/2016.
 */
public class SQLAdmin {
    private static Connection conn = ConnectionManagerAdmin.getInstance().getConnection();

    public boolean findUserExists(String username, String password) {
        String sql = "CALL money_maker_stored_procedures.findUserExists(\"" + username + "\",\"" + password + "\")";
        boolean userExists = false;

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {

            while (rs.next()) {
                int result = rs.getInt("correct");
                if (result != 0) {
                    userExists = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public boolean findDuplicateTransaction(String username) {
        String sql = "CALL money_maker_stored_procedures.findDuplicateUser(\"" + username + "\")";
        boolean duplicatesFound = false;
        try (
                Statement stmtFindDuplicates = conn.createStatement();
                ResultSet rs = stmtFindDuplicates.executeQuery(sql)
        ) {
            while (rs.next()) {
                int result = rs.getInt("duplicateuser");
                if (result != 0) {
                    duplicatesFound = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duplicatesFound;
    }

    public void createNewUser(String username, String password, String firstName, String lastName, String email, String userSchema) {
        String sql = "CALL money_maker_stored_procedures.createuser(\"" + username + "\", \"" + password + "\", \"" + firstName + "\", " +
                "\"" + lastName + "\", \"" + email + "\", \"" + userSchema + "\")";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserSchema(String username, String password) {
            String sql = "CALL money_maker_stored_procedures.getUserSchema(\"" + username + "\", \"" + password + "\")";
        String userSchema = null;
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
                ) {
            while (rs.next()) {
                userSchema = rs.getObject("userSchema", String.class);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSchema;
    }
}
