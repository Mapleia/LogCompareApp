package main.model;

import persistence.JsonReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogCompare {
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

    public Output compare() {

        return null;
    }
}
