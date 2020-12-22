package model;

import java.sql.*;

// used to interface with the database for the compare-er
public class DBLogger {
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "32314";
    private Input input;
    private Connection connection;
    private String tableTitle;

    // constructor
    public DBLogger(Input input) {
        this.input = input;
        setup();

        tableTitle = this.input.getTableTitle();
    }

    public DBLogger() {
        setup();
    }

    // EFFECT: set's up a connection to the database
    private void setup() {
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306",
                    USERNAME, PASSWORD);
            //sqlQuery("CREATE DATABASE IF NOT EXISTS logcompare;");
            connection.setCatalog("logcompare");
        } catch (SQLException throwables) {
            System.out.println("Unable to establish connection.");
            throwables.printStackTrace();
        }
    }

    // EFFECT: if not there, a table for the encounter will be created, and the log will be added
    public void upload() {
        String table = "CREATE TABLE IF NOT EXISTS " + tableTitle + " ("
                + "Gw2Build INT, "
                + "FightID INT, "
                + "CMMode BOOLEAN, "
                + "Account TEXT, "
                + "DPS INT, "
                + "ARCHETYPE TEXT, "
                + "b717 DOUBLE(6, 3), b718 DOUBLE(6, 3), b719 DOUBLE(6, 3), b725 DOUBLE(6, 3), b726 DOUBLE(6, 3), " +
                "b740 DOUBLE(6, 3), b743 DOUBLE(6, 3), b873 DOUBLE(6, 3), b1122 DOUBLE(6, 3), b1187 DOUBLE(6, 3), " +
                "b17674 DOUBLE(6, 3), b17675 DOUBLE(6, 3), b26980 DOUBLE(6, 3), b30328 DOUBLE(6, 3)"
                + ");";
        sqlQuery(table);

        for (String s: input.createQueries()) {
            String insert = "INSERT INTO " + tableTitle + " "+ s;
            System.out.println(insert);
            sqlUpdate(insert);
        }

    }

    // EFFECT: make a query to the database, using the supplied query string
    public ResultSet sqlQuery(String query) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(query);
        }
        return rs;
    }

    // EFFECT: updates the table established in the setup with Input object values
    private void sqlUpdate(String update) {
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(update);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(update);
        }
    }

    // EFFECT: closes the connection to the database
    public void end() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // EFFECT: check if table exist
    public boolean tableExist() {
        boolean result = false;

        String checkTable = "SELECT COUNT(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = 'LogCompare')"
                + " AND (TABLE_NAME = '" + tableTitle + "')";
        ResultSet rsTable = sqlQuery(checkTable);
        try {
            rsTable.next();
            if (rsTable.getInt(1) != 0) {
                result = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    // EFFECT: checks if table exist, if it does return boolean of if a fight with the fightID is already in the db
    public boolean exists() {
        boolean result = tableExist();

        String query = "SELECT COUNT(*) FROM " + tableTitle
                + " WHERE FightID="
                + input.hashCode();
        ResultSet rs = sqlQuery(query);
        try {
            rs.next();
            result = rs.getInt(1) != 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
