package main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Input {
    private String fileName;
    private Map<Integer, Buff> buffMap;

    private List<Player> players;
    private List<Mechanic> mechanics;
    private List<Phase> phases;

    public Input(String fileName) {
        buffMap = new TreeMap<>();

        players = new ArrayList<>();
        mechanics = new ArrayList<>();
        phases = new ArrayList<>();

        this.fileName = fileName;
    }

    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    public void addMechanic(Mechanic m) {
        if (!mechanics.contains(m)) {
            mechanics.add(m);
        }
    }

    public void addPhase(Phase phase) {
        if (!phases.contains(phase)) {
            phases.add(phase);
        }
    }

    public void putBuff(int id, Buff b) {
        buffMap.put(id, b);
    }

    public Buff getBuff(int id) {
        return buffMap.get(id);
    }
}
