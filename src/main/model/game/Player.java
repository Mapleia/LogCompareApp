package model.game;

import java.util.List;

// represents a player in the encounter, and their boon uptime is iterable
public class Player {
    private final List<Double> uptimes;
    private final String account;
    private final int dps;
    private final String type;

    /**
     * constructor
     * @param account : account string of the player
     * @param dps : overall dps of the player
     * @param type : archetype of the character (support/healer/dps)
     * @param uptimes : list of overall boon uptime for each boon
     * */
    public Player(String account, int dps, String type, List<Double> uptimes) {
        this.account = account;
        this.dps = dps;
        this.uptimes = uptimes;
        this.type = type;
    }

    /*@Override
    public Iterator<Double> iterator() {
        return uptimes.iterator();
    }*/

    @Override
    public String toString() {
        StringBuilder valueBuilder = new StringBuilder("'" + account +"', '" + dps + "', '" + type + "', ");
        for (double i : uptimes) {
            valueBuilder.append(+i).append(", ");
        }

        return valueBuilder.toString();
    }
}
