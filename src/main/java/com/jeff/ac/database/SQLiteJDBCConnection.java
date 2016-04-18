package com.jeff.ac.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBCConnection {

    public Connection conn;
    private Statement sqlStatement;

    public static SQLiteJDBCConnection db;

    private SQLiteJDBCConnection () {
        String sqliteJdbcUrl = "jdbc:sqlite";
        String dbName = "moviescript.db";
        String sqliteDriver = "org.sqlite.JDBC";

        try {
            Class.forName(sqliteDriver).newInstance();
            this.conn = DriverManager.getConnection(sqliteJdbcUrl + ":" + dbName);
        }
        catch (Exception ex) {
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Get SQLite database connection
     *
     * @return SQLiteJDBCConnection Database connection object
     */
    public static synchronized SQLiteJDBCConnection getDbCon() {
        if (db == null) {
            db = new SQLiteJDBCConnection();
        }
        return db;
    }

    /**
     * Execute SQL query
     *
     * @param query String The query to be executed
     * @return a ResultSet object containing the results or null if not available
     *
     * @throws SQLException
     */
    public ResultSet query(String query) throws SQLException{
        if (db != null) {
            sqlStatement = db.conn.createStatement();
            ResultSet res = sqlStatement.executeQuery(query);
            return res;
        }

        return null;
    }

    /**
     * Execute insertion query
     *
     * @param insertQuery String The insert query
     *
     * @return boolean
     * @throws SQLException
     */
    public int insert(String insertQuery) throws SQLException {
        if (db != null) {
            sqlStatement = db.conn.createStatement();
            int result = sqlStatement.executeUpdate(insertQuery);
            return result;
        }

        return -1;
    }

    public void setAutoCommit(boolean value) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(value);
        }
    }

    public void commit() throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    public void rollBack() throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
