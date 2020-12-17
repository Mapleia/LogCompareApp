package main.ui;

import main.model.LogCompare;
import main.model.Output;
import persistence.JsonWriter;

import java.util.Scanner;

public class LogCompareApp {
    private Scanner scanner;
    private String folderPath;
    private String fileName;
    private LogCompare compare;

    public LogCompareApp() {
        init();
    }

    private void init() {
        scanner = new Scanner(System.in);
        askFolder();
        processFile();
    }

    private void processFile() {
        compare = new LogCompare(folderPath, fileName);
        Output output = compare.compare();
        System.out.println("Where would you like to save your file?");

        String saveLocation = scanner.nextLine();
        JsonWriter writer = new JsonWriter(output, saveLocation);
        writer.open();
        writer.write();
        writer.close();
    }

    private void askFolder() {
        System.out.println("Welcome to the Log-Compare App for Guild War 2!");
        System.out.println("What is the folder for the encounter? (folder specific to boss).");
        folderPath = scanner.nextLine();
        System.out.println("What's the file name you'd like to process?");
        fileName = scanner.nextLine();

    }
}
