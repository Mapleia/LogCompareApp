import model.DBInterface;
import model.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestReader {
    private Input input;
    private JsonReader file;

    @BeforeEach
    public void setup() {
        file = new JsonReader("./data/Arkk/20201217-233716_arkk_kill.json",
                "20201217-233716_arkk_kill");
        input = file.read();
    }

    @Test
    public void testHashCode() {
        JsonReader reader = new JsonReader("./data/Arkk/20201217-233716_arkk_kill.json",
                "20201217-233716_arkk_kill");
        JsonReader reader2 = new JsonReader("./data/Arkk/20201218-013716_arkk_kill.json",
                "20201218-013716_arkk_kill");

        Input file1 = reader.read();
        Input file2 = reader2.read();

        assertEquals(file1.hashCode(), file2.hashCode());
        assertEquals(file1, file2);
    }

    @Test
    public void testDBLogger() {
        DBInterface logger = new DBInterface(input);
        logger.upload();
    }

}
