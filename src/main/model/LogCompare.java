package model;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonReader;

import java.io.File;
import java.sql.Connection;

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
}
