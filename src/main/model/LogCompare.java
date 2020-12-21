package main.model;

import main.model.game.Player;
import main.persistence.JsonReader;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogCompare {
    public static final String[] ARCHETYPES = new String[]{"HEALER", "SUPPORT", "DPS"};

    private static final String[] BOON_COLUMNS = new String[]{"b717", "b718", "b719", "b725", "b726", "b740", "b743",
            "b873", "b1122", "b1187", "b17674", "b17675", "b26980"};
    private static final String[] BOON_NAMES = new String[]{"Protection","Regeneration","Swiftness","Fury","Vigor",
            "Might","Aegis","Retaliation","Stability","Quickness","Regeneration","Aegis","Resistance"};
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
    * */

    private String folderPath;
    private String fileName;
    private Input primary;

    // constructor
    public LogCompare(String folderPath, String fileName) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        init();
    }

    // EFFECT: initializes by processing file, adds all the other log files in the same folder
    // to the database if not already there
    private void init() {
        String path = folderPath + fileName + ".json";
        JsonReader reader = new JsonReader(path, fileName);
        reader.read();
        primary = reader.addToInput();

        try {
            File folder = new File(folderPath);
            DBLogger logger = null;
            for (File f : folder.listFiles()) {
                if (f.getName().equals(fileName)) {
                    continue;
                }
                JsonReader reader1 = new JsonReader(f.getPath(), f.getName());

                logger = new DBLogger(reader1.read());
                if (!logger.exists()) {
                    logger = new DBLogger(reader1.addToInput());
                    logger.upload();
                }
            }
            logger.end();

        } catch (NullPointerException e) {
            System.out.println(folderPath + " was not valid.");
        }
    }

    public Output compare() throws SQLException {
        List<List<Double>> boons = makeUptimeQuery();
        Map<String, Map<String, Integer>> bPtles = playerBoonsPercentiles(boons);

        List<List<Double>> dps = makeDpsQuery();
        Map<String, Integer> dPtles = playersDpsPercentiles(dps);

        DBLogger logger = new DBLogger(primary);
        if (!logger.exists()) {
            logger.upload();
        }

        return new Output(bPtles, dPtles, primary.hashCode());
    }

    private List<List<Double>> makeUptimeQuery() throws SQLException {
        DBLogger logger = new DBLogger();
        List<List<Double>> result = new ArrayList<>();

        for (String s : BOON_COLUMNS) {
            List<Double> values = new ArrayList<>();
            String query = "SELECT " + s + " FROM " + primary.getTableTitle();
            ResultSet resultSet = logger.sqlQuery(query);

            while(resultSet.next()) {
                values.add(resultSet.getDouble(s));
            }

            result.add(values);
        }

        return result;
    }

    private List<List<Double>> makeDpsQuery() throws SQLException {
        DBLogger logger = new DBLogger();
        List<List<Double>> result = new ArrayList<>();

        for (String s : ARCHETYPES) {
            List<Double> values = new ArrayList<>();
            String query = "SELECT DPS FROM " + primary.getTableTitle() + " WHERE ARCHETYPE='" + s + "';";
            ResultSet resultSet = logger.sqlQuery(query);
            while(resultSet.next()) {
                values.add((double) resultSet.getInt("DPS"));
            }
            result.add(values);
        }

        return result;
    }

    private Map<String, Map<String, Integer>> playerBoonsPercentiles(List<List<Double>> boons) {
        Map<String, Map<String, Integer>> playerPercentiles = new HashMap<>();
        for (Player p : primary.getPlayers()) {
            Map<String, Integer> percentiles = new HashMap<>();
            for (int i = 0; i < BOON_NAMES.length; i++) {
                percentiles.put(BOON_NAMES[i], percentile(boons.get(i), p.getBoon(i)));
            }

            playerPercentiles.put(p.getAccount(), percentiles);
        }

        return playerPercentiles;
    }

    private Map<String, Integer> playersDpsPercentiles(List<List<Double>> dpsList) {
        Map<String, Integer> playerPercentiles = new HashMap<>();
        for (Player p : primary.getPlayers()) {
            switch (p.getType()) {
                case "HEALER":
                    playerPercentiles.put(p.getAccount(), percentile(dpsList.get(0), p.getDps()));
                    break;
                case "SUPPORT":
                    playerPercentiles.put(p.getAccount(), percentile(dpsList.get(1), p.getDps()));
                    break;
                default:
                    playerPercentiles.put(p.getAccount(), percentile(dpsList.get(2), p.getDps()));
                    break;
            }
        }
        return playerPercentiles;

    }

    private int percentile(List<Double> doubleList, double boon) {
        List<Number> under = doubleList.stream().filter(aDouble -> aDouble <= boon).collect(Collectors.toList());
        return Math.round(((float) under.size()/ (float) (doubleList.size() + 1)) * 100);
    }
}
