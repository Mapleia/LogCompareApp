package model.game;

import java.util.Iterator;
import java.util.List;

// represents a player in the encounter, and their boon uptime is iterable
public class Player implements Iterable<Double>{
    private List<Double> uptimes;
    private String account;
    private int dps;
    private String type;

    /**
     * constructor
     * @param account : account string of the player
     * @param dps : overall dps of the player
     * @param type : archetype of the character (support/healer/dps)
     * @param uptimes : list of overall boon uptime for each boon
     * */
    public Player(String account, int dps, String type, List<Double> uptimes) {
        this.account = account;
        this.dps = dps;
        this.uptimes = uptimes;
        this.type = type;
    }

    @Override
    public Iterator<Double> iterator() {
        return uptimes.iterator();
    }

    public double getBoon(int i) {
        return uptimes.get(i);
    }

    public List<Double> getUptimes() {
        return uptimes;
    }

    // getters
    public String getAccount() {
        return account;
    }

    // getters
    public int getDps() {
        return dps;
    }

    // getters
    public String getType() {
        return type;
    }

}
