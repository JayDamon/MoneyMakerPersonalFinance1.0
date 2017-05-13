package com.moneymaker.utilities.QueryCreator;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.plaf.nimbus.State;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jay on 5/12/2017.
 */
public class QueryBuilder {

    public static ResultSet runQuery(Connection conn, ArrayList<String> fieldList, String tableName) {
        String sql = createSelectStatement(fieldList, tableName);
        PreparedStatement stmt = createPreparedStatement(conn, sql);
        return executeQuery(stmt);
    }
    public static ResultSet runQuery(Connection conn, ArrayList<String> fieldList, String tableName, ArrayList<String> conditionalColumns, ArrayList<Object> conditions) {
        String sql = createSelectStatement(fieldList, tableName);
        StringBuilder sb = new StringBuilder();
        sb.append(addWhereClause(sql, conditionalColumns));
        PreparedStatement stmt = createPreparedStatement(conn, sb.toString());
        stmt = setConditions(stmt, conditions);
        return executeQuery(stmt);
    }

    private static String createSelectStatement(ArrayList<String> fieldList, String tableName) {
        StringBuilder fieldString = new StringBuilder();
        for (int i = 0; i <= fieldList.size(); i++) {
            fieldString.append(fieldList.get(i));
            if (i != fieldList.size()) {
                fieldString.append(", ");
            }
        }

        return "SELECT " + fieldString.toString() + "FROM " + tableName;
    }

    private static String addWhereClause(String query, ArrayList<String> conditionColumns) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i <= conditionColumns.size(); i++) {
            sb.append(conditionColumns.get(i));
            if (i != conditionColumns.size()) {
                sb.append(" = ? ");
            }
        }
        return "WHERE " + sb.toString();
    }

    private static PreparedStatement setConditions(PreparedStatement stmt, ArrayList<Object> conditionValues) {
        try {
            for (int i = 1; i < conditionValues.size(); i++) {
                if (conditionValues.get(i) instanceof Integer) {
                    stmt.setInt(i, (int)conditionValues.get(i));
                } else if (conditionValues.get(i) instanceof String) {
                    stmt.setString(i, (String)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Short) {
                    stmt.setShort(i, (short)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof BigDecimal) {
                    stmt.setBigDecimal(i, (BigDecimal) conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Byte) {
                    stmt.setByte(i, (byte)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Boolean) {
                    stmt.setBoolean(i, (boolean)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Long) {
                    stmt.setLong(i, (long)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Float) {
                    stmt.setFloat(i, (float)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof Double) {
                    stmt.setDouble(i, (double)conditionValues.get(i));
                } else if(conditionValues.get(i) instanceof java.sql.Date) {
                    stmt.setDate(i, (java.sql.Date) conditionValues.get(i));
                }
            }
            return stmt;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PreparedStatement createPreparedStatement(Connection conn, String sql) {
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ResultSet executeQuery(PreparedStatement stmt) {
        try {
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
