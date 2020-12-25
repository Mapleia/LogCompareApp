package model;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonReader;

import java.io.File;

// class that does all of the comparisons and gives an output
public class LogCompare {
    private final Input primary;
    public static final String PROPERTIES_PATH = "./data/assets/sample.properties";
    // constructor
    public LogCompare(File file) {
        JsonReader reader = new JsonReader(file.getAbsolutePath(), FilenameUtils.getBaseName(file.getName()));
        primary = reader.read();
    }

    // EFFECT: compare with other logs in the database and create a JSONObject of the percentiles
    public JSONObject compare() {
        DBInterface logger = new DBInterface(primary);
        logger.upload();

        return primary.toJson(logger.uptimePercentile(), logger.dpsPercentiles());
    }
}
