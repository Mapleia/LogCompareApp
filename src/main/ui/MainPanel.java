package ui;

import model.LogCompare;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONObject;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.io.*;

// the main panel where users interact with the app
public class MainPanel extends JPanel {
    private final DefaultListModel<File> fileToDo;
    private String saveLocation = "./data/save/";
    private final JTextArea log;

    // constructor
    public MainPanel() {
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
            invokeEliteInsight();

            for (int i = 0; i < fileToDo.size(); i++) {
                File file = fileToDo.get(i);
                String name = FilenameUtils.getBaseName(file.getName());
                File[] dirs = getFiles(file);
                if (dirs != null) {
                    for (File jsonFile : dirs) {
                        if (FilenameUtils.getExtension(jsonFile.getName()).equals("json")) {
                            name = FilenameUtils.getBaseName(jsonFile.getName());
                            saveOutputJson(name, jsonFile);
                            break;
                        }
                    }
                } else {
                    log.append(name + " was saved successfully.\n");
                }
            }
        });

        return button;
    }

    // EFFECT: from a given evtc, filter for files with similar names
    private File[] getFiles(File file) {
        String find = FilenameUtils.getBaseName(file.getName()) + "*";
        FileFilter fileFilter = new WildcardFileFilter(find);
        return new File(file.getParent()).listFiles(fileFilter);
    }

    // EFFECT: save JSONObject to .json file to saveLocation
    private void saveOutputJson(String evtc, File json) {
        LogCompare app = new LogCompare(json);
        try {
            JSONObject output = app.compare();
            String name = evtc + "_LOGCOMPARE";
            JsonWriter writer = new JsonWriter(saveLocation, name);
            writer.open();
            writer.write(output);
            writer.close();
            log.append(name + " was skipped.\n");
        } catch (Exception e) {
            e.printStackTrace();
            log.append(e.getLocalizedMessage()+ "\n");
        }
    }

    // EFFECT: create a string array of commands for the EliteInsight parser
    private String[] createCommandArray() {
        String[] result = new String[fileToDo.size() + 3];
        result[0] = "./data/GW2EI/GuildWars2EliteInsights.exe";
        result[1] = "-c";
        try {
            result[2] = "\"" + new File("./data/assets/app.conf").getCanonicalPath() + "\"";
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < fileToDo.size(); i++) {
            File file = fileToDo.get(i);
            try {
                result[3 + i] = "\"" + file.getCanonicalPath() + "\"";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // EFFECT: run GuildWars2EliteInsights.exe
    private void invokeEliteInsight() {
        try {
            String[] params = createCommandArray();


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