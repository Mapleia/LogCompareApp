package ui;

import model.PropertyManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

// a panel for users to setup their app so it works with their database
public class SetupPanel extends JPanel {
    private final LogCompareApp app;
    private PropertyManager manager;
    String[] appProps = new String[]{"Port", "Password"};

    // constructor
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

        setup();
    }

    // MODIFIES: manager, app
    // EFFECT: creates a button where properties file is updated with input and moves user to the main panel
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

    // EFFECT: returns true if the password was changed from default.
    public boolean confirmPassChange() {
        return !manager.getProperty("Password").equals("");
    }

    // MODIFIES: this
    // EFFECT: creates a panel to input new password and ports (if necessary)
    private void setup() {
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
            add(mini);
        }
    }
}
