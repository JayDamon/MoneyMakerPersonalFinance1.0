package com.moneymaker.utilities.ConnectionManager;

import com.moneymaker.main.UsernameData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManagerUser {
    private static UsernameData usernameData = new UsernameData();
    private static ConnectionManagerUser instance = null;
    private static final String USERNAME = usernameData.getSessionUsername();
    private static final String PASSWORD = usernameData.getSessionPassword();
    private static final String CONN_STRING =
            "jdbc:mysql://localhost/" + usernameData.getUserSchema();

    private Connection conn = null;

    private ConnectionManagerUser() {
    }

    public static ConnectionManagerUser getInstance() {
        if(instance == null) {
            instance = new ConnectionManagerUser();
        }
        return instance;
    }

    private boolean openConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            return true;
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection()
    {
        if (conn == null) {
            if (openConnection()) {
                System.out.println("Connection opened");
                return conn;
            } else {
                return null;
            }
        }
        return conn;
    }

    public void close() {
        System.out.println("Closing connection");
        try {
            conn.close();
            conn = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
