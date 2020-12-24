package model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
    private Properties properties;

    public PropertyManager() throws IOException {
        properties = new Properties();
        getProperties();
    }

    private void getProperties() throws IOException {
        String file = "config.properties";
        InputStream i = getClass().getClassLoader().getResourceAsStream(file);

        if (i != null) {
            properties.load(i);
        } else {
            throw new FileNotFoundException("property file '" + file + "' not found.");
        }
    }
    
    public String getProperty(String s){
        return properties.getProperty(s);
    }

    public void update(String property, String update) throws IOException {
        FileOutputStream out = new FileOutputStream("config.properties");
        properties.setProperty(property, update);
        properties.store(out, null);
        out.close();
    }
}
