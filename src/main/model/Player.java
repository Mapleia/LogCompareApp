package main.model;

import java.util.List;

public class Player {
    private String name;
    private List<BuffUptime> uptimes;

    public Player(String name, List<BuffUptime> uptimes) {
        this.name = name;
        this.uptimes = uptimes;
    }
}
