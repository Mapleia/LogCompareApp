import model.Input;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestReader {
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

}
