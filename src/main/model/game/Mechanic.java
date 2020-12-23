package model.game;

import java.util.TreeMap;

// class that represents a mechanic, and stores all of the characters that did it
public class Mechanic {
    private final String name;
    private final TreeMap<Integer, String> dataMap;

    /**
     * constructor:
     * @param name name of the mechanic
     * */
    public Mechanic(String name) {
        this.name = name;
        dataMap = new TreeMap<>();
    }

    // EFFECT: add player to mechanics
    public void add(int time, String actor) {
        dataMap.putIfAbsent(time, actor);
    }
}
