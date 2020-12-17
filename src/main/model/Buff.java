package main.model;

public class Buff {
    private final int id;
    private final String name;
    private final String icon;
    private final boolean stacks;
    private final String[] descriptions;

    public Buff(int id, String name, String icon, boolean stacks, String[] descriptions) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.stacks = stacks;
        this.descriptions = descriptions;
    }
}
