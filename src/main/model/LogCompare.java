package model;

import model.game.Player;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonReader;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogCompare {
    public static final String[] ARCHETYPES = new String[]{"HEALER", "SUPPORT", "DPS"};

    public static final String[] BOON_COLUMNS = new String[]{"b717", "b718", "b719", "b725", "b726", "b740", "b743",
            "b873", "b1122", "b1187", "b17674", "b17675", "b26980", "b30328"};
    public static final String[] BOON_NAMES = new String[]{"Protection","Regeneration","Swiftness","Fury","Vigor",
            "Might","Aegis","Retaliation","Stability","Quickness","Regeneration","Aegis","Resistance", "Alacrity"};

    public static final int[] BOONS = new int[]{717,718,719,725,726,740,743,873,1122,1187,17674,17675,26980,30328};

    private File file;
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

    private String folderPath;
    private String fileName;
    private Input primary;

    // constructor
    public LogCompare(File file) {
        this.file = file;
        init();
    }

    // EFFECT: initializes by processing file
    private void init() {
        fileName = FilenameUtils.getBaseName(file.getName());
        folderPath = file.getAbsolutePath();
        JsonReader reader = new JsonReader(folderPath, fileName);
        primary = reader.read();
    }

    public JSONObject compare() throws SQLException {
        DBInterface logger = new DBInterface(primary);

        Map<String, Map<String, Integer>> boonPercentile;
        Map<String, Integer> dpsPercentile;
        if (logger.tableExist()) {
            List<List<Double>> boons = logger.makeUptimeQuery();
            boonPercentile = playerBoonsPercentiles(boons);

            List<List<Double>> dps = logger.makeDpsQuery();
            dpsPercentile = playersDpsPercentiles(dps);

            if (!logger.exists()) {
                logger.upload();
            }

            return primary.toJson(boonPercentile, dpsPercentile);
        } else {
            return firstLog();
        }

    }

    private JSONObject firstLog() {
        Map<String, Map<String, Integer>> boonPercentile = new HashMap<>();
        Map<String, Integer> dpsPercentile = new HashMap<>();

        Map<String, Integer> boon100Percentile = new HashMap<>();
        for (String boon : BOON_NAMES) {
            boon100Percentile.put(boon, 100);
        }

        for (Player p : primary.getPlayers()) {
            boonPercentile.put(p.getAccount(), boon100Percentile);
            dpsPercentile.put(p.getAccount(), 100);
        }

        return primary.toJson(boonPercentile, dpsPercentile);
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

    private int percentile(List<Double> doubleList, double compare) {
        List<Number> under = doubleList.stream().filter(aDouble -> aDouble <= compare).collect(Collectors.toList());
        return Math.round(((float) under.size()/ (float) (doubleList.size() + 1)) * 100);
    }
}
