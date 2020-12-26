package model;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonReader;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

// class that does all of the comparisons and gives an output
public class LogCompare {
    private Connection con;

    // constructor
    public LogCompare(Connection con) {
        this.con = con;
    }

    // EFFECT: compare with other logs in the database and create a JSONObject of the percentiles
    public JSONObject compare(File file) {
        JsonReader reader = new JsonReader(file.getAbsolutePath(), FilenameUtils.getBaseName(file.getName()));
        Input primary = reader.read();

        DBInterface logger = new DBInterface(primary, con);
        logger.upload();

        return primary.toJson(logger.uptimePercentile(), logger.dpsPercentiles());
    }

    public static List<File> processFiles(Object[] toDo, JTextArea log) {

        FileManager finder = new FileManager(toDo);
        // Sort files into either needing a Gw2EI parse or not.
        Map<String, List<File>> sorted = finder.sortShouldEIParse();

        // Parse all the files that need a json file.
        invokeEliteInsight(sorted.get("toEI"), log);

        // Add all the new json files
        List<File> files = sorted.get("json");
        FileManager findJson = new FileManager(sorted.get("toEI").toArray());
        files.addAll(findJson.findEIParsedFiles());

        return files;
    }

    // EFFECT: create a string array of commands for the EliteInsight parser
    private static String[] createCommandArray(List<File> evtcFiles) {
        String[] result = new String[evtcFiles.size() + 3];
        result[0] = "./data/GW2EI/GuildWars2EliteInsights.exe";
        result[1] = "-c";
        try {
            result[2] = "\"" + new File("./data/assets/app.conf").getCanonicalPath() + "\"";
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < evtcFiles.size(); i++) {
            File file = evtcFiles.get(i);
            try {
                result[3 + i] = "\"" + file.getCanonicalPath() + "\"";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // EFFECT: run GuildWars2EliteInsights.exe
    private static void invokeEliteInsight(List<File> evtcFiles, JTextArea log) {
        try {
            String[] params = createCommandArray(evtcFiles);

            Process p = Runtime.getRuntime().exec(params);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {
                log.append(line+ "\n");
            }

            input.close();
        } catch (IOException e) {
            log.append("Exception occurred: " + e+ "\n");
        }

    }
}
