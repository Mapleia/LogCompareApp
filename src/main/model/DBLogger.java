package main.model;

import java.sql.*;

public class DBLogger {
    private Input input;
    private Connection connection;
    private String tableTitle;

    public DBLogger(Input input) {
        this.input = input;
        setup();
    }

    private void setup() {
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare",
                    DBCredentials.USERNAME, DBCredentials.PASSWORD);
        } catch (SQLException throwables) {
            System.out.println("Unable to establish connection.");
            throwables.printStackTrace();
        }

        tableTitle = input.getFightName();
        tableTitle = tableTitle.replaceAll("\\s+","");
        if (input.isCM()) {
            tableTitle += "CM";
        }

    }

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
                "b17674 DOUBLE(6, 3), b17675 DOUBLE(6, 3), b26980 DOUBLE(6, 3)"
                + ");";
        sqlQuery(table);

        for (String s: input.createQueries()) {
            String insert = "INSERT INTO " + tableTitle + " "+ s;
            System.out.println(insert);
            sqlUpdate(insert);
        }

    }

    private ResultSet sqlQuery(String query) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    private void sqlUpdate(String update) {
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(update);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void end() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean exists() {
        boolean result = false;

        String checkTable = "SELECT COUNT(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = 'LogCompare')"
                + " AND (TABLE_NAME = '" + tableTitle + "')";
        ResultSet rsTable = sqlQuery(checkTable);
        try {
            rsTable.next();
            if (rsTable.getInt(1) == 0) {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

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
