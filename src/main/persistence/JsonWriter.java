package persistence;

import model.Output;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Taken from JSONSerializationDemo project.
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
// JsonWriter creates and writes a User file.
// The demo is an app that works with a "workroom" and "thingy"/"thingies".
// All methods have references to the JsonWriter (Demo), but it was been reworked to work with Output.
public class JsonWriter {
    private static final int TAB = 4;
    private final String destination;
    private PrintWriter writer;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination, String fileName) {
        this.destination = destination + fileName + ".json";
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of User to file
    public void write(Output output) {
        JSONObject json = output.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
