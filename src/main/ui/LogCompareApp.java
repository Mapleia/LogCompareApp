package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LogCompareApp extends JFrame {

    // constructor: sets UI to system look, and checks config for proper setup.
    public LogCompareApp() {
        super("LogCompare Gw2");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            setupFrame();

            add(new SetupPanel(this));
            pack();
            setVisible(true);
        }

    }

    private void setupFrame() {
        java.net.URL url = ClassLoader.getSystemResource("Bane_Signet.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image img = kit.createImage(url);
        setIconImage(img);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    // EFFECT: starts the app
    public static void main(String[] arg) {
        new LogCompareApp();
    }

    public boolean next(String text) {
        try {
            Connection con = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/", "root", text);

            Container contain = getContentPane();
            contain.removeAll();
            add(new JScrollPane(new MainPanel(text, con)));
            contain.revalidate();
            pack();

            return true;
        } catch (SQLException se) {
            return false;
        }
    }
}
