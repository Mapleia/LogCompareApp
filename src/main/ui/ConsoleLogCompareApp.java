package ui;

public class ConsoleLogCompareApp {
/*  private Scanner scanner;
    private String folderPath;
    private String fileName;
    private LogCompare logCompare;

    // constructor
    public ConsoleLogCompareApp() {
        scanner = new Scanner(System.in);
        askFolderFile();
        processFile();
    }

    // EFFECT: ask for the folder path, and the .json file name
    private void askFolderFile() {
        System.out.println("Welcome to the Log-Compare App for Guild War 2!");
        System.out.println("What is the folder for the encounter? (folder specific to boss).");
        folderPath = scanner.nextLine();
        System.out.println("What's the file name you'd like to process?");
        fileName = scanner.nextLine();

    }

    // EFFECT: updates log database, compares the inputted log, then save percentile result file.
    private void processFile() {
        logCompare = new LogCompare(folderPath, fileName);
        Output output = null;
        try {
            output = logCompare.compare();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Please name your file: (no .json needed)");

        String save = scanner.nextLine();
        JsonWriter writer = new JsonWriter(save);
        try {
            writer.open();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.write(output);
        writer.close();
    }

    public static void main(String[] arg) {
        new ConsoleLogCompareApp();
    }*/
}