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

    public LogCompare(String folderPath, String fileName) {
        this.folderPath = folderPath;
        this.fileName = fileName;
        init();
    }

    private void init() {
        String path = folderPath + fileName + ".json";
        JsonReader reader = new JsonReader(path, fileName);
        primary = reader.read();

        File folder = new File(folderPath);
        compareTo = new ArrayList<>();

        for (File f : folder.listFiles()) {
            JsonReader reader1 = new JsonReader(f.getName(), f.getPath());
            compareTo.add(reader1.read());
        }
    }

    public Output compare() {
        return null;
    }
}
