package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// a panel for users to setup their app so it works with their database
public class SetupPanel extends JPanel {
    private final LogCompareApp app;

    // constructor
    public SetupPanel(LogCompareApp app) {
        this.app = app;

        setBorder(new EmptyBorder(30, 15, 30, 15));
        setPreferredSize(new Dimension(300, 100));

        JLabel l = new JLabel("password");
        l.setBorder(new EmptyBorder(0, 10, 0, 10));

        JPasswordField password = new JPasswordField(15);
        TimedPasswordListener tpl = new TimedPasswordListener(password);
        password.getDocument().addDocumentListener(tpl);
        password.addActionListener(e -> {
            canGoToMain(String.valueOf(password.getPassword()));
        });

        JButton button = new JButton("Confirm");
        button.setActionCommand("Confirm");
        button.addActionListener(e -> {
            canGoToMain(String.valueOf(password.getPassword()));
        });

        add(l);
        add(password);
        add(button);
    }

    private void canGoToMain(String pass) {
        if (app.isValid(pass)) {
            app.next();
        } else {
            JOptionPane.showMessageDialog(null, "Password is invalid. Please try again.");
        }
    }

    private class TimedPasswordListener implements DocumentListener, ActionListener {

        private Timer timer = new Timer(3000, this);
        private char echoChar;
        private JPasswordField pwf;

        public TimedPasswordListener(JPasswordField jp) {
            pwf = jp;
            timer.setRepeats(false);
        }

        public void insertUpdate(DocumentEvent e) {
            showText(e);
        }

        public void removeUpdate(DocumentEvent e) {
            showText(e);
        }

        public void changedUpdate(DocumentEvent e) {}

        public void showText(DocumentEvent e) {
            if (0 != pwf.getEchoChar()) {
                echoChar = pwf.getEchoChar();
            }
            pwf.setEchoChar((char) 0);
            timer.restart();
        }

        public void actionPerformed(ActionEvent e) {
            pwf.setEchoChar(echoChar);
        }
    }
}
