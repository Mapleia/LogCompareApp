package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// used to interface with the database for the compare-er
public class DBInterface {
    private String username;
    private String password;
    private final Input input;
    private Connection connection;
    private final String tableTitle;

    // constructor
    public DBInterface(Input input) {
        this.input = input;
        setup();

        tableTitle = this.input.getTableTitle();
    }

    // EFFECT: set's up a connection to the database
    private void setup() {
        try {
            getProperties();
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306",
                    username, password);
            //sqlQuery("CREATE DATABASE IF NOT EXISTS logcompare;");
            connection.setCatalog("logcompare");
        } catch (Exception e) {
            System.out.println("Unable to establish connection.");
            e.printStackTrace();
        }
    }

    private void getProperties() throws IOException {
        Properties properties = new Properties();
        String file = "config.properties";
        InputStream i = getClass().getClassLoader().getResourceAsStream(file);

        if (i != null) {
            properties.load(i);
        } else {
            throw new FileNotFoundException("property file '" + file + "' not found.");
        }

        username = properties.getProperty("username");
        password = properties.getProperty("password");
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

    public List<List<Double>> makeUptimeQuery() throws SQLException {
        List<List<Double>> result = new ArrayList<>();

        for (String s : LogCompare.BOON_COLUMNS) {
            List<Double> values = new ArrayList<>();
            String query = "SELECT " + s + " FROM " + input.getTableTitle();
            ResultSet resultSet = sqlQuery(query);

            while(resultSet.next()) {
                values.add(resultSet.getDouble(s));
            }

            result.add(values);
        }

        return result;
    }

    public List<List<Double>> makeDpsQuery() throws SQLException {
        List<List<Double>> result = new ArrayList<>();

        for (String s : LogCompare.ARCHETYPES) {
            List<Double> values = new ArrayList<>();
            String query = "SELECT DPS FROM " + input.getTableTitle() + " WHERE ARCHETYPE='" + s + "';";
            ResultSet resultSet = sqlQuery(query);
            while(resultSet.next()) {
                values.add((double) resultSet.getInt("DPS"));
            }
            result.add(values);
        }

        return result;
    }

    // EFFECT: make a query to the database, using the supplied query string
    private ResultSet sqlQuery(String query) {
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
