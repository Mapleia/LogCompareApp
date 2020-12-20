package main.model.game;

public class Phase {
    private int start;
    private int end;
    private String name;
    private Integer[] targets;
    private Integer[] subPhases;
    private boolean isBreakBarPhase;

    public Phase(int start, int end, String name, Integer[] targets, Integer[] subPArr, boolean breakbar) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.targets = targets;
        this.subPhases = subPArr;
        this.isBreakBarPhase = breakbar;

    }

    public boolean isBreakbar() {
        return isBreakBarPhase;
    }
}
