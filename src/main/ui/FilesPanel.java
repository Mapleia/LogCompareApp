package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

// the panel that deals with selecting files
public class FilesPanel extends JPanel {
    private DefaultListModel<File> fileToDo;
    private JList<File> jList;

    // constructor
    public FilesPanel(DefaultListModel<File> filesToDo) {
        this.fileToDo = filesToDo;
        jList = new JList<>(fileToDo);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Drop files here"));

        add(dragNDrop());
        JPanel subPanel = new JPanel();
        subPanel.add(fileChooserButton());
        subPanel.add(removeButton(jList));

        add(subPanel);
    }

    // EFFECT: creates the pane where files can be dragged and dropped, or selected from file explorer
    private JScrollPane dragNDrop() {
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
        return pane;
    }

    // EFFECT: opens file explorer popup so users can add files
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

    // EFFECT: removes selected entries (file(s)) from list
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
}
