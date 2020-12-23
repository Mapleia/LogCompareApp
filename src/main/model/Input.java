package model;

import model.game.Mechanic;
import model.game.Player;
import org.json.JSONObject;

import java.util.*;

public class Input {

    private final boolean isCM;
    private final int gameBuild;
    private final String fightName;
    private String tableTitle;

    private List<Player> players;
    private List<Mechanic> mechanics;
    private Map<String, Integer> accounts;

    // constructor
    public Input(boolean isCM, int gameBuild, String fightName) {
        players = new ArrayList<>();
        mechanics = new ArrayList<>();
        accounts = new TreeMap<>();

        this.isCM = isCM;

        this.gameBuild = gameBuild;

        this.fightName = fightName;

        tableTitle = this.fightName.replaceAll("\\s+","");
        if (isCM) {
            tableTitle += "CM";
        }
    }

    // getter
    public int getGameBuild() {
        return gameBuild;
    }

    // getter
    public String getFightName() {
        return fightName;
    }

    // getter
    public boolean isCM() {
        return isCM;
    }

    // getter
    public String getTableTitle() {
        return tableTitle;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<String, Integer> getAccounts() {
        return accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return isCM() == input.isCM() &&
                getGameBuild() == input.getGameBuild() &&
                getFightName().equals(input.getFightName()) &&
                getAccounts().equals(input.getAccounts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCM(), getGameBuild(), getFightName(), getAccounts());
    }

    // EFFECT: add a Player
    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    // EFFECT: add to the list of account names
    public void addAccount(String s, int instanceID) {
        accounts.putIfAbsent(s, instanceID);
    }

    // EFFECT: add a mechanic to the list
    public void addMechanic(Mechanic m) {
        if (!mechanics.contains(m)) {
            mechanics.add(m);
        }
    }

    // EFFECT: based on the fields, create a string to be used to set values for the database
    public List<String> createQueries() {
        List<String> result = new ArrayList<>();
        for (Player p : players) {
            String value = "VALUE ("
                    + gameBuild + ", "
                    + hashCode() + ", "
                    + isCM + ", '"
                    + p.getAccount() + "', "
                    + p.getDps() + ", '"
                    + p.getType() + "', ";

            for (double i : p) {
                value += + i + ", ";
            }
            value = value.trim().substring(0, value.length()-2);
            value += ");";


            result.add(value);
        }
        return result;
    }

    public JSONObject toJson(Map<String, Map<String, Integer>> boons, Map<String, Integer> dps) {
        JSONObject result = new JSONObject();

        result.put("FightID", hashCode());
        result.put("BOON PERCENTILES", new JSONObject(boons));
        result.put("DPS PERCENTILES", new JSONObject(dps));
        result.put("FIGHT_NAME", fightName);

        return result;
    }

}
