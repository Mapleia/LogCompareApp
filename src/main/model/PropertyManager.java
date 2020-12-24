package model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// a class that manages the config.properties file
public class PropertyManager {
    private final Properties properties;

    // constructor, throws IOException
    public PropertyManager() throws IOException {
        properties = new Properties();
        getProperties();
    }

    // MODIFIES: this
    // EFFECT: from config file, load the properties from the file
    private void getProperties() throws IOException {
        String file = "config.properties";
        InputStream i = getClass().getClassLoader().getResourceAsStream(file);

        if (i != null) {
            properties.load(i);
        } else {
            throw new FileNotFoundException("property file '" + file + "' not found.");
        }
    }

    // EFFECT: getter of a specific property
    public String getProperty(String s){
        return properties.getProperty(s);
    }

    // MODIFIES: this
    // EFFECT: update the config file with given update string value
    public void update(String property, String update) throws IOException {
        FileOutputStream out = new FileOutputStream("./src/main/resources/config.properties");
        properties.setProperty(property, update);
        properties.store(out, null);
        out.close();
    }
}
