package main.model;

import org.json.JSONObject;

import java.util.Map;

public class Output {
    private int fightID;
    private final Map<String, Map<String, Integer>> boons;
    private final Map<String, Integer> dps;

    public Output(Map<String, Map<String, Integer>> boons, Map<String, Integer> dps, int fightID) {
        this.boons = boons;
        this.dps = dps;
        this.fightID = fightID;
    }

    @Override
    public String toString() {
        return "Output {" +
                "\n\tboons=" + boons +
                ", \n\tdps=" + dps +
                "\n}";
    }

    public JSONObject toJson() {
        JSONObject result = new JSONObject();
        JSONObject boonsObject = new JSONObject();

        for (String s : boons.keySet()) {
            boonsObject.put(s, new JSONObject(boons.get(s)));
        }
        result.put("FightID", fightID);
        result.put("BOON PERCENTILES", boonsObject);
        result.put("DPS PERCENTILES", dps);

        return result;
    }
}
