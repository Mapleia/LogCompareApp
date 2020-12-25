package model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// a class that manages the datasource.properties file
public class PropertyManager {

    // constructor, throws IOException
    private PropertyManager() {
    }

    // MODIFIES: this
    // EFFECT: from config file, load the properties from the file
    public static Properties getProperties(String file) {
        InputStream i = PropertyManager.class.getClassLoader().getResourceAsStream(file);
        Properties properties = new Properties();

        if (i != null) {
            try {
                properties.load(i);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    // MODIFIES: this
    // EFFECT: update the config file with given update string value
    public static void update(Properties prop, String property, String update, String file) throws IOException {
        FileOutputStream out = new FileOutputStream("./src/main/resources/"+ file);
        prop.setProperty(property, update);
        prop.store(out, null);
        out.close();
    }
}
