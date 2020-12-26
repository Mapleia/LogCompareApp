package persistence;

import model.Input;
import model.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

// reads and creates an Input object from given file path
public class JsonReader {
    public static final int[] BOONS = new int[]{717,718,719,725,726,740,743,873,1122,1187,17674,17675,26980,30328};
    public static final String[] ARCHETYPES = new String[]{"HEALER", "SUPPORT", "DPS"};
    private final String path;
    private final String fileName;
    private Input input;
    private JSONObject jsonObject;

    /**
     * constructor
     * @param path is the full path.
     * @param name is the file name.
     */
    public JsonReader(String path, String name) {
        this.path = path;
        this.fileName = name;
        init();
    }

    private void init() {
        try {
            String s = stringify();

            jsonObject = new JSONObject(s);
            input = parseInput();

        } catch (IOException e) {
            System.out.println("Error found reading the file.");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(fileName);
        }
    }

    // EFFECTS: reads source file as string and returns it
    private String stringify() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // MODIFIES: input
    // EFFECT: returns an Input object with all players parsed and added.
    public Input read() {
        JSONArray players = jsonObject.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            input.addPlayer(parsePlayer(p));
        }
        return input;
    }

    // EFFECT: returns an Input with all of the values inputted (taken from the given file)
    private Input parseInput() {
        Input input = new Input(jsonObject.getBoolean("isCM"), jsonObject.getInt("gW2Build"),
                jsonObject.getString("fightName"));

        JSONArray players = jsonObject.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            input.addAccount(p.getString("account"), p.getInt("instanceID"));
        }

        return input;
    }

    // EFFECT: returns parsed Player object, with field formatting intact from JSON file.
    private Player parsePlayer(JSONObject o) {
        JSONArray buffUptimes = o.getJSONArray("buffUptimesActive");

        // List of buffs, with their uptimes
        Map<Integer, JSONObject> map = new TreeMap<>();
        for (int j = 0; j< buffUptimes.length(); j++) {
            JSONObject buff = buffUptimes.getJSONObject(j);
            map.put(buff.getInt("id"), buff);
        }

        List<Double> uptimes = new ArrayList<>();
        for (int key : BOONS) {
            if (!map.containsKey(key)) {
                uptimes.add(0.0);
            } else {
                double uptime = map.get(key)
                        .getJSONArray("buffData")
                        .getJSONObject(0)
                        .getDouble("uptime");
                uptimes.add(uptime);
            }
        }

        String type = ARCHETYPES[2];
        if (o.getInt("healing") > 0) {
            type = ARCHETYPES[0];
        } else if (o.getInt("toughness") > 0 || o.getInt("concentration") > 0) {
            type = ARCHETYPES[1];
        }

        return new Player(o.getString("account"),
                o.getJSONArray("dpsTargets").getJSONArray(0).getJSONObject(0).getInt("dps"),
                type,
                uptimes);
    }
}
