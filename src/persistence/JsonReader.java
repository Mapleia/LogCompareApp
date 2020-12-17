package persistence;

import main.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class JsonReader {
    private String input;
    private String fileName;
    private File file;
    /**
     * constructor
     * @param name is the file name.
     * @param input is the full path.
     */
    public JsonReader(String name, String input) {
        this.input = input;
        this.fileName = name;
    }

    // EFFECTS: reads source file as string and returns it
    public String readFile() throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(input), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    // EFFECT: returns parsed file as an Input object
    public Input read() {
        String stringified = "";

        try {
            stringified = readFile();
        } catch (Exception e) {
            System.out.println("Error found reading the file.");
        }

        JSONObject object = new JSONObject(stringified);
        return parseInput(object);
    }

    // EFFECT: returns an Input object with all players and mechanics parsed and added.
    private Input parseInput(JSONObject object) {
        Input input = new Input(fileName);

        JSONObject buffs = object.getJSONObject("buffMap");
        for (String key : buffs.keySet()) {
            try {
                int id = Integer.parseInt(key.substring(1));
                Buff buff = parseBuff(buffs, key, id);
                input.putBuff(id, buff);

            } catch (Exception e) {
                System.out.println("An error was found parsing buffMap: (the buff key)->" + key);
            }
        }

        JSONArray players = object.getJSONArray("players");
        for (int i = 0; i < players.length(); i++) {
            JSONObject p = players.getJSONObject(i);
            input.addPlayer(parsePlayer(p));
        }
        
        JSONArray mechanics = object.getJSONArray("mechanics");
        for (int j = 0; j < mechanics.length(); j++) {
            JSONObject m = mechanics.getJSONObject(j);
            input.addMechanic(parseMechanics(m));
        }

        JSONArray phases = object.getJSONArray("phases");
        for (int k = 0; k < phases.length(); k++) {
            JSONObject ph = phases.getJSONObject(k);
            input.addPhase(parsePhase(ph));
        }


        return input;
    }

    private Buff parseBuff(JSONObject buffs, String key, int id) {
        JSONObject o = buffs.getJSONObject(key);
        String name = o.getString("name");
        String icon = o.getString("icon");
        boolean stacks = o.getBoolean("stacking");
        JSONArray d = o.getJSONArray("descriptions");
        String[] descriptions = d.toList().toArray(new String[d.length()]);
        Buff buff = new Buff(id, name, icon, stacks, descriptions);
        return buff;
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

    private Phase parsePhase(JSONObject jsonPhase) {
        int start = jsonPhase.getInt("start");
        int end = jsonPhase.getInt("end");
        String name = jsonPhase.getString("name");

        JSONArray targets = jsonPhase.getJSONArray("targets");
        Integer[] intArr = targets.toList().toArray(new Integer[targets.length()]);

        JSONArray subPhases = jsonPhase.getJSONArray("subPhases");
        Integer[] subPArr = subPhases.toList().toArray(new Integer[subPhases.length()]);

        boolean breakbar = jsonPhase.getBoolean("breakbarPhase");

        Phase phase = new Phase(start, end, name, intArr, subPArr, breakbar);

        return phase;
    }

    private Player parsePlayer(JSONObject o) {
        JSONArray buffUptimes = o.getJSONArray("buffUptimes");
        List<BuffUptime> uptimes = new ArrayList<>();
        for (int j = 0; j< buffUptimes.length(); j++) {
            JSONObject buff = buffUptimes.getJSONObject(j);
            BuffUptime buffUptime = new BuffUptime(buff.getInt("id"));

            JSONArray data = buff.getJSONArray("buffData");
            for (int i = 0; i< data.length(); i++) {
                JSONObject phase = data.getJSONObject(i);
                double uptime = phase.getDouble("uptime");
                PhaseUptime phaseUptime = new PhaseUptime(uptime);

                for (String s : phase.keySet()) {
                    if (s.equals("generated") || s.equals("overstacked") || s.equals("wasted")){
                        phaseUptime.add(s, parseDataPoints(s, phase));
                    } else {
                        continue;
                    }
                }
                buffUptime.add(phaseUptime);
            }
            uptimes.add(buffUptime);
        }
        return new Player(o.getString("name"), uptimes);
    }

    private HashMap<String, Double> parseDataPoints(String key, JSONObject phase) {
        HashMap<String, Double> dataPoint = new HashMap<>();

        JSONObject point = phase.getJSONObject(key);
        for (String s : point.keySet()) {
            dataPoint.put(s, point.getDouble(s));
        }

        return dataPoint;
    }
}
