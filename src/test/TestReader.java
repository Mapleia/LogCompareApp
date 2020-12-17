package test;

import main.model.Input;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

public class TestReader {
    @Test
    public void testMe() {
        JsonReader file = new JsonReader("20200721-003610_sh_kill",
                    "./data/Souless Horror/20200721-003610_sh_kill.json");
        Input i = file.read();
        System.out.println(i.getBuff(740));
    }

}
