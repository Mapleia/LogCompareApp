package ui;

import javax.swing.*;
import java.awt.*;

public class LogCompareApp extends JFrame {
    private JPanel current;

    // constructor: sets UI to system look, and checks config for proper setup.
    public LogCompareApp() {
        super("LogCompare Gw2");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            java.net.URL url = ClassLoader.getSystemResource("Bane_Signet.png");
            Toolkit kit = Toolkit.getDefaultToolkit();
            Image img = kit.createImage(url);
            setIconImage(img);

            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            current = new MainPanel();
            add(current);
            pack();
            setVisible(true);
        }

    }

    // EFFECT: starts the app
    public static void main(String[] arg) {
        new LogCompareApp();
    }

    // MODIFIES: this
    // EFFECT: changes the current panel of the frame to the one supplied.
    public void next(JPanel current) {
        this.current = current;
        Container contain = getContentPane();
        contain.removeAll();
        add(current);
        contain.revalidate();
        pack();
    }
}
