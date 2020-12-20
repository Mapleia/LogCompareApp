package main.model.game;

import java.util.TreeMap;

public class Mechanic {
    private String name;
    private TreeMap<Integer, String> dataMap;

    public Mechanic(String name) {
        this.name = name;
        dataMap = new TreeMap<>();
    }

    public void add(int time, String actor) {
        dataMap.putIfAbsent(time, actor);
    }
}
