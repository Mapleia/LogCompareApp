package main.persistence;

import main.model.Input;
import main.model.LogCompare;
import main.model.game.Mechanic;
import main.model.game.Player;
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

import static main.model.Input.BOONS;

// reads and creates an Input object from given file path
public class JsonReader {
    private String path;
    private String fileName;
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
    }

    // EFFECTS: reads source file as string and returns it
    public String readFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECT: returns parsed file as an Input object
    public Input read() {
        try {
            String stringified = readFile();

            jsonObject = new JSONObject(stringified);
            input = parseInput();

        } catch (IOException e) {
            System.out.println("Error found reading the file.");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(fileName);
        }

        return input;
    }

    private Input parseInput() {
        Input input = new Input(fileName, jsonObject.getBoolean("isCM"),
                jsonObject.getInt("gW2Build"),
                jsonObject.getString("fightName"));

        JSONArray players = jsonObject.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            input.addAccount(p.getString("account"), p.getInt("instanceID"));
        }

        return input;
    }

    // EFFECT: returns an Input object with all players and mechanics parsed and added.
    public Input addToInput() {
        JSONArray phases = jsonObject.getJSONArray("phases");

        JSONArray players = jsonObject.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            input.addPlayer(parsePlayer(p));
        }
        
        JSONArray mechanics = jsonObject.getJSONArray("mechanics");
        for (int j = 0; j < mechanics.length(); j++) {
            JSONObject m = mechanics.getJSONObject(j);
            input.addMechanic(parseMechanics(m));
        }

        return input;
    }

    // EFFECT: returns Mechanic object from given JSONObject.
    private Mechanic parseMechanics(JSONObject m) {
        String name = m.getString("name");
        Mechanic mechanic = new Mechanic(name);

        JSONArray a = m.getJSONArray("mechanicsData");
        for (int i = 0; i< a.length(); i++) {
            JSONObject obj = a.getJSONObject(i);
            int time = obj.getInt("time");
            String actor = obj.getString("actor");
            mechanic.add(time, actor);
        }

        return mechanic;
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
            if (!map.keySet().contains(key)) {
                uptimes.add(0.0);
                continue;
            } else {
                double uptime = map.get(key)
                        .getJSONArray("buffData")
                        .getJSONObject(0)
                        .getDouble("uptime");
                uptimes.add(uptime);
            }
        }

        String type = LogCompare.ARCHETYPES[2];
        if (o.getInt("healing") > 0) {
            type = LogCompare.ARCHETYPES[0];
        } else if (o.getInt("toughness") > 0 || o.getInt("concentration") > 0) {
            type = LogCompare.ARCHETYPES[1];
        }

        return new Player(o.getString("name"), o.getString("account"),
                o.getJSONArray("dpsTargets").getJSONArray(0).getJSONObject(0).getInt("dps"),
                type,
                uptimes);
    }
}
