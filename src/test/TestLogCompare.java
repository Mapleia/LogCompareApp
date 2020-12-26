import model.DBInterface;
import model.Input;
import model.LogCompare;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.fail;

public class TestLogCompare {

    @Test
    public void compareTest() {
        try (Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare",
                "root", "32314")) {
            LogCompare lc = new LogCompare(con);
            JSONObject obj = lc.compare(new File("./data/sample_logs/20201101-224531_vg_kill.json"));
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
            fail();

        }
    }

    @Test
    public void testDBLogger() {
        JsonReader reader = new JsonReader("./data/sample_logs/20201101-224531_vg_kill.json",
                "20201101-224531_vg_kill.json");
        Input input = reader.read();


        try (Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare",
                "root", "32314")) {
            DBInterface logger = new DBInterface(input, con);
            logger.upload();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDBLoggerSH() {
        try (Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare",
                "root", "32314")) {
            JSONObject o = new LogCompare(con).compare(new File("./data/parsed/20201226-040423_sh_kill.json"));
            System.out.println(o);
        } catch (Exception e) {
            fail();
        }
    }
}
