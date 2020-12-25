package model;

import java.io.*;
import java.util.Properties;

// a class that manages the datasource.properties file
public class PropertyManager {

    // constructor, throws IOException
    private PropertyManager() {
    }

    // MODIFIES: this
    // EFFECT: from config file, load the properties from the file
    public static Properties getProperties(String file) {
        //"./data/assets/sample.properties"
        Properties properties = new Properties();
        try {
            InputStream i = new FileInputStream(file);

            if (i != null) {
                try {
                    properties.load(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return properties;

    }

    // MODIFIES: this
    // EFFECT: update the config file with given update string value
    public static void update(Properties prop, String property, String update, String file) throws IOException {
        FileOutputStream out = new FileOutputStream("./data/assets/" + file);
        prop.setProperty(property, update);
        prop.store(out, null);
        out.close();
    }
}
