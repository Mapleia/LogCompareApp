package ui;

import javax.swing.*;
import java.awt.*;

//TODO: documentation
public class LogCompareApp extends JFrame {
    private JPanel current;
    
    public LogCompareApp() {
        super("LogCompare Gw2");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
            this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setPreferredSize(DimMax);
            if (setup()) {
                current = new MainPanel();
            } else {
                current = new SetupPanel();
            }
            add(current);
            pack();
            setVisible(true);
        }

    }

    private boolean setup() {
        return true;
    }

    public static void main(String[] arg) {
        new LogCompareApp();
    }



}
