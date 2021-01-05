import model.Input;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestReader {
    @Test
    public void testHashCode() {
        JsonReader reader = new JsonReader("./data/parsed/20201228-234846_mama_kill.json",
                "20201228-234846_mama_kill.json");
        JsonReader reader2 = new JsonReader("./data/parsed/20201228-234846_mama_kill.json",
                "20201228-234846_mama_kill.json");

        Input file1 = reader.read();
        Input file2 = reader2.read();

        assertEquals(file1.hashCode(), file2.hashCode());
        assertEquals(file1, file2);
    }

    @Test
    public void testRead() {
        JsonReader reader = new JsonReader("./data/parsed/20201228-234846_mama_kill.json",
                "20201228-234846_mama_kill.json");
        Input file1 = reader.read();
        file1.hashCode();
    }

}
