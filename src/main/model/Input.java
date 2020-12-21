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
    private String tableTitle;

    private List<Player> players;
    private List<Mechanic> mechanics;
    private Map<String, Integer> accounts;

    // constructor
    public Input(String fileName, boolean isCM, int gameBuild, String fightName) {
        players = new ArrayList<>();
        mechanics = new ArrayList<>();
        accounts = new TreeMap<>();

        this.fileName = fileName;
        this.isCM = isCM;

        this.gameBuild = gameBuild;

        this.fightName = fightName;

        tableTitle = this.fightName.replaceAll("\\s+","");
        if (isCM) {
            tableTitle += "CM";
        }
    }

    // getter
    public int getGameBuild() {
        return gameBuild;
    }

    // getter
    public String getFightName() {
        return fightName;
    }

    // getter
    public boolean isCM() {
        return isCM;
    }

    // getter
    public String getTableTitle() {
        return tableTitle;
    }

    public List<Player> getPlayers() {
        return players;
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

    // EFFECT: add a Player
    public void addPlayer(Player p) {
        if (!players.contains(p)) {
            players.add(p);
        }
    }

    // EFFECT: add to the list of account names
    public void addAccount(String s, int instanceID) {
        accounts.putIfAbsent(s, instanceID);
    }

    // EFFECT: add a mechanic to the list
    public void addMechanic(Mechanic m) {
        if (!mechanics.contains(m)) {
            mechanics.add(m);
        }
    }

    // EFFECT: based on the fields, create a string to be used to set values for the database
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

}
