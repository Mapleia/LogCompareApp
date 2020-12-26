package ui;

import model.LogCompare;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

// the main panel where users interact with the app
public class MainPanel extends JPanel {
    public static final String DEFAULT_SAVE = new JFileChooser().getFileSystemView().getDefaultDirectory().toString()
            + "/LogCompare/";
    private final DefaultListModel<File> fileToDo = new DefaultListModel<>();
    private final String pass;
    private Connection con;
    private String saveLocation = DEFAULT_SAVE;
    private final JTextArea log = new JTextArea(10, 50);

    public MainPanel(String pass, Connection con) {
        this.pass = pass;
        this.con = con;

        log.setEditable(false);

        add(new FilesPanel(fileToDo));
        add(outputPanel());

    }

    // EFFECT: create a output panel (consist of LOGS, saveLocation input, and comparing button)
    private JPanel outputPanel() {
        JPanel sub = new JPanel();
        sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));

        sub.add(saveLocation());
        sub.add(new JLabel("LOGS"));
        sub.add(new JScrollPane(log));
        sub.add(confirmAndCompare());

        return sub;
    }

    // EFFECT: creates a panel where user can input their save location
    private JPanel saveLocation() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Enter save location"));

        JTextField location = new JTextField();
        location.setPreferredSize(new Dimension(100, 30));
        try {
            location.setText(new File(saveLocation).getCanonicalPath());
        } catch (IOException e) {
            log.append(e.getLocalizedMessage()+ "\n");
            e.printStackTrace();
        }
        location.addActionListener(e -> saveLocation = location.getText());

        JButton button = new JButton("Select Folder From Directory");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File folder = fileChooser.getSelectedFile();
                saveLocation = folder.getAbsolutePath();
                location.setText(saveLocation);
            }
        });

        panel.add(location);
        panel.add(button);

        return panel;
    }

    // EFFECT: creates a button where users can start the log comparisons
    private JButton confirmAndCompare() {
        JButton button = new JButton("Confirm and Compare");
        button.addActionListener(e -> {
            // if needed
            reopenConnection();
            // if needed
            createSaveFolder();

            List<File> files = LogCompare.processFiles(fileToDo.toArray(), log);
            for (File f : files) {
                saveOutputJson(FilenameUtils.getBaseName(f.getName()), f);
            }

            // close connection now that we're done
            try {
                con.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        });

        return button;
    }

    private void reopenConnection() {
        try {
            if (con.isClosed()) {
                con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare", "root", pass);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void createSaveFolder() {
        if (saveLocation.equals(DEFAULT_SAVE)) {
            File theDir = new File(DEFAULT_SAVE);
            if (!theDir.exists()){
                theDir.mkdirs();
            }
        }
    }

    // EFFECT: save JSONObject to .json file to saveLocation
    private void saveOutputJson(String evtc, File json) {
        LogCompare app = new LogCompare(con);
        try {
            JSONObject output = app.compare(json);
            String name = evtc + "_LOGCOMPARE";

            JsonWriter writer = new JsonWriter(saveLocation, name);
            writer.open();
            writer.write(output);
            writer.close();
            log.append(name + " was saved.\n");
        } catch (Exception e) {
            log.append(e.getLocalizedMessage()+ "\n");
        }
    }
}
