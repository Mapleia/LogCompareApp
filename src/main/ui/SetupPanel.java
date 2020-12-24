package ui;

import model.PropertyManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;



public class SetupPanel extends JPanel {
    private LogCompareApp app;
    private PropertyManager manager;
    String[] appProps = new String[]{"Port", "Password"};

    public SetupPanel(LogCompareApp app) {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 15, 30, 15));
        setPreferredSize(new Dimension(300, 100));

        try {
            manager = new PropertyManager();
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(setup());
    }

    private JButton confirm(String s, JTextField field) {
        JButton button = new JButton("Confirm");
        button.addActionListener(a -> {
            try {
                manager.update(s, field.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(confirmPassChange()) {
                app.next(new MainPanel());
            } else {
                JOptionPane.showMessageDialog(null,
                        "Password was not changed from default. Please change it.");
            }
        });

        return button;
    }

    public boolean confirmPassChange() {
        if (manager.getProperty("Password").equals("DEFAULT")) {
            return false;
        } else {
            return true;
        }
    }

    // EFFECT: creates a panel to input new password and ports (if necessary)
    private JPanel setup() {
        JPanel master = new JPanel();
        master.setLayout(new BoxLayout(master, BoxLayout.Y_AXIS));


        for (String s : appProps) {
            JPanel mini = new JPanel();
            mini.setLayout(new BoxLayout(mini, BoxLayout.X_AXIS));

            JLabel l = new JLabel(s);
            l.setBorder(new EmptyBorder(0, 10, 0, 10));

            JTextField tf = new JTextField(manager.getProperty(s));
            tf.addActionListener(e -> {
                try {
                    manager.update(s, tf.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            mini.add(l);
            mini.add(tf);
            mini.add(confirm(s, tf));

            master.add(mini);
        }

        return master;
    }

}
