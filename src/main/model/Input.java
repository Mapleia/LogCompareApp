package main.model;

import main.model.game.Mechanic;
import main.model.game.Player;

import java.util.*;

public class Input {
    public static final int[] BOONS = new int[]{717,718,719,725,726,740,743,873,1122,1187,17674,17675,26980};

    private String fileName;
    private final boolean isCM;
    private final int gameBuild;
    private final String fightName;

    private List<Player> players;
    private List<Mechanic> mechanics;
    private Map<String, Integer> accounts;

    public Input(String fileName, boolean isCM, int gameBuild, String fightName) {
        players = new ArrayList<>();
        mechanics = new ArrayList<>();
        accounts = new TreeMap<>();

        this.fileName = fileName;
        this.isCM = isCM;

        this.gameBuild = gameBuild;

        this.fightName = fightName;
    }

    public int getGameBuild() {
        return gameBuild;
    }

    public String getFightName() {
        return fightName;
    }


    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    public void addAccount(String s, int instanceID) {
        accounts.putIfAbsent(s, instanceID);
    }

    public void addMechanic(Mechanic m) {
        if (!mechanics.contains(m)) {
            mechanics.add(m);
        }
    }

    public boolean isCM() {
        return isCM;
    }

    public List<String> createQueries() {
        List<String> result = new ArrayList<>();
        for (Player p : players) {
            String value = "VALUE ("
                    + gameBuild + ", "
                    + hashCode() + ", "
                    + isCM + ", '"
                    + p.getAccount() + "', "
                    + p.getDps() + ", '"
                    + p.getType() + "', ";

            for (double i : p) {
                value += + i + ", ";
            }
            value = value.trim().substring(0, value.length()-2);
            value += ");";


            result.add(value);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Input{" +
                "\n\tisCM=" + isCM +
                ", \n\taccounts=" + accounts.toString() +
                ", \n\tgameBuild=" + gameBuild +
                ", \n\tfightName='" + fightName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Input input = (Input) o;
        return isCM() == input.isCM() &&
                getGameBuild() == input.getGameBuild() &&
                getFightName().equals(input.getFightName()) &&
                accounts.equals(input.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCM(), getGameBuild(), getFightName(), accounts);
    }

}
