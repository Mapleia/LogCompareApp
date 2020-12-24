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

// class that does all of the comparisons and gives an output
public class LogCompare {
    public static final String[] ARCHETYPES = new String[]{"HEALER", "SUPPORT", "DPS"};
    public static final String[] BOON_COLUMNS = new String[]{"b717", "b718", "b719", "b725", "b726", "b740", "b743",
            "b873", "b1122", "b1187", "b17674", "b17675", "b26980", "b30328"};
    public static final String[] BOON_NAMES = new String[]{"Protection","Regeneration","Swiftness","Fury","Vigor",
            "Might","Aegis","Retaliation","Stability","Quickness","Regeneration","Aegis","Resistance", "Alacrity"};
    public static final int[] BOONS = new int[]{717,718,719,725,726,740,743,873,1122,1187,17674,17675,26980,30328};
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

    private final Input primary;

    // constructor
    public LogCompare(File file) {
        JsonReader reader = new JsonReader(file.getAbsolutePath(), FilenameUtils.getBaseName(file.getName()));
        primary = reader.read();
    }

    // EFFECT: compare with other logs in the database and create a JSONObject of the percentiles
    public JSONObject compare() throws SQLException {
        DBInterface logger = new DBInterface(primary);
        Map<String, Map<String, Integer>> boonPercentile;
        Map<String, Integer> dpsPercentile;

        if (logger.tableExist()) {
            boonPercentile = playerBoonsPercentiles(logger.makeUptimeQuery());
            dpsPercentile = playersDpsPercentiles(logger.makeDpsQuery());

        } else {
            boonPercentile = new HashMap<>();
            dpsPercentile = new HashMap<>();

            Map<String, Integer> boon100Percentile = new HashMap<>();
            for (String boon : BOON_NAMES) {
                boon100Percentile.put(boon, 100);
            }

            for (Player p : primary.getPlayers()) {
                boonPercentile.put(p.getAccount(), boon100Percentile);
                dpsPercentile.put(p.getAccount(), 100);
            }
        }

        if (!logger.exists()) {
            logger.upload();
        }
        logger.end();

        return primary.toJson(boonPercentile, dpsPercentile);
    }

    // EFFECT: create a map of the boon percentiles
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

    // EFFECT: create a map of the dps percentiles (and only compared to others of the same archetype)
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

    // EFFECT: return a percentile given a value and a list of other values to compare it
    private int percentile(List<Double> doubleList, double compare) {
        List<Number> under = doubleList.stream().filter(aDouble -> aDouble <= compare).collect(Collectors.toList());
        return Math.round(((float) under.size()/ (float) (doubleList.size() + 1)) * 100);
    }
}
