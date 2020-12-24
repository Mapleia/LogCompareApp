package ui;

import model.LogCompare;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONObject;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.util.List;

public class MainPanel extends JPanel {
    private DefaultListModel<File> fileToDo;
    private String saveLocation = "./data/save/";
    private JTextArea log;

    public MainPanel() {
        Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(DimMax);
        fileToDo = new DefaultListModel<>();

        JPanel subPanelLeft = new JPanel();
        subPanelLeft.setLayout(new BoxLayout(subPanelLeft, BoxLayout.Y_AXIS));
        subPanelLeft.add(dragNDrop());
        subPanelLeft.add(saveLocation());

        JPanel subPanelRight = new JPanel();
        subPanelRight.setLayout(new BoxLayout(subPanelRight, BoxLayout.Y_AXIS));
        log = new JTextArea(10, 50);
        log.setEditable(false);
        subPanelRight.add(new JLabel("LOGS"));
        subPanelRight.add(new JScrollPane(log));
        subPanelRight.add(confirm());

        add(subPanelLeft);
        add(subPanelRight);
    }

    private JPanel dragNDrop() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Drop files here"));

        JList<File> jList = new JList<>(fileToDo);
        JScrollPane pane = new JScrollPane(jList);
        pane.setPreferredSize(new Dimension(600, 400));

        jList.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> droppedFiles = (java.util.List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    if (!droppedFiles.isEmpty()) {
                        for (File file : droppedFiles) {
                            fileToDo.addElement(file);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        panel.add(pane);

        JPanel subPanel = new JPanel();
        subPanel.add(fileChooserButton());
        subPanel.add(removeButton(jList));

        panel.add(subPanel);
        return panel;
    }

    private JButton fileChooserButton() {
        JButton button = new JButton("Add From File Explorer");
        button.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] loadedFile = fileChooser.getSelectedFiles();
                for (File file : loadedFile) {
                    fileToDo.addElement(file);
                }
            }
        });
        return button;
    }

    private JButton removeButton(JList<File> jList) {
        JButton button = new JButton("Remove Selected");
        button.addActionListener(e -> {
            List<File> selected = jList.getSelectedValuesList();
            for (File file : selected) {
                fileToDo.removeElement(file);
            }
        });

        return button;
    }

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

    private JButton confirm() {
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
                            File found = jsonFile;
                            name = FilenameUtils.getBaseName(found.getName());
                            saveOutputJson(name, found);
                            break;
                        }
                    }
                } else {
                    updateLog(1, name);
                }
            }
        });

        return button;
    }

    private void updateLog(int status, String name) {
        switch (status) {
            case 0:
                log.append(name + " was saved successfully.\n");
                break;
            default:
                log.append(name + " was skipped.\n");
                break;
        }
    }

    private File[] getFiles(File file) {
        String find = FilenameUtils.getBaseName(file.getName()) + "*";
        FileFilter fileFilter = new WildcardFileFilter(find);
        File[] dirs = new File(file.getParent()).listFiles(fileFilter);
        return dirs;
    }

    private void saveOutputJson(String evtc, File json) {
        LogCompare app = new LogCompare(json);
        try {
            JSONObject output = app.compare();
            String name = evtc + "_LOGCOMPARE";
            JsonWriter writer = new JsonWriter(saveLocation, name);
            writer.open();
            writer.write(output);
            writer.close();
            updateLog(0, name);
        } catch (Exception e) {
            e.printStackTrace();
            log.append(e.getLocalizedMessage()+ "\n");
        }
    }

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
