package main.model;

import main.persistence.JsonReader;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogCompare {
    private static final String[] BOONCOLUMNS = new String[]{"b717", "b718", "b719", "b725", "b726", "b740", "b743",
            "b873", "b1122", "b1187", "b17674", "b17675", "b26980", };
    private String folderPath;
    private String fileName;
    private Input primary;
    private List<Input> compareTo;

    // constructor
    public LogCompare(String folderPath, String fileName) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        init();
    }

    // EFFECT: initializes by processing file, adds all the other log files in the same folder
    // to the database if not already there
    private void init() {
        String path = folderPath + fileName + ".json";
        JsonReader reader = new JsonReader(path, fileName);
        reader.read();
        primary = reader.addToInput();

        File folder = new File(folderPath);
        compareTo = new ArrayList<>();

        DBLogger logger = null;
        for (File f : folder.listFiles()) {
            if (f.getName().equals(fileName)) {
                continue;
            }
            JsonReader reader1 = new JsonReader(f.getPath(), f.getName());
            logger = new DBLogger(reader1.read());
            if (!logger.exists()) {
                logger.upload();
            }
        }
        logger.end();

    }

    public Output compare() throws SQLException {
        DBLogger logger = new DBLogger();

        for (String s : BOONCOLUMNS) {
            List<Double> uptimes = new ArrayList<>();
            String query = "SELECT " + s + " FROM " + primary.getTableTitle();
            ResultSet resultSet = logger.sqlQuery(query);
            while(resultSet.next()) {
                uptimes.add(resultSet.getDouble(s));
            }
        }

        //
        return null;
    }
}
