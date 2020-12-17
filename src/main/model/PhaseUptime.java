package main.model;

import java.util.HashMap;

public class PhaseUptime {
    private double uptime;
    private HashMap<String, Double> generated;
    private HashMap<String, Double> overstacked;
    private HashMap<String, Double> wasted;

    public PhaseUptime(double uptime,
                       HashMap<String, Double> generated,
                       HashMap<String, Double> overstacked,
                       HashMap<String, Double> wasted) {

        this.uptime = uptime;
        this.generated = generated;
        this.overstacked = overstacked;
        this.wasted = wasted;
    }

    public PhaseUptime(double uptime) {
    }

    public void add(String s, HashMap<String, Double> parseDataPoints) {
    }
}
