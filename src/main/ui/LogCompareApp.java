package ui;

import model.LogCompare;
import model.Output;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

//TODO: documentation
public class LogCompareApp extends JFrame {
    private DefaultListModel<File> fileToDo;
    private String saveLocation = "./data/save/";

    public LogCompareApp() {
        super("LogCompare Gw2");
        Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(DimMax);
        fileToDo = new DefaultListModel<>();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JPanel master = new JPanel();

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.add(dragNDrop());
        subPanel.add(saveLocation());
        master.add(subPanel);
        master.add(confirm());

        add(master);
        pack();
        setVisible(true);
    }

    public static void main(String[] arg) {
        new LogCompareApp();
    }

    private JPanel dragNDrop() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Drop files here"));

        JList<File> jList = new JList(fileToDo);
        JScrollPane pane = new JScrollPane(jList);
        pane.setPreferredSize(new Dimension(500, 700));

        jList.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    if (!droppedFiles.isEmpty()) {
                        for(File file : droppedFiles) {
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
                for(File file : loadedFile) {
                    fileToDo.addElement(file);
                }
            }
        });
        return button;
    }

    private JButton removeButton(JList jList) {
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

    private JPanel confirm() {
        JPanel panel = new JPanel();

        JButton button = new JButton("Confirm and Compare");
        button.addActionListener(e -> {
            invokeEliteInsight();

            for (int i = 0; i < fileToDo.size(); i++) {
                File file = fileToDo.get(i);
                String find = FilenameUtils.getBaseName(file.getName())+"*";
                FileFilter fileFilter = new WildcardFileFilter(find);
                String path = file.getParent();
                File[] dirs = new File(path).listFiles(fileFilter);
                File found = null;
                for (File jsonFile : dirs) {
                    if (FilenameUtils.getExtension(jsonFile.getName()).equals("json")) {
                        found = jsonFile;
                    }
                }
                if (found == null) {
                    //TODO: create log panel
                    JOptionPane.showMessageDialog(null,
                            "JSON File was not found.");
                } else {
                    createOutput(FilenameUtils.getBaseName(file.getName()), found);
                }

            }
        });
        panel.add(button);

        return panel;
    }

    private void createOutput(String evtc, File json) {
        LogCompare app = new LogCompare(json);
        Output output = null;
        try {
            output = app.compare();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        String name = evtc +"_LOGCOMPARE";
        JsonWriter writer = new JsonWriter(saveLocation, name);
        try {
            writer.open();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        writer.write(output);
        writer.close();
        JOptionPane.showMessageDialog(null,
                name + " was saved successfully.");
    }

    private String[] createCommandArray() {
        String[] result = new String[fileToDo.size() +3];
        result[0] = "./data/GW2EI/GuildWars2EliteInsights.exe";
        result[1] = "-c";
        try {
            result[2] = "\"" + new File("./data/app.conf").getCanonicalPath() + "\"";
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < fileToDo.size(); i++) {
            File file = fileToDo.get(i);
            try {
                result[3+i] = "\"" + file.getCanonicalPath() + "\"";
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
                System.out.println(line);
            }

            input.close();
        } catch (IOException e) {
            System.out.println("Exception occurred: " + e);
        }

    }
}
