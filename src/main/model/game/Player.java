package main.model.game;

import java.util.Iterator;
import java.util.List;

public class Player implements Iterable<Double>{
    private String name;
    private List<Double> uptimes;
    private String account;
    private int dps;
    private String type;

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

    public String getAccount() {
        return account;
    }

    public int getDps() {
        return dps;
    }

    public String getType() {
        return type;
    }
}
