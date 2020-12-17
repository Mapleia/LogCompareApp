package main.model;

import java.util.ArrayList;
import java.util.List;

public class BuffUptime {
    private int buff;
    private List<PhaseUptime> listOfBuffs;

    public BuffUptime(int buff) {
        this.buff = buff;
        listOfBuffs = new ArrayList<>();
    }

    public void add(PhaseUptime u) {
        listOfBuffs.add(u);
    }
}
