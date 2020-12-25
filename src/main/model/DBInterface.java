package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

// used to interface with the database for the compare-er
public class DBInterface {
    public static final String[] BOON_COLUMNS = new String[]{"b717", "b718", "b719", "b725", "b726", "b740", "b743",
            "b873", "b1122", "b1187", "b17674", "b17675", "b26980", "b30328"};
    public static final String[] BOON_NAMES = new String[]{"Protection","Regeneration","Swiftness","Fury","Vigor",
            "Might","Aegis","Retaliation","Stability","Quickness","Regeneration2","Aegis2","Resistance", "Alacrity"};
    //
    /*
        [717]        = "Protection",
        [718]        = "Regeneration",
        [719]        = "Swiftness",
        [725]        = "Fury",
        [726]        = "Vigor"
        [740]        = "Might",
        [743]        = "Aegis",
        [873]        = "Retaliation",
        [1122]       = "Stability",
        [1187]       = "Quickness",
        [17674]      = "Regeneration",
        [17675]      = "Aegis",
        [26980]      = "Resistance",
        [30328]      = 'Alacrity",
      * */

    private final Input input;

    // constructor
    public DBInterface(Input input) {
        this.input = input;
        setup();
    }

    // MODIFIES: this
    // EFFECT: creates database if one is not made yet, and adds relevant table for the input
    private void setup() {
        try {
            sqlUpdate("CREATE DATABASE IF NOT EXISTS LogCompare;");
            createTable();

        } catch (Exception e) {
            System.out.println("Unable to establish connection.");
            e.printStackTrace();
        }
    }

    private void createTable() {
        if (doesNotExist("SELECT COUNT(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = 'LogCompare')"
                + " AND (TABLE_NAME = '" + input.getTableTitle() + "')")) {

            String table = "CREATE TABLE IF NOT EXISTS LogCompare." + input.getTableTitle() + " ("
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
            System.out.println(table);
            sqlUpdate(table);
        }
    }

    // MODIFIES: database
    // EFFECT: if not there, log will be added
    public void upload() {
        if (doesNotExist("SELECT COUNT(*) FROM LogCompare." + input.getTableTitle()
                + " WHERE FightID=" + input.hashCode())) {
            for (String s: input.createQueries()) {
                String insert = "INSERT INTO " + input.getTableTitle() + " "+ s;
                sqlUpdate(insert);
            }
        }
    }

    // returns true if ___ query comes up blank/empty
    private boolean doesNotExist(String check) {
        boolean result = true;

        try (Connection con = DataSource.getConnection()) {
            Statement st = con.createStatement();

            try (ResultSet rs = st.executeQuery(check)) {
                rs.next();
                result = rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            System.out.println(check);
            e.printStackTrace();
        }
        return result;
    }

    // MODIFIES: database
    // EFFECT: updates the table established in the setup with Input object values
    private void sqlUpdate(String update) {
        Statement st;
        try (Connection connection = DataSource.getConnection()) {
            st = connection.createStatement();
            st.executeUpdate(update);

        } catch (SQLException e) {
            System.out.println(update);
            e.printStackTrace();
        }
    }

    // EFFECT: make uptime percentile query and returns the result
    public Map<String, Map<String, Integer>> uptimePercentile() {
        Map<String, Map<String, Integer>> result = null;

        try (Connection con = DataSource.getConnection()) {
            Statement st = con.createStatement();
                try (ResultSet rs = st.executeQuery(getBoonQuery())) {
                    while (rs.next()) {
                        result = new HashMap<>();
                        Map<String, Integer> boons = new HashMap<>();
                        for (int i = 3; i < BOON_COLUMNS.length + 3; i++) {
                            boons.put(BOON_NAMES[i-3], Math.round(rs.getFloat(i)*100));
                        }
                        result.put(rs.getString(1), boons);
                    }
                } catch ( SQLException e2 ) {
                    e2.printStackTrace();
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // EFFECT: creates a string to query for boon percentiles
    private String getBoonQuery() {

        StringBuilder queryBuilder = new StringBuilder("WITH BOONS AS( SELECT Account, FightID, ");
        for (int i = 0; i < BOON_COLUMNS.length; i++) {

            queryBuilder.append("PERCENT_RANK() OVER (ORDER BY ")
                    .append(BOON_COLUMNS[i])
                    .append(" ASC) AS '")
                    .append(BOON_NAMES[i])
                    .append("'");

            if (i != BOON_COLUMNS.length -1) {
                queryBuilder.append(",");
            }
        }
        String query = queryBuilder.toString();
        query += " FROM LogCompare." + input.getTableTitle()
                + ") SELECT * FROM BOONS WHERE FightID=" + input.hashCode();

        return query;
        /*
        WITH BOONS AS( SELECT ACCOUNT, FightID,

         PERCENT_RANK() OVER (ORDER BY b717 ASC) AS 'PctRank',
                PERCENT_RANK()	OVER (ORDER BY b718 ASC) AS 'PctRank2'
        FROM mamacm)

        SELECT *  FROM BOONS WHERE FightID=-266130439
        */
    }

    // EFFECT: make a map of players with their dps percentiles from database.
    public Map<String, Integer> dpsPercentiles() {
        Map<String, Integer> result = new HashMap<>();
        String query = "WITH DPS_ENCOUNTER AS( SELECT Account, FightID, ARCHETYPE, DPS, " +
                "PERCENT_RANK() OVER (PARTITION BY ARCHETYPE ORDER BY DPS ASC) AS 'DPSRANK' FROM "
                + input.getTableTitle() + ") SELECT * FROM DPS_ENCOUNTER WHERE FightID=" + input.hashCode();
        /*
        WITH DPS_ENCOUNTER AS(
                SELECT ACCOUNT
                , FightID
                , ARCHETYPE
                , DPS
                , PERCENT_RANK()
                OVER (PARTITION BY ARCHETYPE ORDER BY DPS ASC) AS 'DPSRANK'
        FROM mamacm)

        SELECT * FROM DPS_ENCOUNTER
        WHERE FightID=-266130439
        */

        try (Connection con = DataSource.getConnection()) {
            Statement st = con.createStatement();
            try (ResultSet rs = st.executeQuery(query)) {
                while(rs.next()) {
                    result.put(rs.getString("Account"), Math.round(rs.getFloat("DPSRANK")*100));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(query);
        }

        return result;
    }
}

