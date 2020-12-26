package ui;

import model.FileManager;
import model.LogCompare;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

// the main panel where users interact with the app
public class MainPanel extends JPanel {
    public static final String DEFAULT_SAVE = new JFileChooser().getFileSystemView().getDefaultDirectory().toString()
            + "/LogCompare/";
    private final DefaultListModel<File> fileToDo;
    private final String pass;
    private Connection con;
    private String saveLocation = DEFAULT_SAVE;
    private final JTextArea log;

    public MainPanel(String pass, Connection con) {
        this.pass = pass;
        this.con = con;
        fileToDo = new DefaultListModel<>();
        log = new JTextArea(10, 50);
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
            try {
                if (con.isClosed()) {
                    con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/logcompare", "root", pass);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            if (saveLocation.equals(DEFAULT_SAVE)) {
                File theDir = new File(DEFAULT_SAVE);
                if (!theDir.exists()){
                    theDir.mkdirs();
                }
            }

            FileManager finder = new FileManager(fileToDo.toArray());
            // Sort files into either needing a Gw2EI parse or not.
            Map<String, List<File>> sorted = finder.sortShouldEIParse();

            // Parse all the files that need a json file.
            invokeEliteInsight(sorted.get("toEI"));

            // Add all the new json files
            List<File> files = sorted.get("json");
            FileManager findJson = new FileManager(sorted.get("toEI").toArray());
            files.addAll(findJson.findEIParsedFiles());

            for (File f : files) {
                saveOutputJson(FilenameUtils.getBaseName(f.getName()), f);
            }

            try {
                con.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }

        });

        return button;
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

    // EFFECT: create a string array of commands for the EliteInsight parser
    private String[] createCommandArray(List<File> evtcFiles) {
        String[] result = new String[evtcFiles.size() + 3];
        result[0] = "./data/GW2EI/GuildWars2EliteInsights.exe";
        result[1] = "-c";
        try {
            result[2] = "\"" + new File("./data/assets/app.conf").getCanonicalPath() + "\"";
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < evtcFiles.size(); i++) {
            File file = evtcFiles.get(i);
            try {
                result[3 + i] = "\"" + file.getCanonicalPath() + "\"";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // EFFECT: run GuildWars2EliteInsights.exe
    private void invokeEliteInsight(List<File> evtcFiles) {
        try {
            String[] params = createCommandArray(evtcFiles);

            Process p = Runtime.getRuntime().exec(params);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {
                log.append(line+ "\n");
            }

            input.close();
        } catch (IOException e) {
            log.append("Exception occurred: " + e+ "\n");
        }

    }
}
