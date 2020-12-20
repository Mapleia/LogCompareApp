package test;

import main.model.DBLogger;
import main.model.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
    public void testSort() {
        input = file.addToInput();
        System.out.println(input);
    }

    @Test
    public void testDBLogger() {
        file.addToInput();
        DBLogger logger = new DBLogger(input);
        assertFalse(logger.exists());
        logger.upload();
        logger.end();
    }

    @Test
    public void testBuffString() {
//          [743]        = "Aegis",
//          [17675]      = "Aegis",
//          [725]        = "Fury",
//          [740]        = "Might",
//          [717]        = "Protection",
//          [1187]       = "Quickness",
//          [718]        = "Regeneration",
//          [17674]      = "Regeneration",
//          [26980]      = "Resistance",
//          [873]        = "Retaliation",
//          [1122]       = "Stability",
//          [719]        = "Swiftness",
//          [726]        = "Vigor"

        int[] boons = new int[]{717,718,719,725,726,740,743,873,1122,1187,17674,17675,26980};
        String result = "";
        for (int i : boons) {
            result += "b" + i + " DOUBLE(6, 3), ";
        }

        try {
            FileWriter myWriter = new FileWriter(new File("./data/boons.txt"));
            myWriter.write(result);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);

    }

}