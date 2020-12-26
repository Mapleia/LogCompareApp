package model;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
    private Object[] toSort;

    public FileManager(Object[] files) {
        this.toSort = files;
    }

    public Map<String, List<File>> sortShouldEIParse() {
        Map<String, List<File>> sorted = new HashMap<>();

        sorted.put("toEI", new ArrayList<>());
        sorted.put("json", new ArrayList<>());

        for (Object o : toSort) {
            File f = (File) o;
            File[] dirs = getSimilarStartFiles(f);

            File j = getJsonEquivalent(dirs);
            if (j != null) {
                sorted.get("json").add(j);
            } else {
                sorted.get("toEI").add(f);
            }
        }

        return sorted;
    }

    public List<File> findEIParsedFiles() {
        List<File> files = new ArrayList<>();

        for (Object o : toSort) {
            if (toSort == null || toSort.length == 0) {
                break;
            }
            if (o == null) {
                continue;
            }
            File f = (File) o;
            File[] dirs = getSimilarStartFiles(f);
            File j = getJsonEquivalent(dirs);
            if (j != null) {
                files.add(j);
            }
        }

        return files;

    }

    public static File[] getSimilarStartFiles(File file) {
        String find = FilenameUtils.getBaseName(file.getName()) + "*";
        FileFilter fileFilter = new WildcardFileFilter(find);
        return new File("./data/parsed/").listFiles(fileFilter);
    }

    public static File getJsonEquivalent(File[] dirs) {
        if (dirs != null) {
            for (File jsonFile : dirs) {
                if (FilenameUtils.getExtension(jsonFile.getName()).equals("json")) {
                    return jsonFile;
                }
            }
        }
        return null;
    }
}
