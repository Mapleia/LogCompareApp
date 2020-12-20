package main.model.game;

import java.util.Iterator;
import java.util.List;

// represents a player in the encounter, and their boon uptime is iterable
public class Player implements Iterable<Double>{
    private String name;
    private List<Double> uptimes;
    private String account;
    private int dps;
    private String type;

    /**
     * constructor
     * @param name : name of the character
     * @param account : account string of the player
     * @param dps : overall dps of the player
     * @param type : archetype of the character (support/healer/dps)
     * @param uptimes : list of overall boon uptime for each boon
     * */
    public Player(String name, String account, int dps, String type, List<Double> uptimes) {
        this.name = name;
        this.account = account;
        this.dps = dps;
        this.uptimes = uptimes;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Player{" +
                "\n\t name='" + name + '\'' + ", " +
                "\n\tuptimes=" + uptimes + '}';
    }

    @Override
    public Iterator<Double> iterator() {
        return uptimes.iterator();
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
