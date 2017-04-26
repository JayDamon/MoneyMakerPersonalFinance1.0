package com.moneymaker.utilities.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by jaynd on 2/3/2016.
 */
public class ConnectionManagerAdmin {
    private static ConnectionManagerAdmin instance = null;

    private static final String USERNAME = "root";
    private static final String PASSWORD = "Heron2128#";
    private static final String CONN_STRING =
            "jdbc:mysql://localhost/";

    private Connection conn = null;

    private ConnectionManagerAdmin() {
    }

    public static ConnectionManagerAdmin getInstance() {
        if(instance == null) {
            instance = new ConnectionManagerAdmin();
        }
        return instance;
    }

    private boolean openConnection(String userName, String password) {
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
            if (openConnection(USERNAME, PASSWORD)) {
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

        }
    }

}
